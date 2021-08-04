package de.taschi.bulkgpsviewer.gpx;

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
