package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import io.jenetics.jpx.TrackSegment;

public class SpeedOverTimePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -553811986061884999L;

	@Override
	public String getXAxisLabel() {
		return "Time [minutes]";
	}

	@Override
	public String getYAxisLabel() {
		var unitSystem = SettingsManager.getInstance().getSettings().getUnitSystem();
		
		return String.format("Speed [%s]", unitSystem.getDefaultSpeedUnit());
	}

	@Override
	public XYDataset getDataset(TrackSegment segment) {
		return TrackStatisticsManager.getInstance().getSpeedOverTimeAsXY(segment, 
				SettingsManager.getInstance().getSettings().getUnitSystem());
	}
	
}
