package de.taschi.bulkgpxviewer.ui.map;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.geo.GpxToJxMapper;
import io.jenetics.jpx.WayPoint;
import lombok.extern.log4j.Log4j2;

/**
 * Highlights a set of points on the map
 */
@Log4j2
public class SelectionPainter implements Painter<JXMapViewer>
{	
	private Image marker;
	private int imageSize;
	private int halfImageSize;
	
	private boolean enabled = false;
	
    private boolean antiAlias = true;
    private Set<GeoPosition> selection = new HashSet<>();

    public SelectionPainter() {
    	try {
			marker = ImageIO.read(ClassLoader.getSystemResource("ui/marker16.png"));
		} catch (Exception e) {
			log.error("Could not load marker image", e);
			marker = null;
		}
    	
    	if (marker != null) {
	    	imageSize = marker.getWidth(null);
	    	halfImageSize = imageSize / 2;
    	} else {
			imageSize = 0;
			halfImageSize = 0;
    	}
    }
    
	@Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (GeoPosition position: selection) {
        	Point2D point = map.getTileFactory().geoToPixel(position, map.getZoom());   	
        	// center points
        	int x = (int) Math.round(point.getX());
        	int y = (int) Math.round(point.getY());
        	        	
        	g.drawImage(marker, 
        			x - halfImageSize, y - halfImageSize, x + halfImageSize, y + halfImageSize, 
        			0, 0, imageSize, imageSize,
        			map);
        }

        g.dispose();
    }
	
	public void setSelection(Set<GeoPosition> selection) {
		this.selection = selection;
	}
	
	public void setSelectionFromWayPoints(Set<WayPoint> selection) {
		log.info("Updating selection, {} items now selected", selection.size());
		this.selection = selection.stream().map(GpxToJxMapper.getInstance()::waypointToGeoPosition).collect(Collectors.toSet());
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
