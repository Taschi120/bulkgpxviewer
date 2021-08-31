package de.taschi.bulkgpxviewer.ui.sidepanel;

import de.taschi.bulkgpxviewer.files.GpxFile;

/**
 * Interface for all GPX tree nodes that are below a {@linkplain GpxFileTreeNode} in the side view.
 */
public interface GpxFileSubnode {
	
	/**
	 * Returns the parent {@link GpxFileTreeNode}
	 * @return
	 */
	public GpxFileTreeNode getGpxFileTreeNode();
	
	/**
	 * Returns the associated {@linkplain GpxFile}
	 * @return
	 */
	public default GpxFile getGpxFile() {
		return getGpxFileTreeNode().getTrack();
	}
	
	
	/**
	 * Update the node's data. Call this after the GpxFile has been changed.
	 */
	public void update();
}
