package de.taschi.bulkgpxviewer.geo;

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
}
