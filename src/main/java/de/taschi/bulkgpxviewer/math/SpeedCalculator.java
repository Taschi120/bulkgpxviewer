package de.taschi.bulkgpxviewer.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;
import de.taschi.bulkgpxviewer.ui.Messages;
import io.jenetics.jpx.GPX;

public class SpeedCalculator {

	private static SpeedCalculator INSTANCE = null;
	
	public static SpeedCalculator getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SpeedCalculator();
		}
		
		return INSTANCE;
	}
	
	private SpeedCalculator() {
		// prevent instantiation
	}
	
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
		var distance = RouteLengthCalculator.getInstance().getTotalDistance(gpx, unitSystem);
		var optionalTime = DurationCalculator.getInstance().getRecordedDurationForGpxFile(gpx);
		
		if(optionalTime.isPresent()) {
			var time = optionalTime.get().getSeconds();
			if (time == 0) {
				return Double.NaN;
			} else {
				var speedInKph = distance / time * 3600;
				if (unitSystem == UnitSystem.IMPERIAL) {
					return UnitConverter.kilometersToMiles(speedInKph);
				} else {
					return speedInKph;
				}
			}
		} else {
			return Double.NaN;
		}
	}
}
