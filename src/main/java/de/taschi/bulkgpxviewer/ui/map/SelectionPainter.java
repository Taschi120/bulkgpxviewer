package de.taschi.bulkgpxviewer.ui.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
	private static final int CIRCLE_RADIUS = MapSelectionHandler.SELECTION_THRESHOLD;
	private static final int BORDER_RADIUS = CIRCLE_RADIUS + 2;
	
	private boolean enabled = false;
	
    private boolean antiAlias = true;
    private Set<GeoPosition> selection = new HashSet<>();

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
        	        	
        	// draw outer circle
        	g.setColor(Color.BLACK);
        	drawCircle(g, x, y, BORDER_RADIUS);
        	
        	// draw inner circle
        	g.setColor(Color.WHITE);
        	drawCircle(g, x, y, CIRCLE_RADIUS);
        }

        g.dispose();
    }
	
	/**
	 * draw a circle
	 * @param g graphics object to paint on
	 * @param x x coordinate of the center
	 * @param y y coordinate of the center
	 * @param r radius
	 */
	private void drawCircle(Graphics2D g, int x, int y, int r) {
		g.fillOval(x - r, y - r, 2*r, 2*r);
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
