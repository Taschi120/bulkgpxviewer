package de.taschi.bulkgpxviewer.ui.windowbuilder;

/*-
 * #%L
 * bulkgpxviewer
 * %%
 * Copyright (C) 2021 S. Hillebrand
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.geo.GpxFile;
import de.taschi.bulkgpxviewer.ui.IconHandler;
import de.taschi.bulkgpxviewer.ui.Messages;
import de.taschi.bulkgpxviewer.ui.map.MapSelectionHandler;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EditingPanel extends JPanel {
	
	private GpxFile track;
	private MapSelectionHandler selectionHandler;
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
	private JButton save;
	private JButton cancel;

	/**
	 * Create the panel.
	 */
	public EditingPanel() {
		setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_2);
		
		cancel = new JButton(Messages.getString("EditingPanel.Cancel")); //$NON-NLS-1$
		cancel.addActionListener(this::onCancel);
		panel_2.add(cancel);
		
		save = new JButton(Messages.getString("EditingPanel.Save")); //$NON-NLS-1$
		save.addActionListener(this::onSave);
		panel_2.add(save);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EmptyBorder(3, 3, 3, 3));
		add(panel_3, BorderLayout.CENTER);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
		gbl_panel_3.rowHeights = new int[] {0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0};
		panel_3.setLayout(gbl_panel_3);
		
		JLabel lblNewLabel = new JLabel(Messages.getString("EditingPanel.PointA")); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_3.add(lblNewLabel, gbc_lblNewLabel);
		
		firstSelectedPointLabel = new JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST"); //$NON-NLS-1$
		GridBagConstraints gbc_firstSelectedPointLabel = new GridBagConstraints();
		gbc_firstSelectedPointLabel.anchor = GridBagConstraints.WEST;
		gbc_firstSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_firstSelectedPointLabel.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectedPointLabel.gridx = 1;
		gbc_firstSelectedPointLabel.gridy = 0;
		panel_3.add(firstSelectedPointLabel, gbc_firstSelectedPointLabel);
		
		firstSelectionTransportBack = new JButton(IconHandler.loadIcon("arrow-left-s-line")); //$NON-NLS-1$
		GridBagConstraints gbc_firstSelectionTransportBack = new GridBagConstraints();
		gbc_firstSelectionTransportBack.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectionTransportBack.gridx = 2;
		gbc_firstSelectionTransportBack.gridy = 0;
		firstSelectionTransportBack.addActionListener(this::onFirstSelectionTransportBack);
		panel_3.add(firstSelectionTransportBack, gbc_firstSelectionTransportBack);
		
		firstSelectionDeselect = new JButton(IconHandler.loadIcon("close-circle-line")); //$NON-NLS-1$
		GridBagConstraints gbc_firstSelectionDeselect = new GridBagConstraints();
		gbc_firstSelectionDeselect.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectionDeselect.gridx = 3;
		gbc_firstSelectionDeselect.gridy = 0;
		firstSelectionDeselect.addActionListener(this::onFirstSelectionDeselect);
		panel_3.add(firstSelectionDeselect, gbc_firstSelectionDeselect);
		
		firstSelectionTransportForward = new JButton(IconHandler.loadIcon("arrow-right-s-line")); //$NON-NLS-1$
		GridBagConstraints gbc_firstSelectionTransportForward = new GridBagConstraints();
		gbc_firstSelectionTransportForward.insets = new Insets(0, 0, 5, 5);
		gbc_firstSelectionTransportForward.gridx = 4;
		gbc_firstSelectionTransportForward.gridy = 0;
		firstSelectionTransportForward.addActionListener(this::onFirstSelectionTransportForward);
		panel_3.add(firstSelectionTransportForward, gbc_firstSelectionTransportForward);
		
		cropBefore = new JButton(Messages.getString("EditingPanel.CropBefore")); //$NON-NLS-1$
		cropBefore.setIcon(IconHandler.loadIcon("scissors-cut-line"));
		GridBagConstraints gbc_cropBefore = new GridBagConstraints();
		gbc_cropBefore.fill = GridBagConstraints.HORIZONTAL;
		gbc_cropBefore.insets = new Insets(0, 0, 5, 5);
		gbc_cropBefore.gridx = 5;
		gbc_cropBefore.gridy = 0;
		cropBefore.addActionListener(this::onCropBefore);
		panel_3.add(cropBefore, gbc_cropBefore);
		
		cropBetween = new JButton(Messages.getString("EditingPanel.CropBetween")); //$NON-NLS-1$
		cropBetween.setIcon(IconHandler.loadIcon("scissors-cut-line"));
		GridBagConstraints gbc_cropBetween = new GridBagConstraints();
		gbc_cropBetween.fill = GridBagConstraints.HORIZONTAL;
		gbc_cropBetween.insets = new Insets(0, 0, 5, 5);
		gbc_cropBetween.gridx = 5;
		gbc_cropBetween.gridy = 1;
		cropBetween.addActionListener(this::onCropBetween);
		panel_3.add(cropBetween, gbc_cropBetween);
		
		JLabel label2 = new JLabel(Messages.getString("EditingPanel.PointB")); //$NON-NLS-1$
		GridBagConstraints gbc_label2 = new GridBagConstraints();
		gbc_label2.anchor = GridBagConstraints.WEST;
		gbc_label2.insets = new Insets(0, 0, 5, 5);
		gbc_label2.gridx = 0;
		gbc_label2.gridy = 2;
		panel_3.add(label2, gbc_label2);
		
		secondSelectedPointLabel = new JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST"); //$NON-NLS-1$
		GridBagConstraints gbc_secondSelectedPointLabel = new GridBagConstraints();
		gbc_secondSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_secondSelectedPointLabel.anchor = GridBagConstraints.WEST;
		gbc_secondSelectedPointLabel.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectedPointLabel.gridx = 1;
		gbc_secondSelectedPointLabel.gridy = 2;
		panel_3.add(secondSelectedPointLabel, gbc_secondSelectedPointLabel);
		
		secondSelectionTransportBack = new JButton(IconHandler.loadIcon("arrow-left-s-line")); //$NON-NLS-1$
		GridBagConstraints gbc_secondSelectionTransportBack = new GridBagConstraints();
		gbc_secondSelectionTransportBack.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectionTransportBack.gridx = 2;
		gbc_secondSelectionTransportBack.gridy = 2;
		secondSelectionTransportBack.addActionListener(this::onSecondSelectionTransportBack);
		panel_3.add(secondSelectionTransportBack, gbc_secondSelectionTransportBack);
		
		secondSelectionDeselect = new JButton(IconHandler.loadIcon("close-circle-line")); //$NON-NLS-1$
		GridBagConstraints gbc_secondSelectionDeselect = new GridBagConstraints();
		gbc_secondSelectionDeselect.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectionDeselect.gridx = 3;
		gbc_secondSelectionDeselect.gridy = 2;
		secondSelectionDeselect.addActionListener(this::onSecondSelectionDeselect);
		panel_3.add(secondSelectionDeselect, gbc_secondSelectionDeselect);
		
		secondSelectionTransportForward = new JButton(IconHandler.loadIcon("arrow-right-s-line")); //$NON-NLS-1$
		GridBagConstraints gbc_secondSelectionTransportForward = new GridBagConstraints();
		gbc_secondSelectionTransportForward.insets = new Insets(0, 0, 5, 5);
		gbc_secondSelectionTransportForward.gridx = 4;
		gbc_secondSelectionTransportForward.gridy = 2;
		secondSelectionTransportForward.addActionListener(this::onSecondSelectionTransportForward);
		panel_3.add(secondSelectionTransportForward, gbc_secondSelectionTransportForward);
		
		cropAfter = new JButton(Messages.getString("EditingPanel.CropAfter")); //$NON-NLS-1$
		cropAfter.setIcon(IconHandler.loadIcon("scissors-cut-line"));
		GridBagConstraints gbc_cropAfter = new GridBagConstraints();
		gbc_cropAfter.fill = GridBagConstraints.HORIZONTAL;
		gbc_cropAfter.insets = new Insets(0, 0, 5, 5);
		gbc_cropAfter.gridx = 5;
		gbc_cropAfter.gridy = 2;
		cropAfter.addActionListener(this::onCropAfter);
		panel_3.add(cropAfter, gbc_cropAfter);
	}

	public void setSelection(Set<WayPoint> selection) {
		this.selection = new ArrayList<>(selection);
		disableAndEnableButtons();
		updateLabels();
	}
	
	public void setTrack(GpxFile track) {
		this.track = track;
	}
	
	public void setSelectionHandler(MapSelectionHandler handler) {
		this.selectionHandler = handler;
	}

	/**
	 * update the component labels
	 */
	private void updateLabels() {
		var size = selection.size();
		
		if (size == 0) {
			firstSelectedPointLabel.setText(Messages.getString("EditingPanel.Empty")); //$NON-NLS-1$
			secondSelectedPointLabel.setText(Messages.getString("EditingPanel.Empty")); //$NON-NLS-1$
		}
		else if (size == 1) {
			firstSelectedPointLabel.setText(makeDescriptorString(selection.get(0)));
			secondSelectedPointLabel.setText(Messages.getString("EditingPanel.Empty")); //$NON-NLS-1$
		} else if (size == 2) {
			selection.sort((o1, o2) -> 
				track.indexOfWayPoint(o2).orElseThrow().compareTo(track.indexOfWayPoint(o1).orElseThrow()));
			firstSelectedPointLabel.setText(makeDescriptorString(selection.get(0)));
			secondSelectedPointLabel.setText(makeDescriptorString(selection.get(1)));
		}
	}

	private String makeDescriptorString(WayPoint wayPoint) {
		var lat = doubleToDegMinSec(wayPoint.getLatitude().toDegrees(), Messages.getString("EditingPanel.South"), Messages.getString("EditingPanel.North")); //$NON-NLS-1$ //$NON-NLS-2$
		var lon = doubleToDegMinSec(wayPoint.getLongitude().toDegrees(), Messages.getString("EditingPanel.West"), Messages.getString("EditingPanel.East")); //$NON-NLS-1$ //$NON-NLS-2$
		
		var time = wayPoint.getTime().map(DateTimeFormatter.ISO_LOCAL_DATE_TIME::format).orElse(Messages.getString("EditingPanel.UnknownDate")); //$NON-NLS-1$
		return String.format(Messages.getString("EditingPanel.WaypointLabelFormat"), lat, lon, time); //$NON-NLS-1$
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
		
		return String.format(Messages.getString("EditingPanel.DegMinSecFormat"), deg, min, sec, side); //$NON-NLS-1$
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

	private void onSave(ActionEvent evt) {
		try {
			track.save();
			JOptionPane.showMessageDialog(Application.getMainWindow().getFrame(), Messages.getString("EditingPanel.SaveSuccessful")); //$NON-NLS-1$
		} catch (Exception e) {
			log.error("Error while saving GPX file", e); //$NON-NLS-1$
			JOptionPane.showMessageDialog(Application.getMainWindow().getFrame(), 
					String.format(Messages.getString("EditingPanel.ErrorWhileSaving"), e.getLocalizedMessage())); //$NON-NLS-1$
		}
	}

	private void onCancel(ActionEvent evt) {
		var reallyCancel = false;
		if (track.isChanged()) {
			var result = JOptionPane.showConfirmDialog(Application.getMainWindow().getFrame(), 
					Messages.getString("EditingPanel.ConfirmCancelWithoutSaving"), Messages.getString("EditingPanel.ReallyQuit"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			
			reallyCancel = (result == JOptionPane.YES_OPTION);
		} else {
			reallyCancel = true;
		}
		
		if (reallyCancel) {
			Application.getMainWindow().exitEditingMode();
		}
	}
	
	private void onFirstSelectionTransportBack(ActionEvent e) {
		if(!selection.isEmpty()) {
			var item = selection.get(0);
			var index = track.indexOfWayPoint(item).get();
			
			if(index.getWaypointId() > 0) {
				WayPoint newSelection = track.getGpx()
						.getTracks().get(index.getTrackId())
						.getSegments().get(index.getSegmentId())
						.getPoints().get(index.getWaypointId() - 1);
				
				selection.set(0, newSelection);
				selectionHandler.setSelection(selection);
			}
		}
	}
	
	private void onFirstSelectionTransportForward(ActionEvent e) {
		if(!selection.isEmpty()) {
			var item = selection.get(0);
			var index = track.indexOfWayPoint(item).get();
			
			var segment = track.getGpx()
					.getTracks().get(index.getTrackId())
					.getSegments().get(index.getSegmentId());
			
			var points = segment.getPoints();
			if(index.getWaypointId() < points.size() - 1) {
				var newSelection = points.get(index.getWaypointId() + 1);
				selection.set(0, newSelection);
				selectionHandler.setSelection(selection);
			}
		}
	}
	
	private void onFirstSelectionDeselect(ActionEvent e) {
		if(!selection.isEmpty()) {
			selection.remove(0);
			selectionHandler.setSelection(selection);
		}
	}

	private void onSecondSelectionTransportBack(ActionEvent actionevent1) {
		if(selection.size() >= 2) {
			var item = selection.get(1);
			var index = track.indexOfWayPoint(item).get();
			
			if(index.getWaypointId() > 0) {
				WayPoint newSelection = track.getGpx()
						.getTracks().get(index.getTrackId())
						.getSegments().get(index.getSegmentId())
						.getPoints().get(index.getWaypointId() - 1);
				
				selection.set(1, newSelection);
				selectionHandler.setSelection(selection);
			}
		}
	}

	private void onSecondSelectionDeselect(ActionEvent e) {
		if(selection.size() >= 2) {
			selection.remove(1);
			selectionHandler.setSelection(selection);
		}
	}
	
	private void onSecondSelectionTransportForward(ActionEvent e) {
		if(selection.size() >= 2) {
			var item = selection.get(1);
			var index = track.indexOfWayPoint(item).get();
			
			var segment = track.getGpx()
					.getTracks().get(index.getTrackId())
					.getSegments().get(index.getSegmentId());
			
			var points = segment.getPoints();
			if(index.getWaypointId() < points.size() - 1) {
				var newSelection = points.get(index.getWaypointId() + 1);
				selection.set(1, newSelection);
				selectionHandler.setSelection(selection);
			}
		}
	}
	
	private void onCropBefore(ActionEvent e) {
		if(!selection.isEmpty()) {
			var endpoint = selection.get(0);
			var index = track.indexOfWayPoint(endpoint).get();
			
			var toBeDeleted = track.getGpx().getTracks().get(index.getTrackId())
					.getSegments().get(index.getSegmentId()).getPoints().subList(0, index.getWaypointId());
			
			final GPX gpx1 = track.getGpx().toBuilder()
			    .trackFilter()
			        .map(track -> track.toBuilder()
			            .map(segment -> segment.toBuilder()
			                .filter((it) -> !toBeDeleted.contains(it))
			                .build())
			            .build())
			        .build()
			    .build();
			
			track.setGpx(gpx1);
		}
	}
	
	private void onCropAfter(ActionEvent e) {
		if(!selection.isEmpty()) {
			var endPoint = selection.get(selection.size() - 1);
			var index = track.indexOfWayPoint(endPoint).get();
			
			var points =  track.getGpx().getTracks().get(index.getTrackId())
					.getSegments().get(index.getSegmentId()).getPoints();
			
			var toBeDeleted = points.subList(index.getWaypointId() + 1, points.size());

			final GPX gpx1 = track.getGpx().toBuilder()
				    .trackFilter()
				        .map(track -> track.toBuilder()
				            .map(segment -> segment.toBuilder()
				                .filter((it) -> !toBeDeleted.contains(it))
				                .build())
				            .build())
				        .build()
				    .build();
							
			track.setGpx(gpx1);
		}

	}
	
	private void onCropBetween(ActionEvent e) {
		if(selection.size() == 2) {
			var point1 = selection.get(0);
			var point2 = selection.get(1);
			
			var idx1 = track.indexOfWayPoint(point1).get();
			var idx2 = track.indexOfWayPoint(point2).get();
			
			if(idx1.isOnSameSegment(idx2)) {
				var i1 = idx1.getWaypointId();
				var i2 = idx2.getWaypointId();
				
				if (i1 > i2) {
					var o = i2;
					i2 = i1;
					i1 = o;
				}
				
				var points =  track.getGpx().getTracks().get(idx1.getTrackId())
						.getSegments().get(idx1.getSegmentId()).getPoints();
				
				var toBeDeleted = points.subList(i1 + 1, i2);
				
				final GPX gpx1 = track.getGpx().toBuilder()
					    .trackFilter()
					        .map(track -> track.toBuilder()
					            .map(segment -> segment.toBuilder()
					                .filter((it) -> !toBeDeleted.contains(it))
					                .build())
					            .build())
					        .build()
					    .build();
								
				track.setGpx(gpx1);

			} else {
				log.error("Selections not on same track: {} and {}", idx1, idx2); //$NON-NLS-1$
			}
		}
	}
}
