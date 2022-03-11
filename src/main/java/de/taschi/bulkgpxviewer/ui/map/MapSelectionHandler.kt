package de.taschi.bulkgpxviewer.ui.map

import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.ui.map.MapSelectionHandler
import io.jenetics.jpx.WayPoint
import org.jxmapviewer.JXMapViewer
import org.slf4j.LoggerFactory
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Point2D
import java.util.*
import java.util.stream.Collectors
import javax.swing.SwingUtilities

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
 */ /**
 * This listener handles the selection of wapypoints on the map during editing mode.
 */
class MapSelectionHandler(private val target: JXMapViewer) : MouseAdapter() {
    private var active = false
    private var track: GpxFile? = null

    /** currently selected points  */
    private var selectedPoints: MutableSet<WayPoint> = HashSet()

    /** unmodifiable copy of selectedPoints, cached for performance reasons  */
    var selectedWayPoints = Collections.unmodifiableSet(selectedPoints)
        private set

    private val selectionChangeListeners: MutableList<WaypointSelectionChangeListener> = ArrayList()


    override fun mouseClicked(e: MouseEvent) {
        if (!active) {
            return
        }
        val wp = getNearestWaypointToMouseCoords(e.point)
        if (wp != null) {
            if (selectedPoints.contains(wp)) {
                log.info("Deselecting waypoint at {}|{}", wp.latitude, wp.longitude) //$NON-NLS-1$
                selectedPoints.remove(wp)
            } else if (selectedPoints.size < 2) {
                log.info("Selecting waypoint at {}|{}", wp.latitude, wp.longitude) //$NON-NLS-1$
                selectedPoints.add(wp)
            }
            selectedWayPoints = Collections.unmodifiableSet(selectedPoints)
            fireSelectionChangeListeners()
        }
    }

    private fun fireSelectionChangeListeners() {
        SwingUtilities.invokeLater {
            selectionChangeListeners.stream()
                .forEach { it: WaypointSelectionChangeListener -> it.selectionChanged(selectedPoints) }
        }
    }

    private fun getNearestWaypointToMouseCoords(mouse: Point2D): WayPoint? {
        val lTrack = track
        if (lTrack == null) {
            log.warn("getNearestWaypoingToMouseCoords called while track is null")
            return null
        }
        val points = lTrack.allWayPoints
        val geoPositions = lTrack.allGeoPositions ?: emptyList()
        if (points!!.isEmpty()) {
            log.warn("Track is empty, cannot find nearest point") //$NON-NLS-1$
            return null
        }
        var nearestWaypoint = points[0]
        var nearestDistance = Double.MAX_VALUE
        for (i in points.indices) {
            val comparison = points[i]
            val position = geoPositions[i]
            val point = target.convertGeoPositionToPoint(position)
            val distance = point.distance(mouse)
            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestWaypoint = comparison
            }
        }
        return if (nearestDistance <= SELECTION_THRESHOLD) {
            nearestWaypoint
        } else {
            null
        }
    }

    /**
     * Activates this listener
     * @param track the track which is being edited
     */
    fun activate(track: GpxFile) {
        log.info("Entering edit mode for track {}", track.fileName) //$NON-NLS-1$
        this.track = track
        active = true
    }

    /**
     * Deactivates this listener
     */
    fun deactivate() {
        log.info("Exiting edit mode") //$NON-NLS-1$
        track = null
        active = false
        selectedPoints = mutableSetOf()
    }

    fun addSelectionChangeListener(listener: (Set<WayPoint>?) -> Unit) {
        selectionChangeListeners.add(object : WaypointSelectionChangeListener {
            override fun selectionChanged(selection: Set<WayPoint>?) {
                listener(selection)
            }
        })
    }

    fun addSelectionChangeListener(listener: WaypointSelectionChangeListener) {
        selectionChangeListeners.add(listener)
    }

    fun removeSelectionChangeListener(listener: WaypointSelectionChangeListener): Boolean {
        return selectionChangeListeners.remove(listener)
    }

    /**
     * Change the current selection
     * @param selection
     */
    fun setSelection(selection: List<WayPoint>) {
        selectedPoints = selection.stream().distinct().collect(Collectors.toSet())
        selectedWayPoints = Collections.unmodifiableSet(selectedPoints)
        fireSelectionChangeListeners()
    }

    companion object {
        /**
         * How far away from the waypoint the cursor can be when selecting a waypoint, in pixels
         */
        const val SELECTION_THRESHOLD = 20
        private val log = LoggerFactory.getLogger(MapSelectionHandler::class.java)
    }
}