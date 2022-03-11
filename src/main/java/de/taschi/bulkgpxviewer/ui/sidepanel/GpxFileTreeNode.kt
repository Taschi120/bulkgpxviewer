package de.taschi.bulkgpxviewer.ui.sidepanel

import de.taschi.bulkgpxviewer.files.GpxFile
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/**
 * A [JTree] node representing a [GpxFile] in the [SidePanel].
 */
class GpxFileTreeNode(val track: GpxFile) : DefaultMutableTreeNode(track.fileName.fileName), GpxFileRelatedNode {
    private var startDateNode: StartDateTreeNode? = null
    private val trackLengthNode: DistanceNode
    private val durationNode: DurationTreeNode
    private val avgSpeedNode: AvgSpeedNode
    private val tagNode: TagNode

    init {
        makeOrUpdateStartDateNode()
        trackLengthNode = DistanceNode(this)
        add(trackLengthNode)
        durationNode = DurationTreeNode(this)
        add(durationNode)
        avgSpeedNode = AvgSpeedNode(this)
        add(avgSpeedNode)
        tagNode = TagNode(this)
        add(tagNode)
    }

    /**
     * Update this node, and all child nodes, if needed.
     */
    override fun update() {
        setUserObject(track.fileName.fileName)
        makeOrUpdateStartDateNode()
        trackLengthNode.update()
        durationNode.update()
        avgSpeedNode.update()
        tagNode.update()
    }

    private fun makeOrUpdateStartDateNode() {
        if (startDateNode != null) {
            // update existing node
            startDateNode!!.update()
        } else {
            // create new node
            startDateNode = StartDateTreeNode(this)
            add(startDateNode)
        }
    }

    override val gpxFileTreeNode: GpxFileTreeNode
        get() = this

    companion object {
        private const val serialVersionUID = 6138013700158255219L
    }
}