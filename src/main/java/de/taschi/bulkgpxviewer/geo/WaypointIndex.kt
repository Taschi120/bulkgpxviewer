package de.taschi.bulkgpxviewer.geo;

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

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import lombok.Builder;
import lombok.Data;

/**
 * Identifier for the position of a {@link WayPoint} within a {@link GPX}
 */
@Data
@Builder
public class WaypointIndex implements Comparable<WaypointIndex> {
	private GPX gpx;
	private int trackId;
	private int segmentId;
	private int waypointId;
	
	@Override
	public int compareTo(WaypointIndex o) {
		if (o.getTrackId() != this.getTrackId()) {
			return o.getTrackId() - this.getTrackId();
		}
		
		if (o.getSegmentId() != this.getSegmentId()) {
			return o.getSegmentId() - this.getSegmentId();
		}
		
		return o.getWaypointId() - this.getWaypointId();
	}
	
	public boolean isOnSameSegment(WaypointIndex other) {
		return other.getGpx() == this.gpx
				&& other.getTrackId() == this.trackId
				&& other.getSegmentId() == this.segmentId;
	}
}
