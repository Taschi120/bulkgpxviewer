package de.taschi.bulkgpxviewer.ui;

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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import de.taschi.bulkgpxviewer.gpx.GpsBoundingBox;
import de.taschi.bulkgpxviewer.gpx.GpxFileUtil;
import de.taschi.bulkgpxviewer.gpx.GpxToJxMapper;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.dto.MainWindowSettings;
import io.jenetics.jpx.WayPoint;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -4091111218882475584L;
	
	private final JXMapKit mapKit;
	
	private final RoutesPainter routesPainter;
	
	private final MenuBar menuBar;
	
	private static final String BASE_TITLE = "MapView";
	
	private List<List<GeoPosition>> displayedTracks = new ArrayList<>();
	
	public MainWindow() {
		super(BASE_TITLE);
		
		MainWindowSettings settings = SettingsManager.getInstance().getSettings().getMainWindowSettings();
		
		setSize(settings.getWidth(), settings.getHeight());
		setMaximizedBounds(getBounds());
		
		if (settings.isMaximized()) {
			setExtendedState(MAXIMIZED_BOTH);
		} else {
			setExtendedState(0);
		}
		
        menuBar = new MenuBar(this);
        setJMenuBar(menuBar);
		
		mapKit = new JXMapKit();
        getContentPane().add(mapKit);

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		writeWindowStateToSettings();
        		SettingsManager.getInstance().saveSettings();
        	}
        });
        
        List<List<WayPoint>> tracks = new ArrayList<List<WayPoint>>(0);
        displayedTracks = GpxToJxMapper.getInstance().waypointTracksToGeoPositionTracks(tracks);
        routesPainter = new RoutesPainter(displayedTracks);
        mapKit.getMainMap().setOverlayPainter(routesPainter);
        
        String lastUsedDirectory = SettingsManager.getInstance().getSettings().getLastUsedDirectory();
        
        if (!StringUtils.isEmpty(lastUsedDirectory)) {
        	setCrawlDirectory(new File(lastUsedDirectory));
        }
        
        setZoomAndLocation();
	}

	private void setZoomAndLocation() {
		if (displayedTracks.isEmpty()) {
			// default location
			mapKit.setZoom(8);
	        mapKit.setAddressLocation(new GeoPosition(50.11, 8.68));
		} else {
			GpsBoundingBox bb = new GpsBoundingBox();
			Set<GeoPosition> allPositions = new LinkedHashSet<GeoPosition>();
			
			for(List<GeoPosition> track: displayedTracks) {
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
	        List<List<WayPoint>> tracks = GpxFileUtil.getInstance().loadAllGpxInFolder(selectedFile.toPath());
	        displayedTracks = GpxToJxMapper.getInstance().waypointTracksToGeoPositionTracks(tracks);
	        routesPainter.setTracks(displayedTracks);
	        setTitle(BASE_TITLE + " " + selectedFile.getPath());
	        
	        SettingsManager.getInstance().getSettings().setLastUsedDirectory(selectedFile.getCanonicalPath());
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(this, "Error while loading GPX files from folder " + selectedFile.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	public void forceSettingsRefresh() {
		routesPainter.refreshColorList();
	}
	
	private void writeWindowStateToSettings() {
		MainWindowSettings settings = SettingsManager.getInstance().getSettings().getMainWindowSettings();
		settings.setWidth(getWidth());
		settings.setHeight(getHeight());
		settings.setX(getX());
		settings.setY(getY());
		settings.setMaximized((getExtendedState() & MAXIMIZED_BOTH) != 0);
	}
}
