package de.taschi.bulkgpxviewer.ui.sidepanel

import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.math.SpeedCalculator
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.Messages
import javax.inject.Inject
import javax.swing.tree.DefaultMutableTreeNode

/**
 *
 * Note that this class needs to be instantiated through
 */
class AvgSpeedNode(parent: GpxFileTreeNode) : DefaultMutableTreeNode(), GpxFileRelatedNode {
    @Inject
    private val speedCalculator: SpeedCalculator? = null

    @Inject
    private val settingsManager: SettingsManager? = null

    protected val parentNode: GpxFileTreeNode

    init {
        parentNode = parent
        Application.Companion.getInjector().injectMembers(this)
        update()
    }

    override val gpxFileTreeNode: GpxFileTreeNode
        get() = parentNode

    private fun getLabel(file: GpxFile): String {
        val unitSystem = settingsManager?.settings?.unitSystem ?: UnitSystem.METRIC
        val formattedSpeed = speedCalculator!!.getFormattedAverageSpeed(parentNode.track.gpx, unitSystem)
        return String.format(
            Messages.getString("GpxFileTreeNode.AverageSpeedLabelFormat"),
            formattedSpeed
        ) //$NON-NLS-1$
    }

    override fun update() {
        setUserObject(getLabel(parentNode.track))
    }

    companion object {
        private const val serialVersionUID = -123087122558756515L
    }
}