package de.taschi.bulkgpxviewer.ui.sidepanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;
import de.taschi.bulkgpxviewer.ui.Messages;

/**
 * A {@link JTree} node representing a {@link GpxViewerTrack} in the {@link SidePanel}.
 */
public class GpxFileTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 6138013700158255219L;

	private static final DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME;
		
	private final GpxViewerTrack track;
	
	private DefaultMutableTreeNode startDateNode;
	private DefaultMutableTreeNode trackLengthNode;
		
	public GpxFileTreeNode(GpxViewerTrack track) {
		super(track.getFileName().getFileName());

		this.track = track;
		makeOrUpdateStartDateNode();
		
		trackLengthNode = new DefaultMutableTreeNode(getTrackLengthLabel()); 
		add(trackLengthNode);		
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
		Instant startedAt = track.getStartedAt();
		if (startedAt != null) {
			if (startDateNode != null) {
				// update existing node
				startDateNode.setUserObject(getStartDateLabel(startedAt));
			} else {
				// create new node
				startDateNode = new DefaultMutableTreeNode(getStartDateLabel(startedAt));
				add(startDateNode);
			}
		} else if (startDateNode != null) {
			// remove existing node
			this.remove(startDateNode);
			startDateNode = null;
		}
	}
	
	private String getStartDateLabel(Instant startedAt) {
		return Messages.getString("SidePanel.StartedAt")  //$NON-NLS-1$
				+ startedAt.atZone(ZoneId.systemDefault()).format(dtf);
	}
	
	private String getTrackLengthLabel() {
		if (SettingsManager.getInstance().getSettings().getUnitSystem() == UnitSystem.METRIC) {
			return Messages.getString("SidePanel.RouteLength")  //$NON-NLS-1$
					+ track.getRouteLengthInKilometers() 
					+ Messages.getString("SidePanel.Unit_km"); //$NON-NLS-1$
		} else {
			return Messages.getString("SidePanel.RouteLength")  //$NON-NLS-1$
					+ track.getRouteLengthInMiles() 
					+ Messages.getString("SidePanel.Unit_miles"); //$NON-NLS-1$
		}
	}

	public GpxViewerTrack getTrack() {
		return track;
	}
	
}
