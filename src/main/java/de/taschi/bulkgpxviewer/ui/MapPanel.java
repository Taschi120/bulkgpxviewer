package de.taschi.bulkgpxviewer.ui;

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
import de.taschi.bulkgpxviewer.gpx.GpsBoundingBox;
import de.taschi.bulkgpxviewer.gpx.GpxViewerTrack;

/**
 * A JPanel which wraps the {@link JXMapKit} instance
 */
public class MapPanel extends JPanel {
	
	private static final long serialVersionUID = -1865773680018087837L;

	private final JXMapKit mapKit;
	
	private final RoutesPainter routesPainter;
			
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
                
        routesPainter = new RoutesPainter();
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
		routesPainter.refreshColorList();
	}
	
	public JXMapKit getMapKit() {
		return mapKit;
	}
	
	public RoutesPainter getRoutesPainter() {
		return routesPainter;
	}
}
