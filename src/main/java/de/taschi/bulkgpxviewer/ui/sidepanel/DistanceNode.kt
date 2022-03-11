package de.taschi.bulkgpxviewer.ui.sidepanel

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.math.RouteLengthCalculator
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.Messages
import javax.swing.tree.DefaultMutableTreeNode

class DistanceNode(parent: GpxFileTreeNode) : DefaultMutableTreeNode(), GpxFileRelatedNode {
    @Inject
    private lateinit var routeLengthCalculator: RouteLengthCalculator

    @Inject
    private lateinit var settingsManager: SettingsManager

    val parentNode: GpxFileTreeNode

    init {
        this.parentNode = parent
        Application.getInjector().injectMembers(this)
        update()
    }

    override val gpxFileTreeNode: GpxFileTreeNode
        get() = parentNode

    private fun getLabel(file: GpxFile): String {
        val unitSystem = settingsManager.settings?.unitSystem ?: UnitSystem.METRIC
        val distance = routeLengthCalculator.getFormattedTotalDistance(parentNode.track.gpx, unitSystem)
        return String.format(Messages.getString("GpxFileTreeNode.TrackLength"), distance) //$NON-NLS-1$	
    }

    override fun update() {
        setUserObject(getLabel(parentNode.track))
    }

    companion object {
        private const val serialVersionUID = 4744651843197435586L
    }
}