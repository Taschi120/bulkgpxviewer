package de.taschi.bulkgpxviewer.ui.windowbuilder;

/*-
 * #%L
 * bulkgpxviewer
 * %%
 * Copyright (C) 2021 S. Hillebrand
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpsBoundingBox;
import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.dto.MainWindowSettings;
import de.taschi.bulkgpxviewer.ui.IconHandler;
import de.taschi.bulkgpxviewer.ui.Messages;
import de.taschi.bulkgpxviewer.ui.map.MapPanel;
import de.taschi.bulkgpxviewer.ui.map.MapSelectionHandler;
import de.taschi.bulkgpxviewer.ui.sidepanel.SidePanel;
import io.jenetics.jpx.WayPoint;

public class MainWindow {
	
	private static final Logger LOG = LogManager.getLogger(MainWindow.class);

	private JFrame frame;
	private JSplitPane splitPane;
	private JMenuItem openFolderMenuItem;
	private JMenuItem settingsMenuItem;
	private JMenuItem quitMenuItem;
	private JMenuItem openGithubMenuItem;
	private JMenuItem aboutMenuItem;
	private MapPanel mapPanel;

	private String BASE_TITLE = Messages.getString("MainWindow.WindowTitle"); //$NON-NLS-1$
	private SidePanel sidePanel;
	
	private MainWindowMode currentMode = MainWindowMode.BULK_DISPLAY;
	private EditingPanel editingPanel = new EditingPanel();
	private JPanel leftSideRootPanel;
	private JPanel rightSideRootPanel;
	
	private MapSelectionHandler mapSelectionHandler;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(Messages.getString("MainWindow.WindowTitle")); //$NON-NLS-1$
		frame.setBounds(100, 100, 710, 481);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.75);
		splitPane.setOneTouchExpandable(true);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		leftSideRootPanel = new JPanel();
		splitPane.setLeftComponent(leftSideRootPanel);
		leftSideRootPanel.setLayout(new BorderLayout(0, 0));

		mapPanel = new MapPanel();
		leftSideRootPanel.add(mapPanel, BorderLayout.CENTER);
				
		rightSideRootPanel = new JPanel();
		splitPane.setRightComponent(rightSideRootPanel);
		rightSideRootPanel.setLayout(new BorderLayout(0, 0));
		
		sidePanel = new SidePanel();
		rightSideRootPanel.add(sidePanel);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu(Messages.getString("MainWindow.FileMenu")); //$NON-NLS-1$
		menuBar.add(mnNewMenu);
		
		openFolderMenuItem = new JMenuItem(Messages.getString("MainWindow.OpenFolderMenuItem")); //$NON-NLS-1$
		openFolderMenuItem.addActionListener(this::openFolderEventHandler);
		openFolderMenuItem.setIcon(IconHandler.loadIcon("folder-open-line")); //$NON-NLS-1$
		mnNewMenu.add(openFolderMenuItem);
		
		settingsMenuItem = new JMenuItem(Messages.getString("MainWindow.SettingsMenuItem")); //$NON-NLS-1$
		settingsMenuItem.addActionListener(this::showSettingsWindowEventHandler);
		settingsMenuItem.setIcon(IconHandler.loadIcon("settings-5-line"));
		mnNewMenu.add(settingsMenuItem);
		
		quitMenuItem = new JMenuItem(Messages.getString("MainWindow.QuitMenuItem")); //$NON-NLS-1$
		quitMenuItem.addActionListener(this::closeEventHandler);
		quitMenuItem.setIcon(IconHandler.loadIcon("door-open-line"));
		mnNewMenu.add(quitMenuItem);
		
		JMenu mnNewMenu_1 = new JMenu(Messages.getString("MainWindow.HelpMenuItem")); //$NON-NLS-1$
		menuBar.add(mnNewMenu_1);
		
		openGithubMenuItem = new JMenuItem(Messages.getString("MainWindow.GithubMenuItem")); //$NON-NLS-1$
		openGithubMenuItem.addActionListener(this::openGithubEventHandler);
		mnNewMenu_1.add(openGithubMenuItem);
		
		aboutMenuItem = new JMenuItem(Messages.getString("MainWindow.AboutMenuItem")); //$NON-NLS-1$
		aboutMenuItem.addActionListener(this::aboutEventHandler);
		aboutMenuItem.setIcon(IconHandler.loadIcon("question-line"));
		mnNewMenu_1.add(aboutMenuItem);
		
		
		// TODO move mapSelectionHandler into MapPanel
		mapSelectionHandler = new MapSelectionHandler(mapPanel.getMapKit().getMainMap());
		mapPanel.getMapKit().getMainMap();
		mapSelectionHandler.addSelectionChangeListener(selection -> 
			mapPanel.getSelectionPainter().setSelectionFromWayPoints(selection));
		mapSelectionHandler.addSelectionChangeListener(selection ->
			editingPanel.setSelection(selection));
		
		mapPanel.getMapKit().getMainMap().addMouseListener(mapSelectionHandler);
		
		restoreWindowGeometry();
		loadAndSetIcon();
		
		frame.addWindowListener(new LocalWindowAdapter());
        
        String lastUsedDirectory = SettingsManager.getInstance().getSettings().getLastUsedDirectory();
        
        if (!StringUtils.isEmpty(lastUsedDirectory)) {
        	setCrawlDirectory(new File(lastUsedDirectory));
        }
		
		setZoomAndLocation();
	}
	
	public void startEditMode(GpxViewerTrack trackToEdit) {
		sidePanel.setEnabled(false);
		if (editingPanel == null) {
			editingPanel = new EditingPanel();
		}
		
		// show editing controls
		leftSideRootPanel.add(editingPanel, BorderLayout.SOUTH);
		leftSideRootPanel.revalidate();
		
		editingPanel.setTrack(trackToEdit);
		editingPanel.setSelection(Collections.<WayPoint>emptySet());
		
		// set up map display
		mapPanel.getSelectionPainter().setEnabled(true);
		mapPanel.getSelectionPainter().setTrack(trackToEdit);
		mapPanel.getRoutesPainter().setProvider(mapPanel.getRoutesPainter().getSingleTrackProvider(trackToEdit));
		
		// enable map listener
		mapSelectionHandler.activate(trackToEdit);
		
		currentMode = MainWindowMode.EDITING;
	}

	private void restoreWindowGeometry() {
		MainWindowSettings settings = SettingsManager.getInstance().getSettings().getMainWindowSettings();
		
		frame.setLocation(settings.getX(), settings.getY());
		frame.setSize(settings.getWidth(), settings.getHeight());
		
		if (settings.isMaximized()) {
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		}
	}
	
	private void loadAndSetIcon() {
		try {
			Image image = ImageIO.read(getClass().getResource("/icon_256.png")); //$NON-NLS-1$
			frame.setIconImage(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setZoomAndLocation() {
		List<GpxViewerTrack> tracks = LoadedFileManager.getInstance().getLoadedTracks();
		
		JXMapKit mapKit = mapPanel.getMapKit();
		
		if (tracks.isEmpty()) {
			// default location
			mapKit.setZoom(8);
			mapKit.setAddressLocation(new GeoPosition(50.11, 8.68));
		} else {
			GpsBoundingBox bb = new GpsBoundingBox();
			Set<GeoPosition> allPositions = new LinkedHashSet<GeoPosition>();
			
			for(List<GeoPosition> track: tracks) {
				allPositions.addAll(track);
				for(GeoPosition pos : track) {
					bb.clamp(pos);
				}
			}
			
			mapKit.setAddressLocation(new GeoPosition(bb.getCenterLat(), bb.getCenterLong()));
			mapKit.setZoom(8);
		}
	}

	public void setCrawlDirectory(File selectedFile) {
		try {
			LoadedFileManager.getInstance().clearAndLoadAllFromDirectory(selectedFile.toPath());

	        frame.setTitle(BASE_TITLE + Messages.getString("MainWindow.WindowTitleSpace") + selectedFile.getPath()); //$NON-NLS-1$
	        SettingsManager.getInstance().getSettings().setLastUsedDirectory(selectedFile.getCanonicalPath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, Messages.getString("MainWindow.ErrorWhileLoadingGPXFiles") + selectedFile.getAbsolutePath()); //$NON-NLS-1$
			LOG.error("Error while loading GPX files from folder " + selectedFile.getAbsolutePath(), e); //$NON-NLS-1$
		}
	}
	
	public void forceSettingsRefresh() {
		mapPanel.repaint();
		sidePanel.updateModel();
	}
	
	private void writeWindowStateToSettings() {
		MainWindowSettings settings = SettingsManager.getInstance().getSettings().getMainWindowSettings();
		settings.setWidth(frame.getWidth());
		settings.setHeight(frame.getHeight());
		settings.setX(frame.getX());
		settings.setY(frame.getY());
		settings.setMaximized((frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public JFrame getFrame() {
		return frame;
	}
	
	private void showSettingsWindowEventHandler(ActionEvent evt) {
		SettingsWindow s = new SettingsWindow(this);
		s.setVisible(true);
	}
	
	private void aboutEventHandler(ActionEvent evt) {
		new InfoDialog().setVisible(true);
	}
	
	private void openFolderEventHandler(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setVisible(true);
		
		try {
			int rc = chooser.showOpenDialog(frame);
			if (rc == JFileChooser.APPROVE_OPTION) {
				setCrawlDirectory(chooser.getSelectedFile());
			}
		} catch (HeadlessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void openGithubEventHandler(ActionEvent evt) {
		try {
			Desktop.getDesktop().browse(URI.create("https://github.com/Taschi120/bulkgpxviewer")); //$NON-NLS-1$
		} catch (IOException e) {
			LOG.error("Error while trying to open GitHub site in system browser", e); //$NON-NLS-1$
			JOptionPane.showMessageDialog(frame, Messages.getString("MainWindow.CouldNotOpenBrowser"), Messages.getString("MainWindow.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void closeEventHandler(ActionEvent evt) {
		writeWindowStateToSettings();
		SettingsManager.getInstance().saveSettings();
		LOG.info("Closing application by user request."); //$NON-NLS-1$
		System.exit(0);
	}
	
	private class LocalWindowAdapter extends WindowAdapter {
		@Override
    	public void windowClosing(WindowEvent e) {
    		closeEventHandler(null);
    	}
	}
}

