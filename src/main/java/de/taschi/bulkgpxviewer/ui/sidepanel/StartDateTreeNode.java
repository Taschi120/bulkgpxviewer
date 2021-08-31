package de.taschi.bulkgpxviewer.ui.sidepanel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.ui.Messages;

public class StartDateTreeNode extends DefaultMutableTreeNode implements GpxFileSubnode {

	private static final long serialVersionUID = -5351575012833312751L;
	private static final DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME;	
	
	final GpxFileTreeNode parent;
	
	public StartDateTreeNode(GpxFileTreeNode parent) {
		this.parent = parent;
		update();
	}
	
	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return parent;
	}
	
	private String getLabel(GpxFile file) {
		var startedAt = file.getStartedAt();
		var formattedDate = startedAt
				.map(it -> it.atZone(ZoneId.systemDefault()).format(dtf))
				.orElse(Messages.getString("GpxFileTreeNode.unknown")); //$NON-NLS-1$
		
		return String.format(Messages.getString("GpxFileTreeNode.StartedAt"), formattedDate); //$NON-NLS-1$
	}

	public void update() {
		setUserObject(getLabel(parent.getTrack()));
	}

}
