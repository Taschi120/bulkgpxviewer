package de.taschi.bulkgpxviewer.geo;

import org.jxmapviewer.viewer.GeoPosition;

import io.jenetics.jpx.WayPoint;

public class GpxToJxMapper {
	private static GpxToJxMapper INSTANCE = null;
	
	public static GpxToJxMapper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GpxToJxMapper();
		}
		return INSTANCE;
	}
	
	public GeoPosition waypointToGeoPosition(WayPoint in) {
		return new GeoPosition(in.getLatitude().doubleValue(), in.getLongitude().doubleValue());
	}
}
