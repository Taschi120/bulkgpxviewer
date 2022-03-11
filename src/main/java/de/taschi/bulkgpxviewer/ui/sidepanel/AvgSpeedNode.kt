package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.inject.Inject;
import javax.swing.tree.DefaultMutableTreeNode;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.math.SpeedCalculator;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.ui.Messages;

/**
 * 
 * Note that this class needs to be instantiated through 
 */
public class AvgSpeedNode extends DefaultMutableTreeNode implements GpxFileRelatedNode {
		
	private static final long serialVersionUID = -123087122558756515L;
	
	@Inject
	private SpeedCalculator speedCalculator;
	
	@Inject
	private SettingsManager settingsManager;
	
	protected final GpxFileTreeNode parent;
	
	public AvgSpeedNode(GpxFileTreeNode parent) {
		this.parent = parent;
		
		Application.getInjector().injectMembers(this);

		update();
	}
	
	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return parent;
	}
	
	private String getLabel(GpxFile file) {
		var unitSystem = settingsManager.getSettings().getUnitSystem();
		var formattedSpeed = speedCalculator.getFormattedAverageSpeed(parent.getTrack().getGpx(), unitSystem);
		
		return String.format(Messages.getString("GpxFileTreeNode.AverageSpeedLabelFormat"), formattedSpeed); //$NON-NLS-1$
	}

	public void update() {
		setUserObject(getLabel(parent.getTrack()));
	}

}
