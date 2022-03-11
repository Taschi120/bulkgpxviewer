package de.taschi.bulkgpxviewer.ui.sidepanel

import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.ui.Messages
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.swing.tree.DefaultMutableTreeNode

class StartDateTreeNode(override val gpxFileTreeNode: GpxFileTreeNode) : DefaultMutableTreeNode(), GpxFileRelatedNode {

    init {
        update()
    }

    private fun getLabel(file: GpxFile): String {
        val startedAt = file.startedAt
        val formattedDate = startedAt
            .map { it.atZone(ZoneId.systemDefault()).format(dtf) }
            .orElse(Messages.getString("GpxFileTreeNode.unknown")) //$NON-NLS-1$
        return String.format(Messages.getString("GpxFileTreeNode.StartedAt"), formattedDate) //$NON-NLS-1$
    }

    override fun update() {
        setUserObject(getLabel(gpxFileTreeNode.track))
    }

    companion object {
        private const val serialVersionUID = -5351575012833312751L
        private val dtf = DateTimeFormatter.RFC_1123_DATE_TIME
    }
}