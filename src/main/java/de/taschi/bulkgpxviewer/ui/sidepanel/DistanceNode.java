package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.swing.tree.DefaultMutableTreeNode;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.math.RouteLengthCalculator;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.ui.Messages;

public class DistanceNode extends DefaultMutableTreeNode implements GpxFileRelatedNode {
	
	private static final long serialVersionUID = 4744651843197435586L;
	
	@Inject
	private RouteLengthCalculator routeLengthCalculator;
	
	@Inject
	private SettingsManager settingsManager;
	
	final GpxFileTreeNode parent;
	
	public DistanceNode(GpxFileTreeNode parent) {
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
		var distance = routeLengthCalculator.getFormattedTotalDistance(parent.getTrack().getGpx(), unitSystem);
		
		return String.format(Messages.getString("GpxFileTreeNode.TrackLength"), distance);  //$NON-NLS-1$	
	}

	public void update() {
		setUserObject(getLabel(parent.getTrack()));
	}

}
