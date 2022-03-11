package de.taschi.bulkgpxviewer.math

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.Messages
import io.jenetics.jpx.GPX
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

class SpeedCalculator {
    @Inject
    private lateinit var durationCalculator: DurationCalculator

    @Inject
    private lateinit var routeLengthCalculator: RouteLengthCalculator

    @Inject
    private lateinit var unitConverter: UnitConverter


    fun getFormattedAverageSpeed(gpx: GPX, unitSystem: UnitSystem): String {
        val averageSpeed: Double = getAverageSpeed(gpx, unitSystem)

        val formattedSpeed = if (averageSpeed.isNaN()) {
            BigDecimal.ZERO.setScale(1).toString()
        } else {
            BigDecimal.valueOf(averageSpeed).setScale(1, RoundingMode.HALF_UP).toString()
        }
        val unit = if (unitSystem == UnitSystem.IMPERIAL) {
            Messages.getString("SpeedCalculator.UnitMph") //$NON-NLS-1$
        } else {
            Messages.getString("SpeedCalculator.UnitKph") //$NON-NLS-1$
        }

        return String.format(Messages.getString("SpeedCalculator.SpeedFormat"), formattedSpeed, unit) //$NON-NLS-1$
    }

    fun getAverageSpeed(gpx: GPX, unitSystem: UnitSystem): Double {
        val distance: Double = routeLengthCalculator.getTotalDistance(gpx, unitSystem)
        val optionalTime = durationCalculator.getRecordedDurationForGpxFile(gpx)

        return optionalTime.map {
            val time: Long = it.seconds
            if (time == 0L) {
                Double.NaN
            } else {
                val speedInKph: Double = distance / time * 3600
                if (unitSystem == UnitSystem.IMPERIAL) {
                    unitConverter.kilometersToMiles(speedInKph)
                } else {
                    speedInKph
                }
            }
        }.orElse(Double.NaN)
    }
}