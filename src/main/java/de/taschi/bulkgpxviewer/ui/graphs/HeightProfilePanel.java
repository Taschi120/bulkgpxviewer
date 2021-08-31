package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class HeightProfilePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -4042051302989229070L;

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
		return TrackStatisticsManager.getInstance().getHeightProfileAsXY(segment);
	}

}
