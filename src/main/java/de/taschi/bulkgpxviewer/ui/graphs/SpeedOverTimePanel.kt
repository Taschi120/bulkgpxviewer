package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import io.jenetics.jpx.TrackSegment;

public class SpeedOverTimePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -553811986061884999L;

	@Inject
	private SettingsManager settingsManager;
	
	@Inject
	private TrackStatisticsManager trackStatisticsManager;
	
	public SpeedOverTimePanel() {
		super();
		Application.getInjector().injectMembers(this);
	}
	
	@Override
	public String getXAxisLabel() {
		return "Time [minutes]";
	}

	@Override
	public String getYAxisLabel() {
		var unitSystem = settingsManager.getSettings().getUnitSystem();
		
		return String.format("Speed [%s]", unitSystem.getDefaultSpeedUnit());
	}

	@Override
	public XYDataset getDataset(TrackSegment segment) {
		return trackStatisticsManager.getSpeedOverTimeAsXY(segment, 
				settingsManager.getSettings().getUnitSystem());
	}
	
}
