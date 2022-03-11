package de.taschi.bulkgpxviewer.math

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.Messages
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import java.math.BigDecimal
import java.math.RoundingMode

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

class RouteLengthCalculator constructor() {
    @Inject
    private lateinit var haversineCalculator: HaversineCalculator

    @Inject
    private lateinit var unitConverter: UnitConverter

    fun getFormattedTotalDistance(gpx: GPX, system: UnitSystem): String {
        val distance: BigDecimal = BigDecimal.valueOf(getTotalDistance(gpx, system))
            .setScale(1, RoundingMode.HALF_UP)
        val unit = when (system) {
            UnitSystem.IMPERIAL -> Messages.getString("RouteLengthCalculator.UnitMiles")
            UnitSystem.METRIC -> Messages.getString("RouteLengthCalculator.UnitKilometers")
            else -> Messages.getString("RouteLengthCalculator.UnitKilometers")
        }
        return String.format(
            Messages.getString("RouteLengthCalculator.RouteLengthFormat"),
            distance,
            unit
        ) //$NON-NLS-1$
    }

    fun getTotalDistance(gpx: GPX, system: UnitSystem): Double {
        val distanceInKm: Double = getTotalDistance(gpx)
        if (system == UnitSystem.METRIC) {
            return distanceInKm
        } else {
            return unitConverter.kilometersToMiles(distanceInKm)
        }
    }

    fun getTotalDistance(track: Track, system: UnitSystem): Double {
        val distanceInKm: Double = getTotalDistance(track)
        if (system == UnitSystem.METRIC) {
            return distanceInKm
        } else {
            return unitConverter.kilometersToMiles(distanceInKm)
        }
    }

    fun getTotalDistance(segment: TrackSegment, system: UnitSystem): Double {
        val distanceInKm: Double = getTotalDistance(segment)
        if (system == UnitSystem.METRIC) {
            return distanceInKm
        } else {
            return unitConverter.kilometersToMiles(distanceInKm)
        }
    }

    private fun getTotalDistance(gpx: GPX): Double {
        return gpx.tracks.stream()
            .map { track: Track -> this.getTotalDistance(track) }
            .reduce(0.0) { a, b -> a + b }
    }

    private fun getTotalDistance(track: Track): Double {
        return track.segments.stream()
            .map { segment -> this.getTotalDistance(segment) }
            .reduce(0.0) {a, b -> a + b}
    }

    /**
     * get a track segments' length in kilometers
     * @param segment the length
     * @return
     */
    private fun getTotalDistance(segment: TrackSegment): Double {
        val points: List<WayPoint> = segment.points
        return if (points.size < 2) {
            0.0
        } else {
            var result = 0.0
            for (i in 0 until (points.size - 1)) {
                val here: WayPoint = points[i]
                val there: WayPoint = points[i + 1]
                result += haversineCalculator.getDistance(here, there)
            }
            result
        }
    }
}