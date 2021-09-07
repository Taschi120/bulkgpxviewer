package de.taschi.bulkgpxviewer.ui.graphs;

import org.jfree.data.xy.XYDataset;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import io.jenetics.jpx.TrackSegment;

public class DistanceOverTimePanel extends AbstractGraphPanel {
	
	private static final long serialVersionUID = -2962417946885383838L;
	
	@Inject
	private TrackStatisticsManager trackStatisticsManager;

	public DistanceOverTimePanel() {
		super();
		Application.getInjector().injectMembers(this);
	}
	
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
		return trackStatisticsManager.getDistanceOverTimeAsXY(segment);
	}
	
}
