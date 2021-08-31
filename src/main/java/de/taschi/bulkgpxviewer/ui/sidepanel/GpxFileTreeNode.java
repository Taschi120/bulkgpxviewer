package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.files.GpxFile;

/**
 * A {@link JTree} node representing a {@link GpxFile} in the {@link SidePanel}.
 */
public class GpxFileTreeNode extends DefaultMutableTreeNode implements GpxFileRelatedNode {
	
	private static final long serialVersionUID = 6138013700158255219L;

	private final GpxFile track;
	
	private StartDateTreeNode startDateNode;
	private DistanceNode trackLengthNode;
	private DurationTreeNode durationNode;
	private AvgSpeedNode avgSpeedNode;
		
	public GpxFileTreeNode(GpxFile track) {
		super(track.getFileName().getFileName());

		this.track = track;
		makeOrUpdateStartDateNode();
		
		trackLengthNode = new DistanceNode(this); 
		add(trackLengthNode);		
		
		durationNode = new DurationTreeNode(this);
		add(durationNode);
		
		avgSpeedNode = new AvgSpeedNode(this);
		add(avgSpeedNode);
	}

	/**
	 * Update this node, and all child nodes, if needed.
	 */
	public void update() {
		this.setUserObject(track.getFileName().getFileName());
		
		makeOrUpdateStartDateNode();
		
		trackLengthNode.update();
		durationNode.update();
		avgSpeedNode.update();
	}
	
	private void makeOrUpdateStartDateNode() {
		if (startDateNode != null) {
			// update existing node
			startDateNode.update();
		} else {
			// create new node
			startDateNode = new StartDateTreeNode(this);
			add(startDateNode);
		}
	}

	public GpxFile getTrack() {
		return track;
	}

	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return this;
	}
}
