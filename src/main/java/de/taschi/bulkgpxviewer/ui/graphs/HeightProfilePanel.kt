package de.taschi.bulkgpxviewer.ui.graphs

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager
import io.jenetics.jpx.TrackSegment
import org.jfree.data.xy.XYDataset

class HeightProfilePanel : AbstractGraphPanel() {
    @Inject
    private val trackStatisticsManager: TrackStatisticsManager? = null

    init {
        Application.Companion.getInjector().injectMembers(this)
    }

    override val xAxisLabel: String
        get() = "Distance (km)"
    override val yAxisLabel: String
        get() = "Altitude (m)"

    override fun getDataset(segment: TrackSegment?): XYDataset? {
        return trackStatisticsManager!!.getHeightProfileAsXY(segment)
    }

    companion object {
        private const val serialVersionUID = -4042051302989229070L
    }
}