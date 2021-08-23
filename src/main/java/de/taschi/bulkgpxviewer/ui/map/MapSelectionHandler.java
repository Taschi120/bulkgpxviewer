package de.taschi.bulkgpxviewer.ui.map;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import io.jenetics.jpx.WayPoint;
import lombok.extern.log4j.Log4j2;

/**
 * This listener handles the selection of wapypoints on the map during editing mode.
 */
@Log4j2
public class MapSelectionHandler extends MouseAdapter {
	
	/**
	 * How far away from the waypoint the cursor can be when selecting a waypoint, in pixels
	 */
	public static final int SELECTION_THRESHOLD = 20;
	
	private final JXMapViewer target;
	
	private boolean active = false;
	private GpxViewerTrack track = null;
	
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
				log.info("Deselecting waypoint at {}|{}", wp.getLatitude(), wp.getLongitude());
				selectedPoints.remove(wp);
			} else {
				log.info("Selecting waypoint at {}|{}", wp.getLatitude(), wp.getLongitude());
				selectedPoints.add(wp);
			}
			
			unmodifiableSelectedPoints = Collections.unmodifiableSet(selectedPoints);
			fireSelectionChangeListeners();
		}
		
		target.repaint();
	}
	
	private void fireSelectionChangeListeners() {
		SwingUtilities.invokeLater(() -> {
			selectionChangeListeners.stream().forEach(it -> it.selectionChanged(selectedPoints));
		});
	}

	private WayPoint getNearestWaypointToMouseCoords(Point2D mouse) {
		if (track.isEmpty()) {
			log.warn("Track is empty, cannot find nearest point");
			return null;
		}
		
		WayPoint nearestWaypoint = track.getWaypoint(0);
		double nearestDistance = Double.MAX_VALUE;
		
		for (int i = 0; i < track.size(); i++) {
			WayPoint comparison = track.getWaypoint(i);
			GeoPosition position = track.get(i);
			
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
	public void activate(GpxViewerTrack track) {
		log.info("Entering edit mode for track {}", track.getFileName());
		this.track = track;
		active = true;
	}
	
	/**
	 * Deactivates this listener
	 */
	public void deactivate() {
		log.info("Exiting edit mode");
		track = null;
		active = false;
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
	
}
