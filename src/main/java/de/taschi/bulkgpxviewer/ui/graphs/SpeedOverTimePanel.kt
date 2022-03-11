package de.taschi.bulkgpxviewer.ui.graphs

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import io.jenetics.jpx.TrackSegment
import org.jfree.data.xy.XYDataset

class SpeedOverTimePanel : AbstractGraphPanel() {

    @Inject
    private val trackStatisticsManager: TrackStatisticsManager? = null

    init {
        Application.getInjector().injectMembers(this)
    }

    override val xAxisLabel: String
        get() = "Time [minutes]"
    override val yAxisLabel: String
        get() {
            val unitSystem = settingsManager?.settings?.unitSystem ?: UnitSystem.METRIC
            return String.format("Speed [%s]", unitSystem.defaultSpeedUnit)
        }

    override fun getDataset(segment: TrackSegment?): XYDataset {
        return trackStatisticsManager!!.getSpeedOverTimeAsXY(
            segment,
            settingsManager?.settings?.unitSystem ?: UnitSystem.METRIC
        )
    }

    companion object {
        private const val serialVersionUID = -553811986061884999L
    }
}