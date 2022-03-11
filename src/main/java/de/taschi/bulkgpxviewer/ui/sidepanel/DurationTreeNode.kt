package de.taschi.bulkgpxviewer.ui.sidepanel

import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.math.DurationCalculator
import de.taschi.bulkgpxviewer.math.DurationFormatter
import de.taschi.bulkgpxviewer.ui.Messages
import java.time.Duration
import javax.inject.Inject
import javax.swing.tree.DefaultMutableTreeNode

class DurationTreeNode(parent: GpxFileTreeNode) : DefaultMutableTreeNode(), GpxFileRelatedNode {
    @Inject
    private val durationCalculator: DurationCalculator? = null

    @Inject
    private val durationFormatter: DurationFormatter? = null
    protected val parentNode: GpxFileTreeNode

    init {
        this.parentNode = parent
        Application.Companion.getInjector().injectMembers(this)
        update()
    }

    override val gpxFileTreeNode: GpxFileTreeNode
        get() = parentNode

    private fun getLabel(file: GpxFile): String {
        val formattedDuration = durationCalculator!!.getRecordedDurationForGpxFile(parentNode.track.gpx)
            .map { duration: Duration? -> durationFormatter!!.format(duration) }
            .orElse(Messages.getString("GpxFileTreeNode.unknown")) //$NON-NLS-1$
        return String.format(
            Messages.getString("GpxFileTreeNode.Duration"),
            formattedDuration
        ) //$NON_NLS-1$ //$NON-NLS-1$
    }

    override fun update() {
        setUserObject(getLabel(parentNode.track))
    }

    companion object {
        private const val serialVersionUID = 5167185250543279471L
    }
}