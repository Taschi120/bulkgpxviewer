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