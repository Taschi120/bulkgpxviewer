package de.taschi.bulkgpxviewer.ui.graphs

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.settings.SettingsUpdateListener
import io.jenetics.jpx.TrackSegment
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.block.BlockBorder
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataset
import java.awt.BasicStroke
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel

abstract class AbstractGraphPanel : JPanel(), SettingsUpdateListener {
    @Inject
    protected var settingsManager: SettingsManager? = null
    private var currentSegment: TrackSegment? = null
    private val plot: XYPlot
    private var chart: JFreeChart
    private val chartPanel: ChartPanel

    init {
        Application.Companion.getInjector().injectMembers(this)
        layout = BorderLayout()
        plot = XYPlot()
        chart = JFreeChart(plot)
        chartPanel = ChartPanel(chart)
        val renderer = XYLineAndShapeRenderer()
        renderer.setSeriesPaint(0, Color.RED)
        renderer.setSeriesStroke(0, BasicStroke(1.0f))
        plot.renderer = renderer
        plot.backgroundPaint = Color.white
        plot.isRangeGridlinesVisible = true
        plot.rangeGridlinePaint = Color.BLACK
        plot.isDomainGridlinesVisible = true
        plot.domainGridlinePaint = Color.BLACK
        add(chartPanel, BorderLayout.CENTER)
        settingsManager!!.addSettingsUpdateListener(this)
    }

    fun setGpxTrackSegment(segment: TrackSegment?) {
        currentSegment = segment
        val dataset = getDataset(segment)
        plot.dataset = dataset
        chart = makeChart(dataset)
        chartPanel.chart = chart
        chartPanel.repaint()
    }

    private fun makeChart(dataset: XYDataset?): JFreeChart {
        val chart = ChartFactory.createXYLineChart(
            null,
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        )
        val plot = chart.xyPlot
        val renderer = XYLineAndShapeRenderer()
        renderer.setSeriesPaint(0, Color.RED)
        renderer.setSeriesStroke(0, BasicStroke(1.0f))
        renderer.setSeriesShapesVisible(0, false)
        plot.renderer = renderer
        plot.backgroundPaint = Color.white
        plot.isRangeGridlinesVisible = true
        plot.rangeGridlinePaint = Color.GRAY
        plot.isDomainGridlinesVisible = true
        plot.domainGridlinePaint = Color.GRAY
        plot.isDomainCrosshairVisible = false
        chart.legend.frame = BlockBorder.NONE
        return chart
    }

    override fun onSettingsUpdated() {
        // Force re-creation of the diagram because unit system might have changed
        setGpxTrackSegment(currentSegment)
    }

    abstract val xAxisLabel: String
    abstract val yAxisLabel: String
    abstract fun getDataset(segment: TrackSegment?): XYDataset?

    companion object {
        private const val serialVersionUID = 1948085630207508743L
    }
}