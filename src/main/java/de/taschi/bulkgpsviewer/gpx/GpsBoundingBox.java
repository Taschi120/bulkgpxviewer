package de.taschi.bulkgpsviewer.gpx;

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

public class GpsBoundingBox {
	public double lowestLat = Double.MAX_VALUE;
	public double highestLat = Double.MIN_VALUE;

	public double lowestLong = Double.MAX_VALUE;
	public double highestLong = Double.MIN_VALUE;
	public double getLowestLat() {
		return lowestLat;
	}
	public void setLowestLat(double lowestLat) {
		this.lowestLat = lowestLat;
	}
	public double getHighestLat() {
		return highestLat;
	}
	public void setHighestLat(double highestLat) {
		this.highestLat = highestLat;
	}
	public double getLowestLong() {
		return lowestLong;
	}
	public void setLowestLong(double lowestLong) {
		this.lowestLong = lowestLong;
	}
	public double getHighestLong() {
		return highestLong;
	}
	public void setHighestLong(double highestLong) {
		this.highestLong = highestLong;
	}
	
	public double getCenterLat() {
		return (highestLat + lowestLat) / 2d;
	}
	
	public double getCenterLong() {
		return (highestLong + lowestLong) / 2d;
	}
	
	public void clampLat(double lat) {
		if (lowestLat > lat) {
			lowestLat = lat;
		}
		if (highestLat < lat) {
			highestLat = lat;
		}
	}
	
	public void clampLong(double lon) {
		if (lowestLong > lon) {
			lowestLong = lon;
		}
		if (highestLong < lon) {
			highestLong = lon;
		}
	}
	
	public void clamp(GeoPosition p) {
		clampLat(p.getLatitude());
		clampLong(p.getLongitude());
	}
}
