package de.taschi.bulkgpxviewer.ui.map;

import java.util.Set;

import io.jenetics.jpx.WayPoint;

/**
 * Listener interface used by {@linkplain MapSelectionHandler} to notify other components of selection changes
 */
public interface WaypointSelectionChangeListener {
	public void selectionChanged(Set<WayPoint> selection);
}
