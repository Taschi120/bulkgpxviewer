package de.taschi.bulkgpxviewer.ui.windowbuilder;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import io.jenetics.jpx.WayPoint;

public class EditingPanel extends JPanel {
	
	private GpxViewerTrack track;
	private List<WayPoint> selection = Collections.<WayPoint>emptyList();

	private static final long serialVersionUID = 6937011900054488316L;
	private JLabel firstSelectedPointLabel;
	private JButton firstSelectionTransportBack;
	private JButton firstSelectionTransportForward;
	private JButton secondSelectionTransportBack;
	private JButton secondSelectionDeselect;
	private JButton cropAfter;
	private JButton secondSelectionTransportForward;
	private JButton cropBetween;
	private JButton cropBefore;
	private JButton firstSelectionDeselect;
	private JLabel secondSelectedPointLabel;

	/**
	 * Create the panel.
	 */
	public EditingPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_2);
		
		JButton btnNewButton_2 = new JButton("Cancel");
		panel_2.add(btnNewButton_2);
		
		JButton btnNewButton_1 = new JButton("Save");
		panel_2.add(btnNewButton_1);
		
		JPanel panel_3 = new JPanel();
		add(panel_3, BorderLayout.CENTER);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_3.rowHeights = new int[] {0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0};
		panel_3.setLayout(gbl_panel_3);
		
		JLabel lblNewLabel = new JLabel("First selected point: ");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_3.add(lblNewLabel, gbc_lblNewLabel);
		
		firstSelectedPointLabel = new JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST");
		GridBagConstraints gbc_firstSelectedPointLabel = new GridBagConstraints();
		gbc_firstSelectedPointLabel.anchor = GridBagConstraints.WEST;
		gbc_firstSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstSelectedPointLabel.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectedPointLabel.gridx = 1;
		gbc_firstSelectedPointLabel.gridy = 0;
		panel_3.add(firstSelectedPointLabel, gbc_firstSelectedPointLabel);
		
		firstSelectionTransportBack = new JButton("<");
		GridBagConstraints gbc_firstSelectionTransportBack = new GridBagConstraints();
		gbc_firstSelectionTransportBack.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectionTransportBack.gridx = 2;
		gbc_firstSelectionTransportBack.gridy = 0;
		panel_3.add(firstSelectionTransportBack, gbc_firstSelectionTransportBack);
		
		firstSelectionDeselect = new JButton("X");
		GridBagConstraints gbc_firstSelectionDeselect = new GridBagConstraints();
		gbc_firstSelectionDeselect.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectionDeselect.gridx = 3;
		gbc_firstSelectionDeselect.gridy = 0;
		panel_3.add(firstSelectionDeselect, gbc_firstSelectionDeselect);
		
		firstSelectionTransportForward = new JButton(">");
		GridBagConstraints gbc_firstSelectionTransportForward = new GridBagConstraints();
		gbc_firstSelectionTransportForward.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectionTransportForward.gridx = 4;
		gbc_firstSelectionTransportForward.gridy = 0;
		panel_3.add(firstSelectionTransportForward, gbc_firstSelectionTransportForward);
		
		cropBefore = new JButton("Crop Before");
		GridBagConstraints gbc_cropBefore = new GridBagConstraints();
		gbc_cropBefore.fill = GridBagConstraints.HORIZONTAL;
		gbc_cropBefore.insets = new Insets(0, 0, 5, 5);
		gbc_cropBefore.gridx = 5;
		gbc_cropBefore.gridy = 0;
		panel_3.add(cropBefore, gbc_cropBefore);
		
		cropBetween = new JButton("Crop Between");
		GridBagConstraints gbc_cropBetween = new GridBagConstraints();
		gbc_cropBetween.fill = GridBagConstraints.HORIZONTAL;
		gbc_cropBetween.insets = new Insets(0, 0, 5, 5);
		gbc_cropBetween.gridx = 5;
		gbc_cropBetween.gridy = 1;
		panel_3.add(cropBetween, gbc_cropBetween);
		
		JLabel label2 = new JLabel("Second selected point:");
		GridBagConstraints gbc_label2 = new GridBagConstraints();
		gbc_label2.insets = new Insets(0, 0, 5, 5);
		gbc_label2.gridx = 0;
		gbc_label2.gridy = 2;
		panel_3.add(label2, gbc_label2);
		
		secondSelectedPointLabel = new JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST");
		GridBagConstraints gbc_secondSelectedPointLabel = new GridBagConstraints();
		gbc_secondSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_secondSelectedPointLabel.anchor = GridBagConstraints.WEST;
		gbc_secondSelectedPointLabel.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectedPointLabel.gridx = 1;
		gbc_secondSelectedPointLabel.gridy = 2;
		panel_3.add(secondSelectedPointLabel, gbc_secondSelectedPointLabel);
		
		secondSelectionTransportBack = new JButton("<");
		GridBagConstraints gbc_secondSelectionTransportBack = new GridBagConstraints();
		gbc_secondSelectionTransportBack.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectionTransportBack.gridx = 2;
		gbc_secondSelectionTransportBack.gridy = 2;
		panel_3.add(secondSelectionTransportBack, gbc_secondSelectionTransportBack);
		
		secondSelectionDeselect = new JButton("X");
		GridBagConstraints gbc_secondSelectionDeselect = new GridBagConstraints();
		gbc_secondSelectionDeselect.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectionDeselect.gridx = 3;
		gbc_secondSelectionDeselect.gridy = 2;
		panel_3.add(secondSelectionDeselect, gbc_secondSelectionDeselect);
		
		secondSelectionTransportForward = new JButton(">\r\n");
		GridBagConstraints gbc_secondSelectionTransportForward = new GridBagConstraints();
		gbc_secondSelectionTransportForward.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectionTransportForward.gridx = 4;
		gbc_secondSelectionTransportForward.gridy = 2;
		panel_3.add(secondSelectionTransportForward, gbc_secondSelectionTransportForward);
		
		cropAfter = new JButton("Crop After");
		GridBagConstraints gbc_cropAfter = new GridBagConstraints();
		gbc_cropAfter.fill = GridBagConstraints.HORIZONTAL;
		gbc_cropAfter.insets = new Insets(0, 0, 5, 5);
		gbc_cropAfter.gridx = 5;
		gbc_cropAfter.gridy = 2;
		panel_3.add(cropAfter, gbc_cropAfter);
	}

	public void setSelection(Set<WayPoint> selection) {
		this.selection = new ArrayList<>(selection);
		disableAndEnableButtons();
		updateLabels();
	}
	
	public void setTrack(GpxViewerTrack track) {
		this.track = track;
	}

	/**
	 * update the component labels
	 */
	private void updateLabels() {
		var size = selection.size();
		
		if (size == 0) {
			firstSelectedPointLabel.setText("");
			secondSelectedPointLabel.setText("");
		}
		else if (size == 1) {
			firstSelectedPointLabel.setText(makeDescriptorString(selection.get(0)));
			secondSelectedPointLabel.setText("");
		} else if (size == 2) {
			selection.sort((o1, o2) -> track.indexOfWayPoint(o2) - track.indexOfWayPoint(o1));
			firstSelectedPointLabel.setText(makeDescriptorString(selection.get(0)));
			secondSelectedPointLabel.setText(makeDescriptorString(selection.get(1)));
		}
	}

	private String makeDescriptorString(WayPoint wayPoint) {
		// TODO Auto-generated method stub
		var lat = doubleToDegMinSec(wayPoint.getLatitude().toDegrees(), "S", "N");
		var lon = doubleToDegMinSec(wayPoint.getLongitude().toDegrees(), "W", "E");
		
		var time = wayPoint.getTime().map(DateTimeFormatter.ISO_LOCAL_DATE_TIME::format).orElse("unknown date");
		return String.format("%s %s @ %s", lat, lon, time);
	}

	private String doubleToDegMinSec(double lat, String negativeSide, String positiveSide) {
		var side = (lat < 0) ? negativeSide : positiveSide;
		lat = Math.abs(lat);
		
		var deg = (int)Math.floor(lat);
		lat -= deg;
		lat *= 60;
		
		var min = (int)Math.floor(lat);
		lat -= min;
		lat *= 60;
		
		var sec = (int)Math.floor(lat);
		
		return String.format("%02d° %02d' %02d\" %s", deg, min, sec, side);
	}

	/**
	 * Update enabled / disabled state of all buttons
	 */
	private void disableAndEnableButtons() {
		boolean firstTransportEnabled = !selection.isEmpty();
		boolean secondTransportEnabled = (selection.size() > 1);
		
		firstSelectionTransportBack.setEnabled(firstTransportEnabled);
		firstSelectionTransportForward.setEnabled(firstTransportEnabled);
		firstSelectionDeselect.setEnabled(firstTransportEnabled);
		
		secondSelectionTransportBack.setEnabled(secondTransportEnabled);
		secondSelectionTransportForward.setEnabled(secondTransportEnabled);
		secondSelectionDeselect.setEnabled(secondTransportEnabled);
		
		cropBefore.setEnabled(firstTransportEnabled);
		cropAfter.setEnabled(firstTransportEnabled);
		cropBetween.setEnabled(secondTransportEnabled);	
	}

}
