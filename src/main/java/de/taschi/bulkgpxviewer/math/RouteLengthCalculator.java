package de.taschi.bulkgpxviewer.math;

import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RouteLengthCalculator {
	private static RouteLengthCalculator INSTANCE;
	
	public static RouteLengthCalculator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RouteLengthCalculator();
		}
		return INSTANCE;
	}
	
	private RouteLengthCalculator() {
		// prevent instantiation
		log.info("Instantiated");
	}
	
	public double getTotalDistance(GPX gpx, UnitSystem system) {
		var distanceInKm = getTotalDistance(gpx);
		
		if (system == UnitSystem.METRIC) {
			return distanceInKm;
		} else {
			return UnitConverter.kilometersToMiles(distanceInKm);
		}
	}
	
	public double getTotalDistance(Track track, UnitSystem system) {
		var distanceInKm = getTotalDistance(track);
		
		if (system == UnitSystem.METRIC) {
			return distanceInKm;
		} else {
			return UnitConverter.kilometersToMiles(distanceInKm);
		}
	}
	
	public double getTotalDistance(TrackSegment segment, UnitSystem system) {
		var distanceInKm = getTotalDistance(segment);
		
		if (system == UnitSystem.METRIC) {
			return distanceInKm;
		} else {
			return UnitConverter.kilometersToMiles(distanceInKm);
		}
	}

	public double getTotalDistance(GPX gpx) {
		return gpx.getTracks().stream()
				.map(this::getTotalDistance)
				.reduce(0.0, Double::sum);
	}
	
	public double getTotalDistance(Track track) {
		return track.getSegments().stream()
				.map(this::getTotalDistance)
				.reduce(0.0, Double::sum);
	}
	
	/**
	 * get a track segments' length in kilometers
	 * @param segment the length
	 * @return
	 */
	public double getTotalDistance(TrackSegment segment) {
		var points = segment.getPoints();
		
		if(points.size() < 2) {
			return 0;
			
		} else {
			
			var result = 0.0;
			for (var i = 0; i < points.size() - 1; i++) {
				var here = points.get(i);
				var there = points.get(i + 1);
				
				result += HaversineCalculator.getDistance(here, there);
			}
			
			return result;
		}
	}
}
