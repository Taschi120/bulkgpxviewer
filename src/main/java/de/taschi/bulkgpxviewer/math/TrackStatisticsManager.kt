package de.taschi.bulkgpxviewer.math;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;
import de.taschi.bulkgpxviewer.ui.Messages;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.Length.Unit;
import lombok.extern.log4j.Log4j2;

/**
 * Helper class for calculating and caching detailed statistics
 * for GPX tracks, like distance, speed, altitude etc as data rows (arrays of data per GPX node)
 */
@Log4j2
public class TrackStatisticsManager {
	
	/**
	 * If speed between two points is below this threshold, it will be rounded down to 0.
	 * This compensates for GPS imprecisions creating the impression of movement while
	 * the GPS receiver is not actually moving.
	 */
	private static final double STANDING_SPEED_THRESHOLD = 2;
	
	private static final Length ZERO_LENGTH = Length.of(0, Unit.METER);
	
	/** Caches */
	private HashMap<TrackSegment, List<Double>> distanceDiffs = new HashMap<>();
	private HashMap<TrackSegment, List<Double>> totalDistances = new HashMap<>();
	private HashMap<TrackSegment, List<Duration>> totalTimes = new HashMap<>();
	private HashMap<TrackSegment, List<Duration>> timeDiffs = new HashMap<>();
	private HashMap<TrackSegment, List<Double>> speeds = new HashMap<>();
	private HashMap<TrackSegment, List<Double>> elevations = new HashMap<>();
	private HashMap<TrackSegment, List<Double>> gradients = new HashMap<>();
	
	@Inject
	private HaversineCalculator haversineCalculator;
	
	@Inject
	private UnitConverter unitConverter;
	
	public double getTotalDistance(TrackSegment segment) {
		return getDistanceDifferences(segment).stream().collect(Collectors.summingDouble(it -> it));
	}
	
	public List<Double> getDistanceDifferences(TrackSegment segment) {
		return resolveCache(distanceDiffs, segment, this::calculateDistanceDifferences);
	}

