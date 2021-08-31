package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.math.SpeedCalculator;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.ui.Messages;

public class AvgSpeedNode extends DefaultMutableTreeNode implements GpxFileSubnode {
		
	private static final long serialVersionUID = -123087122558756515L;
	
	final GpxFileTreeNode parent;
	
	public AvgSpeedNode(GpxFileTreeNode parent) {
		this.parent = parent;
		update();
	}
	
	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return parent;
	}
	
	private String getLabel(GpxFile file) {
		var unitSystem = SettingsManager.getInstance().getSettings().getUnitSystem();
		var formattedSpeed = SpeedCalculator.getInstance().getFormattedAverageSpeed(parent.getTrack().getGpx(), unitSystem);
		
		return String.format(Messages.getString("GpxFileTreeNode.AverageSpeedLabelFormat"), formattedSpeed); //$NON-NLS-1$
	}

	public void update() {
		setUserObject(getLabel(parent.getTrack()));
	}

}
