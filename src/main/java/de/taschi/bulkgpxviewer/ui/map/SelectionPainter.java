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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper;
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
    
    private GpxFile track = null;
    
    private List<WayPoint> selection = Collections.<WayPoint>emptyList();
    private List<GeoPosition> selectionAsGeoPositions = Collections.<GeoPosition>emptyList();

    public SelectionPainter() {
    	try {
    		markerNeutral = ImageIO.read(ClassLoader.getSystemResource("ui/marker16.png")); //$NON-NLS-1$
			markerA = ImageIO.read(ClassLoader.getSystemResource("ui/marker_a16.png")); //$NON-NLS-1$
			markerB = ImageIO.read(ClassLoader.getSystemResource("ui/marker_b16.png")); //$NON-NLS-1$
		} catch (Exception e) {
			log.error("Could not load marker image", e); //$NON-NLS-1$
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
	
	public void setTrack(GpxFile track) {
		this.track = track;
	}

	public void setSelectionFromWayPoints(Set<WayPoint> selection) {
		log.info("Updating selection, {} items now selected", selection.size()); //$NON-NLS-1$
		this.selection = new ArrayList<>(selection);
		this.selection.sort((o1, o2) -> 
			track.indexOfWayPoint(o2).orElseThrow().compareTo(track.indexOfWayPoint(o1).orElseThrow()));
		
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
