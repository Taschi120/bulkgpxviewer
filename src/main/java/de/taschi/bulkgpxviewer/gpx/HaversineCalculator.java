package de.taschi.bulkgpxviewer.gpx;

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

import org.jxmapviewer.viewer.GeoPosition;

public class HaversineCalculator {
	
	private static final int EARTH_RADIUS = 6371;
	
	
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
	public static double getDistance(GeoPosition here, GeoPosition there) {
		var dLat = deg2rad(there.getLatitude() - here.getLatitude());
		var dLon = deg2rad(there.getLongitude() - here.getLongitude());
		
		var a = Math.pow(Math.sin(dLat / 2), 2)
				+ Math.cos(deg2rad(here.getLatitude()))
				* Math.cos(deg2rad(there.getLatitude()))
				* Math.pow(Math.sin(dLon / 2), 2);
		
		var c = 2 * Math.asin(Math.sqrt(a));
		
		var d = EARTH_RADIUS * c;
		
		return d;
	}
	
	/**
	 * convert degrees to radians
	 * @return
	 */
	private static double deg2rad(double deg) {
		return deg * (Math.PI / 180);
	}

}
