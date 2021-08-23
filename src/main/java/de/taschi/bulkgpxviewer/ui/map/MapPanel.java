package de.taschi.bulkgpxviewer.ui.map;

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
import java.util.List;

import javax.swing.JPanel;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpsBoundingBox;
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper;
import de.taschi.bulkgpxviewer.geo.GpxFile;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

/**
 * A JPanel which wraps the {@link JXMapKit} instance
 */
public class MapPanel extends JPanel {
	
	private static final long serialVersionUID = -1865773680018087837L;

	private final JXMapKit mapKit;
	
	private final TracksPainter routesPainter;
	private final SelectionPainter selectionPainter;
	private final CompositePainter compositePainter;
			
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
        selectionPainter = new SelectionPainter();
        
        compositePainter = new CompositePainter();
        compositePainter.addPainter(routesPainter);
        compositePainter.addPainter(selectionPainter);
        
        mapKit.getMainMap().setOverlayPainter(compositePainter);
        
        LoadedFileManager.getInstance().addChangeListener(mapKit::repaint);
        autoSetZoomAndLocation();
	}

	/**
	 * Moves the location to the center point of the area covered by the currently loaded tracks.
	 * 
	 * TODO Better logic, including for setting the zoom level, is needed here.
	 */
	public void autoSetZoomAndLocation() {
		List<GpxFile> tracks = LoadedFileManager.getInstance().getLoadedTracks();
		
		if (tracks.isEmpty()) {
			// default location
			mapKit.setZoom(8);
	        mapKit.setAddressLocation(new GeoPosition(50.11, 8.68));
		} else {
			var bb = new GpsBoundingBox();
			
			tracks.stream().map(GpxFile::getGpx)
					.flatMap(GPX::tracks)
					.flatMap(Track::segments)
					.flatMap(TrackSegment::points)
					.map(GpxToJxMapper.getInstance()::waypointToGeoPosition)
					.forEach(bb::clamp);
			
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
	
	public SelectionPainter getSelectionPainter() {
		return selectionPainter;
	}
}
