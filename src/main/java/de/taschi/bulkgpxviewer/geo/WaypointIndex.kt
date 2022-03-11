package de.taschi.bulkgpxviewer.geo

import io.jenetics.jpx.GPX
import io.jenetics.jpx.WayPoint

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

/**
 * Identifier for the position of a [WayPoint] within a [GPX]
 */
data class WaypointIndex(var gpx: GPX?, var trackId: Int, var segmentId: Int, var waypointId: Int) :
    Comparable<WaypointIndex> {

    override fun compareTo(other: WaypointIndex): Int {
        if (other.trackId != trackId) {
            return other.trackId - trackId
        }
        if (other.segmentId != segmentId) {
            return other.segmentId - segmentId
        }
        return other.waypointId - waypointId
    }

    fun isOnSameSegment(other: WaypointIndex): Boolean {
        return (other.gpx === gpx) && (other.trackId == trackId) && (other.segmentId == segmentId)
    }
}