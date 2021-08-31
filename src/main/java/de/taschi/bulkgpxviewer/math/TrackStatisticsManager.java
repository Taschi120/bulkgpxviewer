package de.taschi.bulkgpxviewer.math;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import io.jenetics.jpx.TrackSegment;
import lombok.extern.log4j.Log4j2;

/**
 * Helper class for calculating and caching detailed statistics
 * for GPX tracks, like distance, speed, altitude etc as data rows (arrays of data per GPX node)
 */
@Log4j2
public class TrackStatisticsManager {

	private static TrackStatisticsManager INSTANCE;
	
	/**
	 * If speed between two points is below this threshold, it will be rounded down to 0.
	 * This compensates for GPS imprecisions creating the impression of movement while
	 * the GPS receiver is not actually moving.
	 */
	private static final double STANDING_SPEED_THRESHOLD = 2;
	
	private HashMap<TrackSegment, List<Double>> distanceDiffs = new HashMap<>();
	private HashMap<TrackSegment, List<Double>> totalDistances = new HashMap<>();
	private HashMap<TrackSegment, List<Duration>> totalTimes = new HashMap<>();
	private HashMap<TrackSegment, List<Duration>> timeDiffs = new HashMap<>();
	private HashMap<TrackSegment, List<Double>> speeds = new HashMap<>();
	
