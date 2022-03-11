package de.taschi.bulkgpxviewer.ui.graphs;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.SettingsUpdateListener;
import io.jenetics.jpx.TrackSegment;

public abstract class AbstractGraphPanel extends JPanel implements SettingsUpdateListener {
		
	private static final long serialVersionUID = 1948085630207508743L;
	
	@Inject
	protected SettingsManager settingsManager;
	
	private TrackSegment currentSegment;
	
	private XYPlot plot;
	private JFreeChart chart;
	private ChartPanel chartPanel;
	
	public AbstractGraphPanel() {
		Application.getInjector().injectMembers(this);
		
		setLayout(new BorderLayout());
		
		plot = new XYPlot();
		chart = new JFreeChart(plot);
		chartPanel = new ChartPanel(chart);
		
		
        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

		add(chartPanel, BorderLayout.CENTER);
		
		settingsManager.addSettingsUpdateListener(this);
	}
	
	public void setGpxTrackSegment(TrackSegment segment) {
		currentSegment = segment;
		
		var dataset = getDataset(segment);
		plot.setDataset(dataset);
		chart = makeChart(dataset);
		chartPanel.setChart(chart);
		chartPanel.repaint();
	}
	
	private JFreeChart makeChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                null,
                getXAxisLabel(),
                getYAxisLabel(),
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShapesVisible(0, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.GRAY);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.GRAY);
        
        plot.setDomainCrosshairVisible(false);

        chart.getLegend().setFrame(BlockBorder.NONE);
        
        return chart;
	}
	
	@Override
	public void onSettingsUpdated() {
		// Force re-creation of the diagram because unit system might have changed
		setGpxTrackSegment(currentSegment);
	}
	
	public abstract String getXAxisLabel();
	public abstract String getYAxisLabel();
	public abstract XYDataset getDataset(TrackSegment segment);
}