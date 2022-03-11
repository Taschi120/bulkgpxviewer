package de.taschi.bulkgpxviewer.math

import io.jenetics.jpx.WayPoint
import org.jxmapviewer.viewer.GeoPosition

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
 */   class HaversineCalculator constructor() {
    /**
     * Calculates the distance between to sets of coordinates, as per the
     * Haversine function. See https://en.wikipedia.org/wiki/Haversine_formula and
     * https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
     * for details on the maths.
     *
     * @return the distance, in kilometers
     */
    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat: Double = deg2rad(lat2 - lat1)
        val dLon: Double = deg2rad(lon2 - lon1)
        val a: Double = (Math.pow(Math.sin(dLat / 2), 2.0)
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.pow(Math.sin(dLon / 2), 2.0)))
        val c: Double = 2 * Math.asin(Math.sqrt(a))
        val d: Double = EARTH_RADIUS * c
        return d
    }

    /**
     * convert degrees to radians
     * @return
     */
    private fun deg2rad(deg: Double): Double {
        return deg * (Math.PI / 180)
    }

    /**
     * Calculates the distance between to sets of coordinates, as per the
     * Haversine function. See https://en.wikipedia.org/wiki/Haversine_formula and
     * https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
     * for details on the maths.
     *
     * @param here the first set of coordinates
     * @param there the second set of coordinates
     * @return the distance, in kilometers
     */
    fun getDistance(here: GeoPosition, there: GeoPosition): Double {
        return getDistance(
            here.getLatitude(), here.getLongitude(),
            there.getLatitude(), there.getLongitude()
        )
    }

    /**
     * Calculates the distance between to sets of coordinates, as per the
     * Haversine function. See https://en.wikipedia.org/wiki/Haversine_formula and
     * https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
     * for details on the maths.
     *
     * @param here the first set of coordinates
     * @param there the second set of coordinates
     * @return the distance, in kilometers
     */
    fun getDistance(here: WayPoint, there: WayPoint): Double {
        return getDistance(
            here.getLatitude().toDouble(), here.getLongitude().toDouble(),
            there.getLatitude().toDouble(), there.getLongitude().toDouble()
        )
    }

    companion object {
        private val EARTH_RADIUS: Int = 6371
    }
}