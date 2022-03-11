package de.taschi.bulkgpxviewer.ui.sidepanel

import de.taschi.bulkgpxviewer.files.GpxFile

/**
 * Interface for all GPX tree nodes that are below a [GpxFileTreeNode] in the side view.
 */
interface GpxFileRelatedNode {
    /**
     * Returns the parent [GpxFileTreeNode]
     * @return
     */
    val gpxFileTreeNode: GpxFileTreeNode

    /**
     * Returns the associated [GpxFile]
     * @return
     */
    val gpxFile: GpxFile?
        get() = gpxFileTreeNode.track

    /**
     * Update the node's data. Call this after the GpxFile has been changed.
     */
    fun update()
}