	private List<Double> calculateDistanceDifferences(TrackSegment segment) {	
		if (segment == null) { 
			return Collections.emptyList();
		}
		var waypoints = segment.getPoints();
		
		List<Double> result = new ArrayList<>(waypoints.size());
		
		for (int i = 0; i < waypoints.size(); i++) {
			if (i == 0) {
				result.add(0.0);
			} else {
				var prev = waypoints.get(i - 1);
				var now = waypoints.get(i);
				result.add(haversineCalculator.getDistance(prev, now));
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
		return resolveCache(totalDistances, segment, this::calculateTotalDistances);
	}

	private List<Double> calculateTotalDistances(TrackSegment segment) {
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
			distanceSoFar += haversineCalculator.getDistance(prevWaypoint, waypoint);
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
		return resolveCache(totalTimes, segment, this::calculateTotalTimes);
	}

	private List<Duration> calculateTotalTimes(TrackSegment segment) {
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
		return resolveCache(speeds, segment, this::calculateSpeeds);
	}

	private List<Double> calculateSpeeds(TrackSegment segment) {
		if (segment == null) { 
			return Collections.emptyList();
		}
		
		var distances = getDistanceDifferences(segment);
		var times = getTimeDifferences(segment);
		
		if (distances.size() != times.size()) {
			log.error("Speed calculation impossible: data rows have incompatible sizes ({} and {}])",  //$NON-NLS-1$
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
	
	public List<Duration> getTimeDifferences(TrackSegment segment) {
		return resolveCache(timeDiffs, segment, this::calculateTimeDifferences);
	}
	
	private List<Duration> calculateTimeDifferences(TrackSegment segment) {
		
		if (segment == null) { 
			return Collections.emptyList();
		}

		var waypoints = segment.getPoints();
		var times = waypoints.stream()
				.map(it -> it.getTime())
				.map(it -> it.orElse(null))
				.collect(Collectors.toList());
		
		if (times.contains(null)) {
			// TODO better error handling
			log.error("gpx point without timestamp!"); //$NON-NLS-1$
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
	
	public List<Double> getElevations(TrackSegment segment) {
		return resolveCache(elevations, segment, this::calculateElevations);
	}
	
	private List<Double> calculateElevations(TrackSegment segment) {
		var waypoints = segment.getPoints();
		var elevations = waypoints.stream()
				.map(it -> it.getElevation().orElse(ZERO_LENGTH).to(Unit.METER))
				.collect(Collectors.toUnmodifiableList());
		
		return elevations;
	}
	
	/**
	 * Gets a list in which element X represents the gradient (in percent) between 
	 * waypoint X - 1 and waypoint X.
	 * 
	 * The first element is 0.
	 * 
	 * @param segment
	 * @return
	 */
	public List<Double> getGradients(TrackSegment segment) {
		return resolveCache(gradients, segment, this::calculateGradients);
	}
	
	private List<Double> calculateGradients(TrackSegment segment) {
		var distanceDifferences = getDistanceDifferences(segment);
		var elevations = smootheWithoutZeroSnap(getElevations(segment), 50);
		
		if (distanceDifferences.size() != elevations.size()) {
			var errorMessage = String.format("Can not build dataset for gradients: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
					distanceDifferences.size(), elevations.size());
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		var result = new ArrayList<Double>(distanceDifferences.size());
		result.add(0.0);
				
		for(int i = 1; i < elevations.size(); i++) {
			var distance = distanceDifferences.get(i);
			
			if (distance == 0) {
				result.add(0.0);
			} else {
				var first = elevations.get(i - 1);
				var second = elevations.get(i);
				
				// factor 1000 is necessary because distances are in kilometers,
				// while altitudes are in meters
				var gradient = (second - first) * 1000 / distance;
				
				result.add(gradient);
			}
		}
		
		return result;
	}
	
	public XYDataset getHeightProfileAsXY(TrackSegment segment) {
		var altitudes = getElevations(segment);
		var distances = getTotalDistances(segment);
		
		var result = new XYSeries(Messages.getString("TrackStatisticsManager.HeightProfileDiagramLabel")); //$NON-NLS-1$
		
		if (altitudes.size() != distances.size()) {
			var errorMessage = String.format("Can not build dataset for speed over time: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
					altitudes.size(), distances.size());
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		for(int i = 0; i < altitudes.size(); i++) {
			result.add(distances.get(i), altitudes.get(i));
		}
		
		return new XYSeriesCollection(result);
	}
	
	public XYDataset getDistanceOverTimeAsXY(TrackSegment segment) {
		var times = getTotalTimes(segment).stream()
				.map(it -> (double) it.getSeconds())
				.collect(Collectors.toList());
		var distances = getTotalDistances(segment);
		
		return buildXYDataset(Messages.getString("TrackStatisticsManager.DistanceOverTimeDiagramLabel"), times, distances); //$NON-NLS-1$
	}
	
	/**
	 * Returns a JFreeChart data set of speed over time for a given TrackSegment.
	 * 
	 * Times are in minutes, distances are in km or miles depending on UnitSystem parameter.
	 * 
	 * @param segment
	 * @return
	 */
	public XYDataset getSpeedOverTimeAsXY(TrackSegment segment, UnitSystem unitSystem) {
		var times = getTotalTimes(segment).stream()
				.map(it -> it.toMillis() / 1000.0)
				.map(it -> it / 60.0)
				.collect(Collectors.toUnmodifiableList());
		
		var speeds = getSpeeds(segment).stream()
				.map(it -> unitSystem == UnitSystem.IMPERIAL ? unitConverter.kilometersToMiles(it) : it)
				.collect(Collectors.toUnmodifiableList());
		
		speeds = smootheWithZeroSnap(speeds, 5);
		
		return buildXYDataset(Messages.getString("TrackStatisticsManager.SpeedOverTimeDiagramLabel"), times, speeds); //$NON-NLS-1$
	}
	

	public XYDataset getGradientOverDistanceAsXY(TrackSegment segment) {
		var gradients = getGradients(segment);
		//gradients = smootheWithoutZeroSnap(gradients, 50);
		var distances = getTotalDistances(segment);
		
		return buildXYDataset(Messages.getString("TrackStatisticsManager.GradientOverDistanceDiagramLabel"), distances, gradients); //$NON-NLS-1$
	}
	
	private <T> List<T> resolveCache(HashMap<TrackSegment, List<T>> cache, TrackSegment segment, Calculator<T> calculator) {
		if (segment == null || segment.isEmpty()) {
			return Collections.emptyList();
		}
		
		var cached = cache.get(segment);
		if (cached != null) {
			return cached;
		} else {
			var calculated = calculator.calculate(segment);
			cache.put(segment, calculated);
			return calculated;
		}
	}
	
	private List<Double> smootheWithoutZeroSnap(List<Double> input, int halfWindowSize) {
		List<Double> output = new ArrayList<>(input.size());
		for(int i = 0; i < input.size(); i++) {
			output.add(average(input, i - halfWindowSize, i + halfWindowSize));
		}
		
		return output;
	}
	
	private List<Double> smootheWithZeroSnap(List<Double> input, int halfWindowSize) {
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

	private static XYDataset buildXYDataset(String name, List<Double> xAxis, List<Double> yAxis) {
		var result = new XYSeries(name);
		
		if (xAxis.size() != yAxis.size()) {
			var errorMessage = String.format("Can not build dataset: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
					xAxis.size(), yAxis.size());
			log.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		for(int i = 0; i < xAxis.size(); i++) {
			result.add(xAxis.get(i), yAxis.get(i));
		}
		return new XYSeriesCollection(result);
	}
	
	private interface Calculator<T> {
		public List<T> calculate(TrackSegment t); 
	}
}
