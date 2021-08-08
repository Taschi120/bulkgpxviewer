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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.List;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.settings.ColorConverter;
import de.taschi.bulkgpxviewer.settings.SettingsManager;

/**
 * Paints a route. This class has been shamelessly stolen from the JXMapViewer samples 
 * at {@link https://github.com/msteiger/jxmapviewer2/blob/master/examples/src/sample2_waypoints/RoutePainter.java}
 * and slightly adapted.
 *
 * @author Martin Steiger
 */
public class RoutesPainter implements Painter<JXMapViewer>
{
    private boolean antiAlias = true;
    
    private List<Color> colors;

    private List<List<GeoPosition>> tracks;

    /**
     * @param track the track
     */
    public RoutesPainter(List<List<GeoPosition>> tracks)
    {
        this.tracks = tracks;
        refreshColorList();
    }
    
    public void refreshColorList() {
        colors = ColorConverter.convertToAwt(SettingsManager.getInstance().getSettings().getRouteColors());
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h)
    {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        int colorIdx = 0;
        for (List<GeoPosition> track: tracks) {
	        // do the drawing
	        g.setColor(Color.BLACK);
	        g.setStroke(new BasicStroke(4));
	
	        drawRoute(g, map, track);
	
	    	Color color = colors.get(colorIdx);
	    	colorIdx = (colorIdx + 1) % colors.size();
	        // do the drawing again
	        g.setColor(color);
	        g.setStroke(new BasicStroke(2));
	
	        drawRoute(g, map, track);
        }

        g.dispose();
    }

    /**
     * @param g the graphics object
     * @param map the map
     */
    private void drawRoute(Graphics2D g, JXMapViewer map, List<GeoPosition> track)
    {
        int lastX = 0;
        int lastY = 0;
        
        boolean first = true;

        for (GeoPosition gp : track)
        {
            // convert geo-coordinate to world bitmap pixel
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

            if (first)
            {
                first = false;
            }
            else
            {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }

	public void setTracks(List<List<GeoPosition>> tracks) {
		this.tracks = tracks;
	}
	
}
