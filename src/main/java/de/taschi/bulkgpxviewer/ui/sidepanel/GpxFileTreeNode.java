package de.taschi.bulkgpxviewer.ui.sidepanel;

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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.geo.GpxFile;
import de.taschi.bulkgpxviewer.math.DurationCalculator;
import de.taschi.bulkgpxviewer.math.DurationFormatter;
import de.taschi.bulkgpxviewer.math.RouteLengthCalculator;
import de.taschi.bulkgpxviewer.math.SpeedCalculator;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.ui.Messages;

/**
 * A {@link JTree} node representing a {@link GpxFile} in the {@link SidePanel}.
 */
public class GpxFileTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 6138013700158255219L;

	private static final DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME;
		
	private final GpxFile track;
	
	private DefaultMutableTreeNode startDateNode;
	private DefaultMutableTreeNode trackLengthNode;
	private DefaultMutableTreeNode durationNode;
	private DefaultMutableTreeNode avgSpeedNode;
		
	public GpxFileTreeNode(GpxFile track) {
		super(track.getFileName().getFileName());

		this.track = track;
		makeOrUpdateStartDateNode();
		
		trackLengthNode = new DefaultMutableTreeNode(getTrackLengthLabel()); 
		add(trackLengthNode);		
		
		durationNode = new DefaultMutableTreeNode(getDurationLabel());
		add(durationNode);
		
		avgSpeedNode = new DefaultMutableTreeNode(getAvgSpeedLabel());
		add(avgSpeedNode);
	}

	/**
	 * Update this node, and all child nodes, if needed.
	 */
	public void update() {
		this.setUserObject(track.getFileName().getFileName());
		
		makeOrUpdateStartDateNode();
		trackLengthNode.setUserObject(getTrackLengthLabel());
	}
	
	private void makeOrUpdateStartDateNode() {
		var startedAt = track.getStartedAt();
		
		if (startDateNode != null) {
			// update existing node
			startDateNode.setUserObject(getStartDateLabel(startedAt));
		} else {
			// create new node
			startDateNode = new DefaultMutableTreeNode(getStartDateLabel(startedAt));
			add(startDateNode);
		}
	}
	
	private String getStartDateLabel(Optional<Instant> startedAt) {
		var formattedDate = startedAt
				.map(it -> it.atZone(ZoneId.systemDefault()).format(dtf))
				.orElse(Messages.getString("GpxFileTreeNode.unknown")); //$NON-NLS-1$
		
		return String.format(Messages.getString("GpxFileTreeNode.StartedAt"), formattedDate); //$NON-NLS-1$
	}
	
	private String getTrackLengthLabel() {
		var unitSystem = SettingsManager.getInstance().getSettings().getUnitSystem();
		var distance = RouteLengthCalculator.getInstance().getFormattedTotalDistance(track.getGpx(), unitSystem);
		
		return String.format(Messages.getString("GpxFileTreeNode.TrackLength"), distance);  //$NON-NLS-1$		
	}
	
	private String getDurationLabel() {
		var formattedDuration = DurationCalculator.getInstance().getRecordedDurationForGpxFile(track.getGpx())
				.map(DurationFormatter.getInstance()::format)
				.orElse(Messages.getString("GpxFileTreeNode.unknown")); //$NON-NLS-1$
				
		return String.format(Messages.getString("GpxFileTreeNode.Duration"), formattedDuration);//$NON_NLS-1$ //$NON-NLS-1$
	}
	
	private String getAvgSpeedLabel() {
		var unitSystem = SettingsManager.getInstance().getSettings().getUnitSystem();
		var formattedSpeed = SpeedCalculator.getInstance().getFormattedAverageSpeed(track.getGpx(), unitSystem);
		
		return String.format(Messages.getString("GpxFileTreeNode.AverageSpeedLabelFormat"), formattedSpeed); //$NON-NLS-1$
	}

	public GpxFile getTrack() {
		return track;
	}
	
	
	
}
