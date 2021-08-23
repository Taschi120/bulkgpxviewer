package de.taschi.bulkgpxviewer.ui.map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.geo.GpxToJxMapper;
import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import io.jenetics.jpx.WayPoint;
import lombok.extern.log4j.Log4j2;

/**
 * Highlights a set of points on the map
 */
@Log4j2
public class SelectionPainter implements Painter<JXMapViewer>
{	
	private Image markerA;
	private Image markerB;
	private Image markerNeutral;
	
	private int imageSize;
	private int halfImageSize;
	
	private boolean enabled = false;
	
    private boolean antiAlias = true;
    
    private GpxViewerTrack track = null;
    
    private List<WayPoint> selection = Collections.<WayPoint>emptyList();
    private List<GeoPosition> selectionAsGeoPositions = Collections.<GeoPosition>emptyList();

    public SelectionPainter() {
    	try {
    		markerNeutral = ImageIO.read(ClassLoader.getSystemResource("ui/marker16.png"));
			markerA = ImageIO.read(ClassLoader.getSystemResource("ui/marker_a16.png"));
			markerB = ImageIO.read(ClassLoader.getSystemResource("ui/marker_b16.png"));
		} catch (Exception e) {
			log.error("Could not load marker image", e);
			markerA = null;
			markerB = null;
		}
    	
    	// Assuming both markers have the same size.
    	if (markerA != null) {
	    	imageSize = markerA.getWidth(null);
	    	halfImageSize = imageSize / 2;
    	} else {
			imageSize = 0;
			halfImageSize = 0;
    	}
    }
    
	@Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		
		if (track == null || !enabled) return;
		
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(selection.size() == 1) {
        	var position = selectionAsGeoPositions.get(0);
        	var point = map.getTileFactory().geoToPixel(position, map.getZoom());   	
        	// center points
        	var x = (int) Math.round(point.getX());
        	var y = (int) Math.round(point.getY());
        	        	
        	paintMarker(g, map, x, y, markerNeutral);
        }
        
        else if(selection.size() == 2) {
        	var position = selectionAsGeoPositions.get(0);
        	var point = map.getTileFactory().geoToPixel(position, map.getZoom());
        	var x = (int) Math.round(point.getX());
        	var y = (int) Math.round(point.getY());
        	        	
        	paintMarker(g, map, x, y, markerA);
        	
        	position = selectionAsGeoPositions.get(1);
        	point = map.getTileFactory().geoToPixel(position, map.getZoom());
        	x = (int) Math.round(point.getX());
        	y = (int) Math.round(point.getY());
        	        	
        	paintMarker(g, map, x, y, markerB);
        }

        g.dispose();
    }
	
	private void paintMarker(Graphics2D g, JXMapViewer map, int x, int y, Image marker) {
		g.drawImage(marker, 
    			x - halfImageSize, y - halfImageSize, x + halfImageSize, y + halfImageSize, 
    			0, 0, imageSize, imageSize,
    			map);
	}
	
	public void setTrack(GpxViewerTrack track) {
		this.track = track;
	}

	public void setSelectionFromWayPoints(Set<WayPoint> selection) {
		log.info("Updating selection, {} items now selected", selection.size());
		this.selection = new ArrayList<>(selection);
		this.selection.sort((o1, o2) -> track.indexOfWayPoint(o2) - track.indexOfWayPoint(o1));
		
		selectionAsGeoPositions = this.selection.stream()
				.map(GpxToJxMapper.getInstance()::waypointToGeoPosition)
				.collect(Collectors.toList());
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
