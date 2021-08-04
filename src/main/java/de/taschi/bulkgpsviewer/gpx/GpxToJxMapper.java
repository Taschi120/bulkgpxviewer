package de.taschi.bulkgpsviewer.gpx;

import java.util.List;
import java.util.stream.Collectors;

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
	
	public List<GeoPosition> waypointsToGeoPositions(List<WayPoint> in) {
		return in.stream()
				.map((it) -> waypointToGeoPosition(it))
				.collect(Collectors.toList());
	}
	
	public List<List<GeoPosition>> waypointTracksToGeoPositionTracks(List<List<WayPoint>> in) {
		return in.stream()
				.map((it) -> waypointsToGeoPositions(it))
				.collect(Collectors.toList());
	}
}
