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

/**
 * Identifier for the position of a {@link WayPoint} within a {@link GPX}
 */
public class WaypointIndex implements Comparable<WaypointIndex> {
	private GPX gpx;
	private int trackId;
	private int segmentId;
	private int waypointId;

	WaypointIndex(GPX gpx, int trackId, int segmentId, int waypointId) {
		this.gpx = gpx;
		this.trackId = trackId;
		this.segmentId = segmentId;
		this.waypointId = waypointId;
	}

	public static WaypointIndexBuilder builder() {
		return new WaypointIndexBuilder();
	}

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

	public GPX getGpx() {
		return this.gpx;
	}

	public int getTrackId() {
		return this.trackId;
	}

	public int getSegmentId() {
		return this.segmentId;
	}

	public int getWaypointId() {
		return this.waypointId;
	}

	public void setGpx(GPX gpx) {
		this.gpx = gpx;
	}

	public void setTrackId(int trackId) {
		this.trackId = trackId;
	}

	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}

	public void setWaypointId(int waypointId) {
		this.waypointId = waypointId;
	}

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof WaypointIndex)) return false;
		final WaypointIndex other = (WaypointIndex) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$gpx = this.getGpx();
		final Object other$gpx = other.getGpx();
		if (this$gpx == null ? other$gpx != null : !this$gpx.equals(other$gpx)) return false;
		if (this.getTrackId() != other.getTrackId()) return false;
		if (this.getSegmentId() != other.getSegmentId()) return false;
		if (this.getWaypointId() != other.getWaypointId()) return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof WaypointIndex;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $gpx = this.getGpx();
		result = result * PRIME + ($gpx == null ? 43 : $gpx.hashCode());
		result = result * PRIME + this.getTrackId();
		result = result * PRIME + this.getSegmentId();
		result = result * PRIME + this.getWaypointId();
		return result;
	}

	public String toString() {
		return "WaypointIndex(gpx=" + this.getGpx() + ", trackId=" + this.getTrackId() + ", segmentId=" + this.getSegmentId() + ", waypointId=" + this.getWaypointId() + ")";
	}

	public static class WaypointIndexBuilder {
		private GPX gpx;
		private int trackId;
		private int segmentId;
		private int waypointId;

		WaypointIndexBuilder() {
		}

		public WaypointIndexBuilder gpx(GPX gpx) {
			this.gpx = gpx;
			return this;
		}

		public WaypointIndexBuilder trackId(int trackId) {
			this.trackId = trackId;
			return this;
		}

		public WaypointIndexBuilder segmentId(int segmentId) {
			this.segmentId = segmentId;
			return this;
		}

		public WaypointIndexBuilder waypointId(int waypointId) {
			this.waypointId = waypointId;
			return this;
		}

		public WaypointIndex build() {
			return new WaypointIndex(gpx, trackId, segmentId, waypointId);
		}

		public String toString() {
			return "WaypointIndex.WaypointIndexBuilder(gpx=" + this.gpx + ", trackId=" + this.trackId + ", segmentId=" + this.segmentId + ", waypointId=" + this.waypointId + ")";
		}
	}
}
