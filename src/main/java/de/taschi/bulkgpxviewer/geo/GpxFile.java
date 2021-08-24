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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GpxFile {
	
	private GPX gpx;
	
	private Path fileName;
	
	private List<WayPoint> allWayPoints;
	private List<GeoPosition> allGeoPositions;
	
	@Getter
	private boolean changed = false;
		
	public GpxFile(Path fileName, GPX gpx) {
		this.fileName = fileName;
		this.gpx = gpx;
		
		updateCachedFields();
	}
	
	private void updateCachedFields() {
		allWayPoints = gpx.getTracks().stream()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.collect(Collectors.toUnmodifiableList());
		
		allGeoPositions = allWayPoints.stream()
				.map(GpxToJxMapper.getInstance()::waypointToGeoPosition)
				.collect(Collectors.toUnmodifiableList());
	}

	public Optional<Instant> getStartedAt() {
		
		for(var track : gpx.getTracks()) {
			for(var segment : track.getSegments()) {
				for(var point : segment.getPoints()) {
					
					var timestamp = point.getTime();
					
					if (timestamp.isPresent()) {
						return timestamp.map(ZonedDateTime::toInstant);
					}
				}
			}
		}
		
		return Optional.empty();
	}
	
	public void save() throws IOException {
		doBackup();
		log.info("Writing GPX to '{}'", fileName); //$NON-NLS-1$
		GPX.write(getGpx(), fileName);
		log.info("Written GPX to '{}' successfully", fileName); //$NON-NLS-1$
		changed = false;
	}
	
	private void doBackup() throws IOException {
		File in = fileName.toFile();
		File out = new File(in.getAbsolutePath() + ".bak"); //$NON-NLS-1$

		log.info("Creating backup of '{}' at '{}'", in, out); //$NON-NLS-1$
		FileUtils.copyFile(in, out);
	}

	public Path getFileName() {
		return fileName;
	}

	public boolean equals(Object o) {
		if (o instanceof GpxFile) {
			var otherGpx = ((GpxFile) o).getGpx();
			return gpx.equals(otherGpx);
		}
		return false;
	}

	public int hashCode() {
		return gpx.hashCode();
	}
	
	public GPX getGpx() {
		return gpx;
	}

	/**
	 * Returns a list of all waypoints in this GPX file, without any guarantees as to order.
	 * @return
	 */
	public List<WayPoint> getAllWayPoints() {
		return allWayPoints;
	}

	/**
	 * Returns a list of all GeoPositions in this GPX file. Order will be consistent with getAllWayPoints, as long as
	 * the model is not changed in the interim.
	 * @return
	 */
	public List<GeoPosition> getAllGeoPositions() {
		return allGeoPositions;
	}

	/**
	 * Find the position of a WayPoint inside the data model
	 * @param wayPoint
	 * @return Optional<WaypointIndex> if found, empty optional otherwise.
	 */
	public Optional<WaypointIndex> indexOfWayPoint(WayPoint wayPoint) {
		var tracks = gpx.getTracks();
		
		for(var trackId = 0; trackId < tracks.size(); trackId ++) {
			var segments = tracks.get(trackId).getSegments();
			
			for(var segmentId = 0; segmentId < segments.size(); segmentId ++) {
				var points = segments.get(segmentId).getPoints();
				
				for(var pointId = 0; pointId < points.size(); pointId ++) {
					if (points.get(pointId).equals(wayPoint)) {
						
						return Optional.of(
								WaypointIndex.builder()
									.gpx(gpx)
									.trackId(trackId)
									.segmentId(segmentId)
									.waypointId(pointId)
									.build());
					}
				}
			}
		}
		
		return Optional.empty();
	}

	public void setGpx(GPX gpx) {
		if (this.gpx.equals(gpx)) {
			return;
		}
		changed = true;
		this.gpx = gpx;
		LoadedFileManager.getInstance().fireChangeListeners();
	}

}
