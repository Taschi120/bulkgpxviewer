package de.taschi.bulkgpxviewer.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.ui.graphs.DistanceOverTimePanel;
import de.taschi.bulkgpxviewer.ui.graphs.GradientOverDistancePanel;
import de.taschi.bulkgpxviewer.ui.graphs.HeightProfilePanel;
import de.taschi.bulkgpxviewer.ui.graphs.SpeedOverTimePanel;
import io.jenetics.jpx.GPX;
import lombok.extern.log4j.Log4j2;

/**
 * Helper class for testing {@linkplain GraphPanel}. Pass a GPX file as the first command line argument
 * to use this. (This is not a unit test!)
 */
@Log4j2
public class GraphPanelTestWindow {
		
	public static void main(String[] args) {
		// test purposes only!
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		
		JTabbedPane tabber = new JTabbedPane();
		frame.add(tabber, BorderLayout.CENTER);
		
		var speedOverTimePanel = new SpeedOverTimePanel();
		tabber.add("Speed over time", speedOverTimePanel);
		
		var distanceOverTimePanel = new DistanceOverTimePanel();
		tabber.add("Distance over time", distanceOverTimePanel);
		
		var heightProfilePanel = new HeightProfilePanel();
		tabber.add("Height profile", heightProfilePanel);
		
		var gradientPanel = new GradientOverDistancePanel();
		tabber.add("Gradients", gradientPanel);
		
		frame.setSize(600, 400);
		frame.setVisible(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			var gpx = GPX.read(args[0]);		
			var track = new GpxFile(null, gpx);
			var segment = track.getGpx().getTracks().get(0).getSegments().get(0);
			
			speedOverTimePanel.setGpxTrackSegment(segment);
			distanceOverTimePanel.setGpxTrackSegment(segment);
			heightProfilePanel.setGpxTrackSegment(segment);
			gradientPanel.setGpxTrackSegment(segment);
		} catch (Exception e) {
			log.error("blah", e);
		}
	}
}
