package de.taschi.bulkgpxviewer.ui.windowbuilder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.gpx.GpsBoundingBox;
import de.taschi.bulkgpxviewer.gpx.GpxViewerTrack;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.dto.MainWindowSettings;
import de.taschi.bulkgpxviewer.ui.MapPanel;

public class MainWindow {

	private JFrame frame;
	private JSplitPane splitPane;
	private JMenuItem openFolderMenuItem;
	private JMenuItem settingsMenuItem;
	private JMenuItem quitMenuItem;
	private JMenuItem openGithubMenuItem;
	private JMenuItem aboutMenuItem;
	private MapPanel mapPanel;

	private String BASE_TITLE = "Bulk GPX Viewer";
	
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
		frame.setTitle("Bulk GPX Viewer");
		frame.setBounds(100, 100, 710, 481);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.75);
		splitPane.setOneTouchExpandable(true);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		mapPanel = new MapPanel();
		panel.add(mapPanel, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		openFolderMenuItem = new JMenuItem("Open folder...");
		openFolderMenuItem.addActionListener(this::openFolderEventHandler);
		mnNewMenu.add(openFolderMenuItem);
		
		settingsMenuItem = new JMenuItem("Settings");
		settingsMenuItem.addActionListener(this::showSettingsWindowEventHandler);
		mnNewMenu.add(settingsMenuItem);
		
		quitMenuItem = new JMenuItem("Quit");
		mnNewMenu.add(quitMenuItem);
		
		JMenu mnNewMenu_1 = new JMenu("Help");
		menuBar.add(mnNewMenu_1);
		
		openGithubMenuItem = new JMenuItem("Bulk GPX Viewer on Github");
		mnNewMenu_1.add(openGithubMenuItem);
		
		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(this::aboutEventHandler);
		mnNewMenu_1.add(aboutMenuItem);
		
		restoreWindowGeometry();
		loadAndSetIcon();
		
		frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		writeWindowStateToSettings();
        		SettingsManager.getInstance().saveSettings();
        	}
        });
		
		LoadedFileManager.getInstance().addChangeListener(() -> {
        	mapPanel.getMapKit().repaint();
        });
        
        String lastUsedDirectory = SettingsManager.getInstance().getSettings().getLastUsedDirectory();
        
        if (!StringUtils.isEmpty(lastUsedDirectory)) {
        	setCrawlDirectory(new File(lastUsedDirectory));
        }
		
		setZoomAndLocation();
		
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
			Image image = ImageIO.read(getClass().getResource("/icon_256.png"));
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

	        frame.setTitle(BASE_TITLE + " " + selectedFile.getPath());
	        SettingsManager.getInstance().getSettings().setLastUsedDirectory(selectedFile.getCanonicalPath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Error while loading GPX files from folder " + selectedFile.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	public void forceSettingsRefresh() {
		mapPanel.getRoutesPainter().refreshColorList();
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
	
	private void showSettingsWindowEventHandler(ActionEvent evt) {
		SettingsWindow s = new SettingsWindow(this);
		s.setVisible(true);
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

	public JFrame getFrame() {
		return frame;
	}
	
	private void aboutEventHandler(ActionEvent evt) {
		new InfoDialog().setVisible(true);
	}
	
}
