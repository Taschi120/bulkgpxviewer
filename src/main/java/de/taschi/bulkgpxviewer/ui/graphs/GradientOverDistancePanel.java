package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class GradientOverDistancePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = 3667881018436994697L;

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
		return TrackStatisticsManager.getInstance().getGradientOverDistanceAsXY(segment);
	}
	
}
