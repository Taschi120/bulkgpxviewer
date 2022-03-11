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

import de.taschi.bulkgpxviewer.files.GpxFile;
import io.jenetics.jpx.WayPoint;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This listener handles the selection of wapypoints on the map during editing mode.
 */
public class MapSelectionHandler extends MouseAdapter {
	
	/**
	 * How far away from the waypoint the cursor can be when selecting a waypoint, in pixels
	 */
	public static final int SELECTION_THRESHOLD = 20;
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(MapSelectionHandler.class);

	private final JXMapViewer target;
	
	private boolean active = false;
	private GpxFile track = null;
	
	/** currently selected points */
	private Set<WayPoint> selectedPoints = new HashSet<WayPoint>();
	
	/** unmodifiable copy of selectedPoints, cached for performance reasons */
	private Set<WayPoint> unmodifiableSelectedPoints = Collections.unmodifiableSet(selectedPoints);
	
	private List<WaypointSelectionChangeListener> selectionChangeListeners = new ArrayList<>();
	
	public MapSelectionHandler(JXMapViewer target) {
		this.target = target;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!active) {
			return;
		}
		
		WayPoint wp = getNearestWaypointToMouseCoords(e.getPoint());
		if (wp != null) {
			if (selectedPoints.contains(wp)) {
				log.info("Deselecting waypoint at {}|{}", wp.getLatitude(), wp.getLongitude()); //$NON-NLS-1$
				selectedPoints.remove(wp);
			} else if (selectedPoints.size() < 2) {
				log.info("Selecting waypoint at {}|{}", wp.getLatitude(), wp.getLongitude()); //$NON-NLS-1$
				selectedPoints.add(wp);
			}
			
			unmodifiableSelectedPoints = Collections.unmodifiableSet(selectedPoints);
			fireSelectionChangeListeners();
		}
	}
	
	private void fireSelectionChangeListeners() {
		SwingUtilities.invokeLater(() -> {
			selectionChangeListeners.stream().forEach(it -> it.selectionChanged(selectedPoints));
		});
	}

	private WayPoint getNearestWaypointToMouseCoords(Point2D mouse) {
		
		var points = track.getAllWayPoints();
		var geoPositions = track.getAllGeoPositions();
		
		if (points.isEmpty()) {
			log.warn("Track is empty, cannot find nearest point"); //$NON-NLS-1$
			return null;
		}
		
		WayPoint nearestWaypoint = points.get(0);
		double nearestDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < points.size(); i++) {
			WayPoint comparison = points.get(i);
			GeoPosition position = geoPositions.get(i);
			
			var point = target.convertGeoPositionToPoint(position);
			double distance = point.distance(mouse);
			
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearestWaypoint = comparison;
			}
		}
		
		if (nearestDistance <= SELECTION_THRESHOLD) {
			return nearestWaypoint;
		} else {
			return null;
		}
	}

	/**
	 * Activates this listener
	 * @param track the track which is being edited
	 */
	public void activate(GpxFile track) {
		log.info("Entering edit mode for track {}", track.getFileName()); //$NON-NLS-1$
		this.track = track;
		active = true;
	}
	
	/**
	 * Deactivates this listener
	 */
	public void deactivate() {
		log.info("Exiting edit mode"); //$NON-NLS-1$
		track = null;
		active = false;
		selectedPoints = Collections.emptySet();
	}
	
	public Set<WayPoint> getSelectedWayPoints() {
		return unmodifiableSelectedPoints;
	}
	
	public void addSelectionChangeListener(WaypointSelectionChangeListener listener) {
		selectionChangeListeners.add(listener);
	}
	
	public boolean removeSelectionChangeListener(WaypointSelectionChangeListener listener) {
		return selectionChangeListeners.remove(listener);
	}

	/**
	 * Change the current selection
	 * @param selection
	 */
	public void setSelection(List<WayPoint> selection) {
		selectedPoints = selection.stream().distinct().collect(Collectors.toSet());
		unmodifiableSelectedPoints = Collections.unmodifiableSet(selectedPoints);
		
		fireSelectionChangeListeners();
	}
	
}
