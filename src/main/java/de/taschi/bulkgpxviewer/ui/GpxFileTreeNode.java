package de.taschi.bulkgpxviewer.ui;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.gpx.GpxViewerTrack;

/**
 * A {@link JTree} node representing a {@link GpxViewerTrack} in the {@link SidePanel}.
 */
public class GpxFileTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 6138013700158255219L;

	private static final DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	private GpxViewerTrack track;
	
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
		return Messages.getString("SidePanel.RouteLength")  //$NON-NLS-1$
				+ track.getRouteLengthInKilometers() 
				+ Messages.getString("SidePanel.Unit_km"); //$NON-NLS-1$
	}
}
