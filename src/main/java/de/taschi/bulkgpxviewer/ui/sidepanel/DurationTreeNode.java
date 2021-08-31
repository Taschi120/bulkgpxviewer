package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.math.DurationCalculator;
import de.taschi.bulkgpxviewer.math.DurationFormatter;
import de.taschi.bulkgpxviewer.ui.Messages;

public class DurationTreeNode extends DefaultMutableTreeNode implements GpxFileSubnode {

	private static final long serialVersionUID = 5167185250543279471L;
	
	final GpxFileTreeNode parent;
	
	public DurationTreeNode(GpxFileTreeNode parent) {
		this.parent = parent;
		update();
	}
	
	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return parent;
	}
	
	private String getLabel(GpxFile file) {
		var formattedDuration = DurationCalculator.getInstance().getRecordedDurationForGpxFile(parent.getTrack().getGpx())
				.map(DurationFormatter.getInstance()::format)
				.orElse(Messages.getString("GpxFileTreeNode.unknown")); //$NON-NLS-1$
				
		return String.format(Messages.getString("GpxFileTreeNode.Duration"), formattedDuration);//$NON_NLS-1$ //$NON-NLS-1$
	}

	public void update() {
		setUserObject(getLabel(parent.getTrack()));
	}

}
