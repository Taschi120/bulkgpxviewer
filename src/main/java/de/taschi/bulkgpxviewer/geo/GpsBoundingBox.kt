package de.taschi.bulkgpxviewer.geo

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
 */   class GpsBoundingBox constructor() {
    var lowestLat: Double = Double.MAX_VALUE
    var highestLat: Double = Double.MIN_VALUE
    var lowestLong: Double = Double.MAX_VALUE
    var highestLong: Double = Double.MIN_VALUE
    val centerLat: Double
        get() {
            return (highestLat + lowestLat) / 2.0
        }
    val centerLong: Double
        get() {
            return (highestLong + lowestLong) / 2.0
        }

    private fun clampLat(lat: Double) {
        if (lowestLat > lat) {
            lowestLat = lat
        }
        if (highestLat < lat) {
            highestLat = lat
        }
    }

    private fun clampLong(lon: Double) {
        if (lowestLong > lon) {
            lowestLong = lon
        }
        if (highestLong < lon) {
            highestLong = lon
        }
    }

    /**
     * Expands the bounding box to include the given point p, if it is not already within the bounding box.
     */
    fun expandToFit(p: GeoPosition) {
        clampLat(p.latitude)
        clampLong(p.longitude)
    }
}