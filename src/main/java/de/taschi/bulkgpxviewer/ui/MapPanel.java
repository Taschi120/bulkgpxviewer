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

import java.awt.BorderLayout;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpsBoundingBox;
import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;

/**
 * A JPanel which wraps the {@link JXMapKit} instance
 */
public class MapPanel extends JPanel {
	
	private static final long serialVersionUID = -1865773680018087837L;

	private final JXMapKit mapKit;
	
	private final TracksPainter routesPainter;
			
	public MapPanel() {
		super();
		
		setLayout(new BorderLayout());
		
		mapKit = new JXMapKit();
        this.add(mapKit, BorderLayout.CENTER);

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);
                
        routesPainter = new TracksPainter();
        mapKit.getMainMap().setOverlayPainter(routesPainter);
        
        LoadedFileManager.getInstance().addChangeListener(mapKit::repaint);
        autoSetZoomAndLocation();
	}

	/**
	 * Moves the location to the center point of the area covered by the currently loaded tracks.
	 * 
	 * TODO Better logic, including for setting the zoom level, is needed here.
	 */
	public void autoSetZoomAndLocation() {
		List<GpxViewerTrack> tracks = LoadedFileManager.getInstance().getLoadedTracks();
		
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
	
	public void forceSettingsRefresh() {
		repaint();
	}
	
	public JXMapKit getMapKit() {
		return mapKit;
	}
	
	public TracksPainter getRoutesPainter() {
		return routesPainter;
	}
}