	public static TrackStatisticsManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TrackStatisticsManager();
		}
		return INSTANCE;
	}
	
	private TrackStatisticsManager() {
		log.info("Instantiation");
	}
	
	public double getTotalDistance(TrackSegment segment) {
		return getDistanceDifferences(segment).stream().collect(Collectors.summingDouble(it -> it));
	}
	
	public List<Double> getDistanceDifferences(TrackSegment segment) {	
		if (segment == null) { 
			return Collections.emptyList();
		}
		// TODO cache me!
		var waypoints = segment.getPoints();
		
		List<Double> result = new ArrayList<>(waypoints.size());
		
		for (int i = 0; i < waypoints.size(); i++) {
			if (i == 0) {
				result.add(0.0);
			} else {
				var prev = waypoints.get(i - 1);
				var now = waypoints.get(i);
				result.add(HaversineCalculator.getDistance(prev, now));
			}
		}
		
		return result;
	}
	
	/**
	 * Maps the list of waypoints in a segment to a list of distance from start to waypoint X
	 * @param segment
	 * @return
	 */
	public List<Double> getTotalDistances(TrackSegment segment) {
		var cached = totalDistances.get(segment);
		if(cached != null) {
			return cached;
		} else {
			var uncached = getTotalDistancesWithoutCache(segment);
			totalDistances.put(segment, uncached);
			return uncached;
		}
	}

	private List<Double> getTotalDistancesWithoutCache(TrackSegment segment) {
		if (segment == null) { 
			return Collections.emptyList();
		}
		
		var waypoints = segment.getPoints();
		
		if(waypoints.size() == 0) {
			return Collections.emptyList();
		}
		
		List<Double> result = new ArrayList<>(waypoints.size());
		
		var distanceSoFar = 0.0;
		var prevWaypoint = waypoints.get(0);
		
		for(var waypoint: waypoints) {
			distanceSoFar += HaversineCalculator.getDistance(prevWaypoint, waypoint);
			result.add(distanceSoFar);
			prevWaypoint = waypoint;
		}
		
		return result;
	}
	
	
	/**
	 * Maps the list of waypoints in a segment to a list of time elapsed from start until this waypoint.
	 * The first value will be 0.
	 * @param segment
	 * @return
	 */
	public List<Duration> getTotalTimes(TrackSegment segment) {
		var cached = totalTimes.get(segment);
		if(cached != null) {
			return cached;
		} else {
			var uncached = getTotalTimesWithoutCaching(segment);
			totalTimes.put(segment, uncached);
			return uncached;
		}
	}

	
	private List<Duration> getTotalTimesWithoutCaching(TrackSegment segment) {
		if (segment == null) { 
			return Collections.emptyList();
		}
		
		var waypoints = segment.getPoints();
				
		if(waypoints.isEmpty()) {
			return Collections.emptyList();
		}
		
		var times = waypoints.stream()
				.map((it) -> it.getTime())
				.map(it -> it.orElse(null))
				.collect(Collectors.toList());
		
		// We need to check if all waypoints actually have a timestamp, since timestamps are an optiona
		// feature of GPX and not all GPX file contain them.
		if (times.contains(null)) {
			// TODO better error handling
			return waypoints.stream().map(it -> Duration.ZERO).collect(Collectors.toList());
		}
		
		var firstTime = times.get(0);
		
		return times.stream()
				.map(it -> Duration.between(firstTime, it))
				.collect(Collectors.toList());
		
	}

	/**
	 * Maps the list of waypoints in a segment to a list of speed between this waypoint and the previous one.
	 * The first value will be 0.
	 * 
	 * (The speed is given in km/h and needs to be converted in the UI layer if necessary.)
	 * @param segment
	 * @return
	 */
	public List<Double> getSpeeds(TrackSegment segment) {
		var cached = speeds.get(segment);
		if(cached != null) {
			return cached;
		} else {
			var uncached = getSpeedsWithoutCaching(segment);
			speeds.put(segment, uncached);
			return uncached;
		}
	}

	private List<Double> getSpeedsWithoutCaching(TrackSegment segment) {
		if (segment == null) { 
			return Collections.emptyList();
		}
		
		var distances = getDistanceDifferences(segment);
		var times = getTimeDifferences(segment);
		
		if (distances.size() != times.size()) {
			log.error("Speed calculation impossible: data rows have incompatible sizes ({} and {}])", 
					distances.size(), times.size());
			return Collections.emptyList();
		}
		
		List<Double> result = new ArrayList<>(distances.size());
		
		for (int i = 0; i < distances.size(); i++) {
			var distance = distances.get(i);
			var time = times.get(i);
			
			var millis = time.toMillis();
			
			if (millis == 0) {
				result.add(0.0);
			} else {
				var speed = distance / millis * 3_600_000;
				if (speed < STANDING_SPEED_THRESHOLD) {
					if (i > 0) {
						result.set(i - 1, 0.0);
					}
					result.add(0.0);
				} else {
					result.add(speed);
				}
			}
		}
		
		return result;
	}
	
	private List<Duration> getTimeDifferences(TrackSegment segment) {
		
		if (segment == null) { 
			return Collections.emptyList();
		}
		// TODO cache me!
		var waypoints = segment.getPoints();
		
		var times = waypoints.stream()
				.map(it -> it.getTime())
				.map(it -> it.orElse(null))
				.collect(Collectors.toList());
		
		if (times.contains(null)) {
			// TODO better error handling
			log.error("gpx point without timestamp!");
		}
		
		List<Duration> result = new ArrayList<>(waypoints.size());
		
		for (int i = 0; i < waypoints.size(); i++) {
			if (i == 0) {
				result.add(Duration.ZERO);
			} else {
				var prev = times.get(i - 1);
				var now = times.get(i);
				result.add(Duration.between(prev, now));
			}
		}
		
		return result;
	}
	
	public XYDataset getDistanceOverTimeAsXY(TrackSegment segment) {
		var times = getTotalTimes(segment);
		var distances = getTotalDistances(segment);
		
		var result = new XYSeries("Distance over Time");
		
		if (times.size() != distances.size()) {
			var errorMessage = String.format("Can not build dataset for speed over time: inconsistent data row lengths (%s and %s)", 
					times.size(), distances.size());
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		for(int i = 0; i < times.size(); i++) {
			result.add(times.get(i).toSeconds(), distances.get(i));
		}
		
		return new XYSeriesCollection(result);
	}

	/**
	 * Returns a JFreeChart data set of speed over time for a given TrackSegment.
	 * 
	 * (The speed is given in km/h and needs to be converted in the UI layer if necessary.)
	 * @param segment
	 * @return
	 */
	public XYDataset getSpeedOverTimeAsXY(TrackSegment segment) {
		// TODO cache me!
		var times = getTotalTimes(segment);
		var speeds = getSpeeds(segment);
		speeds = smoothe(speeds, 5);
		
		var result = new XYSeries("speedOverTime");
		
		if (times.size() != speeds.size()) {
			var errorMessage = String.format("Can not build dataset for speed over time: inconsistent data row lengths (%s and %s)", 
					times.size(), speeds.size());
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		for(int i = 0; i < times.size(); i++) {
			result.add(times.get(i).getSeconds(), speeds.get(i));
		}
		log.info("Calculated speed over time. {} datapoints.", result.getItemCount());
		return new XYSeriesCollection(result);
	}
	
	private List<Double> smoothe(List<Double> input, int halfWindowSize) {
		List<Double> output = new ArrayList<>(input.size());
		for(int i = 0; i < input.size(); i++) {
			var original = input.get(i);
			if (original < STANDING_SPEED_THRESHOLD) {
				output.add(0.0);
			} else {
				output.add(average(input, i - halfWindowSize, i + halfWindowSize));
			}
		}
		
		return output;
	}
	
	private double average(List<Double> input, int min, int max) {
		min = Math.max(min, 0);
		max = Math.min(max, input.size());
		
		if(min > max) {
			throw new IllegalArgumentException();
		}
		
		if (min == max) {
			return input.get(min);
		}
		
		double sum = 0;
		int num = 0;
		
		for(int i = min; i < max; i++) {
			num++;
			sum += input.get(i);
		}
		
		return sum / num;
	}
	
}
