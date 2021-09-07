package de.taschi.bulkgpxviewer.math;

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

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;
import de.taschi.bulkgpxviewer.ui.Messages;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

public class RouteLengthCalculator {
	
	@Inject
	private HaversineCalculator haversineCalculator;
	
	@Inject
	private UnitConverter unitConverter;
	
	public String getFormattedTotalDistance(GPX gpx, UnitSystem system) {
		var distance = BigDecimal.valueOf(getTotalDistance(gpx, system))
				.setScale(1, RoundingMode.HALF_UP);
		
		String unit;
		switch (system) {
			case IMPERIAL: unit = Messages.getString("RouteLengthCalculator.UnitMiles"); break; //$NON-NLS-1$
			case METRIC:
			default: unit = Messages.getString("RouteLengthCalculator.UnitKilometers"); break; //$NON-NLS-1$
		}
		
		return String.format(Messages.getString("RouteLengthCalculator.RouteLengthFormat"), distance, unit); //$NON-NLS-1$
	}
	
	public double getTotalDistance(GPX gpx, UnitSystem system) {
		var distanceInKm = getTotalDistance(gpx);
		
		if (system == UnitSystem.METRIC) {
			return distanceInKm;
		} else {
			return unitConverter.kilometersToMiles(distanceInKm);
		}
	}
	
	public double getTotalDistance(Track track, UnitSystem system) {
		var distanceInKm = getTotalDistance(track);
		
		if (system == UnitSystem.METRIC) {
			return distanceInKm;
		} else {
			return unitConverter.kilometersToMiles(distanceInKm);
		}
	}
	
	public double getTotalDistance(TrackSegment segment, UnitSystem system) {
		var distanceInKm = getTotalDistance(segment);
		
		if (system == UnitSystem.METRIC) {
			return distanceInKm;
		} else {
			return unitConverter.kilometersToMiles(distanceInKm);
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
				
				result += haversineCalculator.getDistance(here, there);
			}
			
			return result;
		}
	}
}
