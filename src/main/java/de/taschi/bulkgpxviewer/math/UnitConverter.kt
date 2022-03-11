package de.taschi.bulkgpxviewer.math

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
class UnitConverter constructor() {
    fun kilometersToMiles(km: Double): Double {
        return km * KILOMETERS_TO_MILES
    }

    fun milesToKilometers(miles: Double): Double {
        return miles / KILOMETERS_TO_MILES
    }

    companion object {
        private val KILOMETERS_TO_MILES: Double = 0.621371
    }
}