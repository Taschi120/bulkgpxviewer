package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.inject.Inject;
import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.math.DurationCalculator;
import de.taschi.bulkgpxviewer.math.DurationFormatter;
import de.taschi.bulkgpxviewer.ui.Messages;

public class DurationTreeNode extends DefaultMutableTreeNode implements GpxFileRelatedNode {

	private static final long serialVersionUID = 5167185250543279471L;
	
	@Inject
	private DurationCalculator durationCalculator;
	
	@Inject
	private DurationFormatter durationFormatter;
	
	protected final GpxFileTreeNode parent;
	
	public DurationTreeNode(GpxFileTreeNode parent) {
		this.parent = parent;
		
		Application.getInjector().injectMembers(this);
		
		update();
	}
	
	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return parent;
	}
	
	private String getLabel(GpxFile file) {
		var formattedDuration = durationCalculator.getRecordedDurationForGpxFile(parent.getTrack().getGpx())
				.map(durationFormatter::format)
				.orElse(Messages.getString("GpxFileTreeNode.unknown")); //$NON-NLS-1$
				
		return String.format(Messages.getString("GpxFileTreeNode.Duration"), formattedDuration);//$NON_NLS-1$ //$NON-NLS-1$
	}

	public void update() {
		setUserObject(getLabel(parent.getTrack()));
	}

}
