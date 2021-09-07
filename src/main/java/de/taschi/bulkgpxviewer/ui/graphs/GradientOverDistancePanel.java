package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class GradientOverDistancePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = 3667881018436994697L;

	@Inject
	private TrackStatisticsManager trackStatisticsManager;
	
	public GradientOverDistancePanel() {
		super();
		Application.getInjector().injectMembers(this);
	}
	
	@Override
	public String getXAxisLabel() {
		return "Distance (km)";
	}

	@Override
	public String getYAxisLabel() {
		return "Gradient (%)";
	}

	@Override
	public XYDataset getDataset(TrackSegment segment) {
		return trackStatisticsManager.getGradientOverDistanceAsXY(segment);
	}
	
}
