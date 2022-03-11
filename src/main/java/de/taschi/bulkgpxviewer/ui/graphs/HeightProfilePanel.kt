package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class HeightProfilePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -4042051302989229070L;

	@Inject
	private TrackStatisticsManager trackStatisticsManager;
	
	public HeightProfilePanel() {
		super();
		Application.getInjector().injectMembers(this);
	}
	
	@Override
	public String getXAxisLabel() {
		return "Distance (km)";
	}

	@Override
	public String getYAxisLabel() {
		return "Altitude (m)";
	}

	@Override
	public XYDataset getDataset(TrackSegment segment) {
		return trackStatisticsManager.getHeightProfileAsXY(segment);
	}

}
