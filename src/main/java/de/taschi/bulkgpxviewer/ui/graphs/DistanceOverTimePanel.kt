package de.taschi.bulkgpxviewer.ui.graphs

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager
import io.jenetics.jpx.TrackSegment
import org.jfree.data.xy.XYDataset

class DistanceOverTimePanel : AbstractGraphPanel() {
    @Inject
    private val trackStatisticsManager: TrackStatisticsManager? = null

    init {
        Application.Companion.getInjector().injectMembers(this)
    }

    override val xAxisLabel: String
        get() = "Time (s)"
    override val yAxisLabel: String
        get() = "Distance (km)"

    override fun getDataset(segment: TrackSegment?): XYDataset? {
        return trackStatisticsManager!!.getDistanceOverTimeAsXY(segment!!)
    }

    companion object {
        private const val serialVersionUID = -2962417946885383838L
    }
}