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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper;
import de.taschi.bulkgpxviewer.geo.GpxFile;
import de.taschi.bulkgpxviewer.ui.TrackColorUtil;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

/**
 * Paints a route. This class has been shamelessly stolen from the JXMapViewer samples 
 * at {@link https://github.com/msteiger/jxmapviewer2/blob/master/examples/src/sample2_waypoints/RoutePainter.java}
 * and slightly adapted.
 *
 * @author Martin Steiger (original), S. Hillebrand (adapted)
 */
public class TracksPainter implements Painter<JXMapViewer>
{
	
    private boolean antiAlias = true;
    
    private Supplier<Collection<GpxFile>> provider = getAllRouteProvider();
    
    /**
     * @param track the track
     */
    public TracksPainter()
    {
    }

    public Supplier<Collection<GpxFile>> getAllRouteProvider() {
		return () -> LoadedFileManager.getInstance().getLoadedTracks();
	}
    
    public Supplier<Collection<GpxFile>> getSingleTrackProvider(GpxFile track) {
    	return () -> Arrays.asList(track);
    }

	@Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        for (GpxFile track: provider.get()) {
	        // do the drawing
	        g.setColor(Color.BLACK);
	        g.setStroke(new BasicStroke(4));
	
	        drawGpxFile(g, map, track);
	
	    	Color color = TrackColorUtil.getInstance().getColorForTrack(track);
	        // do the drawing again
	        g.setColor(color);
	        g.setStroke(new BasicStroke(2));
	
	        drawGpxFile(g, map, track);
        }

        g.dispose();
    }
	
	private void drawGpxFile(Graphics2D g, JXMapViewer map, GpxFile gpxViewerTrack) {
		for(Track track: gpxViewerTrack.getGpx().getTracks()) {
			drawTrack(g, map, track);
		}
	}

    private void drawTrack(Graphics2D g, JXMapViewer map, Track track) {
		for(TrackSegment segment : track.getSegments()) {
			drawSegment(g, map, segment);
		}
	}

    private void drawSegment(Graphics2D g, JXMapViewer map, TrackSegment segment) {
        int lastX = 0;
        int lastY = 0;
        
        boolean first = true;
        
        var wayPoints = segment.getPoints();
        
        // FIXME this is a horrible performance drain, refactor ASAP
        var geoPositions = wayPoints.stream()
        		.map(GpxToJxMapper.getInstance()::waypointToGeoPosition)
        		.collect(Collectors.toList());

        for (GeoPosition gp : geoPositions) {
            // convert geo-coordinate to world bitmap pixel
            Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

            if (first) {
                first = false;
            }
            else {
                g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
            }

            lastX = (int) pt.getX();
            lastY = (int) pt.getY();
        }
    }
    
    /**
     * Changes the track provider. Remember that repaint() needs to be called afterwards on the
     * JXMapViewer or JXMapKit instance.
     * @param provider
     */
    public void setProvider(Supplier<Collection<GpxFile>> provider) {
    	this.provider = provider;
    }
}
