package de.taschi.bulkgpxviewer.math;

import java.time.Duration;
import java.util.Optional;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DurationCalculator {
	private static DurationCalculator INSTANCE;
	
	public static DurationCalculator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DurationCalculator();
		}
		return INSTANCE;
	}
	
	private DurationCalculator() {
		// prevent instantiation
	}
	
	/**
	 * Get the recorded duration of all tracks of a {@link GPX} file, disregarding any gaps between 
	 * segments or tracks.
	 * 
	 * I. e. if the file contains two segment of 30 minutes duration each, and there is a 30 minute gap
	 * between the two segments, the result of this function will be 60 minutes.
	 * 
	 * @param track The track
	 * @return An optional containing the duration if all track segments had timestamp info, or an
	 * 	empty optional if at least one segment had no time stamp info.
	 */
	public Optional<Duration> getRecordedDurationForGpxFile(GPX gpx) {
		Duration result = Duration.ZERO;
		for(Track track: gpx.getTracks()) {
			var duration = getRecordedDurationForTrack(track);
			if (duration.isEmpty()) {
				return duration;
			} else {
				result = result.plus(duration.get());
			}
		}
		return Optional.of(result);
	}
	
	/**
	 * Get the recorded duration of a {@link Track}, disregarding any gaps between segments.
	 * 
	 * I. e. if the Track contains two segment of 30 minutes duration each, and there is a 30 minute gap
	 * between the two segments, the result of this function will be 60 minutes.
	 * 
	 * @param track The track
	 * @return An optional containing the duration if all track segments had timestamp info, or an
	 * 	empty optional if at least one segment had no time stamp info.
	 */
	public Optional<Duration> getRecordedDurationForTrack(Track track) {
		Duration result = Duration.ZERO;
		for(TrackSegment segment: track.getSegments()) {
			var duration = getSegmentDuration(segment);
			if (duration.isEmpty()) {
				return duration;
			} else {
				result = result.plus(duration.get());
			}
		}
		return Optional.of(result);
	}
	
	/**
	 * Get the duration of a {@link TrackSegment}.
	 * @param segment The segment
	 * @return An optional containing the duration if it could be calculated, 
	 * 	or an empty optional if no timestamps were found in the segment.
	 */
	public Optional<Duration> getSegmentDuration(TrackSegment segment) {
		var waypoints = segment.getPoints();
		
		if(waypoints.size() < 2) {
			log.debug("Time calculation impossible: List has fewer than 2 waypoints");
			return Optional.of(Duration.ZERO);
		}
		
		var start = waypoints.get(0).getInstant().orElse(null);
		var end = waypoints.get(waypoints.size() - 1).getInstant().orElse(null);
	
		if (start == null || end == null) {
			log.debug("Time calculation impossible: GPX does not contain timestamps");
			return Optional.empty();
		}
		
		Duration duration = Duration.between(start, end);
		return Optional.of(duration);
	}
}