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
 */ /**
 * Identifier for the position of a [WayPoint] within a [GPX]
 */
class WaypointIndex internal constructor(var gpx: GPX?, var trackId: Int, var segmentId: Int, var waypointId: Int) :
    Comparable<WaypointIndex?> {

    public override fun compareTo(o: WaypointIndex?): Int {
        if (o!!.trackId != trackId) {
            return o.trackId - trackId
        }
        if (o.segmentId != segmentId) {
            return o.segmentId - segmentId
        }
        return o.waypointId - waypointId
    }

    fun isOnSameSegment(other: WaypointIndex): Boolean {
        return (other.gpx === gpx
                ) && (other.trackId == trackId
                ) && (other.segmentId == segmentId)
    }

    public override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (!(o is WaypointIndex)) return false
        val other: WaypointIndex = o
        if (!other.canEqual(this as Any?)) return false
        val `this$gpx`: Any? = gpx
        val `other$gpx`: Any? = other.gpx
        if (if (`this$gpx` == null) `other$gpx` != null else !(`this$gpx` == `other$gpx`)) return false
        if (trackId != other.trackId) return false
        if (segmentId != other.segmentId) return false
        if (waypointId != other.waypointId) return false
        return true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is WaypointIndex
    }

    public override fun hashCode(): Int {
        val PRIME: Int = 59
        var result: Int = 1
        val `$gpx`: Any? = gpx
        result = result * PRIME + (if (`$gpx` == null) 43 else `$gpx`.hashCode())
        result = result * PRIME + trackId
        result = result * PRIME + segmentId
        result = result * PRIME + waypointId
        return result
    }

    public override fun toString(): String {
        return "WaypointIndex(gpx=" + gpx + ", trackId=" + trackId + ", segmentId=" + segmentId + ", waypointId=" + waypointId + ")"
    }

    class WaypointIndexBuilder internal constructor() {
        private var gpx: GPX? = null
        private var trackId: Int = 0
        private var segmentId: Int = 0
        private var waypointId: Int = 0
        fun gpx(gpx: GPX?): WaypointIndexBuilder {
            this.gpx = gpx
            return this
        }

        fun trackId(trackId: Int): WaypointIndexBuilder {
            this.trackId = trackId
            return this
        }

        fun segmentId(segmentId: Int): WaypointIndexBuilder {
            this.segmentId = segmentId
            return this
        }

        fun waypointId(waypointId: Int): WaypointIndexBuilder {
            this.waypointId = waypointId
            return this
        }

        fun build(): WaypointIndex {
            return WaypointIndex(gpx, trackId, segmentId, waypointId)
        }

        public override fun toString(): String {
            return "WaypointIndex.WaypointIndexBuilder(gpx=" + gpx + ", trackId=" + trackId + ", segmentId=" + segmentId + ", waypointId=" + waypointId + ")"
        }
    }

    companion object {
        fun builder(): WaypointIndexBuilder {
            return WaypointIndexBuilder()
        }
    }
}