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

public class SpeedCalculator {
	
	@Inject
	private DurationCalculator durationCalculator;
	
	@Inject
	private RouteLengthCalculator routeLengthCalculator;
	
	@Inject
	private UnitConverter unitConverter;
	
	public String getFormattedAverageSpeed(GPX gpx, UnitSystem unitSystem) {
		var averageSpeed = getAverageSpeed(gpx, unitSystem);
		
		String formattedSpeed;
		if(averageSpeed == Double.NaN) {
			formattedSpeed = BigDecimal.ZERO.setScale(1).toString();
		} else {
			formattedSpeed = BigDecimal.valueOf(averageSpeed).setScale(1, RoundingMode.HALF_UP).toString();
		}
		
		String unit;
		if (unitSystem == UnitSystem.IMPERIAL) {
			unit = Messages.getString("SpeedCalculator.UnitMph"); //$NON-NLS-1$
		} else {
			unit = Messages.getString("SpeedCalculator.UnitKph"); //$NON-NLS-1$
		}
		
		return String.format(Messages.getString("SpeedCalculator.SpeedFormat"), formattedSpeed, unit); //$NON-NLS-1$
	}
	
	public double getAverageSpeed(GPX gpx, UnitSystem unitSystem) {
		var distance = routeLengthCalculator.getTotalDistance(gpx, unitSystem);
		var optionalTime = durationCalculator.getRecordedDurationForGpxFile(gpx);
		
		if(optionalTime.isPresent()) {
			var time = optionalTime.get().getSeconds();
			if (time == 0) {
				return Double.NaN;
			} else {
				var speedInKph = distance / time * 3600;
				if (unitSystem == UnitSystem.IMPERIAL) {
					return unitConverter.kilometersToMiles(speedInKph);
				} else {
					return speedInKph;
				}
			}
		} else {
			return Double.NaN;
		}
	}
}
