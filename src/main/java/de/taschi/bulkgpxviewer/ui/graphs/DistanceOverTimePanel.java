package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class DistanceOverTimePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -2962417946885383838L;

	@Override
	public String getXAxisLabel() {
		return "Time (s)";
	}

	@Override
	public String getYAxisLabel() {
		return "Distance (km)";
	}

	@Override
	public XYDataset getDataset(TrackSegment segment) {
		return TrackStatisticsManager.getInstance().getDistanceOverTimeAsXY(segment);
	}
	
}
