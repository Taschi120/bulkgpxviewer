package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class SpeedOverTimePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -553811986061884999L;

	@Override
	public String getXAxisLabel() {
		return "Time (s)";
	}

	@Override
	public String getYAxisLabel() {
		return "Speed (kph)";
	}

	@Override
	public XYDataset getDataset(TrackSegment segment) {
		return TrackStatisticsManager.getInstance().getSpeedOverTimeAsXY(segment);
	}
	
}
