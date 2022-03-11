package de.taschi.bulkgpxviewer.math

import javax.swing.JPanel
import org.jxmapviewer.JXMapKit
import de.taschi.bulkgpxviewer.ui.map.TracksPainter
import de.taschi.bulkgpxviewer.ui.map.SelectionPainter
import de.taschi.bulkgpxviewer.ui.map.CompositePainter
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.ui.map.MapSelectionHandler
import java.awt.BorderLayout
import org.jxmapviewer.viewer.TileFactoryInfo
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import de.taschi.bulkgpxviewer.files.LoadedFileChangeListener
import de.taschi.bulkgpxviewer.ui.map.WaypointSelectionChangeListener
import io.jenetics.jpx.WayPoint
import org.jxmapviewer.viewer.GeoPosition
import de.taschi.bulkgpxviewer.geo.GpsBoundingBox
import io.jenetics.jpx.GPX
import io.jenetics.jpx.TrackSegment
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper
import de.taschi.bulkgpxviewer.ui.map.MapPanel
import org.jxmapviewer.JXMapViewer
import de.taschi.bulkgpxviewer.ui.TrackColorUtil
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Color
import java.awt.BasicStroke
import de.taschi.bulkgpxviewer.settings.ColorConverter
import java.util.stream.Collectors
import java.awt.geom.Point2D
import java.awt.Image
import javax.imageio.ImageIO
import java.awt.event.MouseAdapter
import javax.swing.SwingUtilities
import java.lang.Runnable
import de.taschi.bulkgpxviewer.settings.SettingsUpdateListener
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataset
import org.jfree.chart.ChartFactory
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.block.BlockBorder
import de.taschi.bulkgpxviewer.ui.graphs.AbstractGraphPanel
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.sidepanel.GpxFileTreeNode
import javax.swing.tree.DefaultMutableTreeNode
import de.taschi.bulkgpxviewer.ui.sidepanel.GpxFileRelatedNode
import de.taschi.bulkgpxviewer.files.TagManager
import javax.swing.JTree
import java.nio.file.Path
import de.taschi.bulkgpxviewer.ui.sidepanel.GpxFilePopupMenu
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants
import de.taschi.bulkgpxviewer.ui.sidepanel.SidePanel.SidePanelMouseListener
import de.taschi.bulkgpxviewer.ui.sidepanel.SidePanel
import javax.swing.tree.DefaultTreeModel
import java.time.ZonedDateTime
import java.time.ZoneId
import javax.swing.tree.TreeSelectionModel
import de.taschi.bulkgpxviewer.math.SpeedCalculator
import de.taschi.bulkgpxviewer.math.RouteLengthCalculator
import de.taschi.bulkgpxviewer.ui.sidepanel.StartDateTreeNode
import de.taschi.bulkgpxviewer.ui.sidepanel.DistanceNode
import de.taschi.bulkgpxviewer.ui.sidepanel.DurationTreeNode
import de.taschi.bulkgpxviewer.ui.sidepanel.AvgSpeedNode
import de.taschi.bulkgpxviewer.ui.sidepanel.TagNode
import de.taschi.bulkgpxviewer.math.DurationCalculator
import de.taschi.bulkgpxviewer.math.DurationFormatter
import javax.swing.JPopupMenu
import javax.swing.JMenuItem
import de.taschi.bulkgpxviewer.ui.IconHandler
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import javax.swing.JOptionPane
import java.io.IOException
import java.time.format.DateTimeFormatter
import javax.swing.JDialog
import javax.swing.JTextPane
import javax.swing.border.EmptyBorder
import javax.swing.JTabbedPane
import javax.swing.JLabel
import java.awt.FlowLayout
import javax.swing.JButton
import java.io.File
import de.taschi.bulkgpxviewer.ui.windowbuilder.InfoDialog
import kotlin.jvm.JvmStatic
import javax.swing.JFrame
import javax.swing.JSplitPane
import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindowMode
import de.taschi.bulkgpxviewer.ui.windowbuilder.EditingPanelWrapper
import de.taschi.bulkgpxviewer.ui.graphs.SpeedOverTimePanel
import de.taschi.bulkgpxviewer.ui.graphs.HeightProfilePanel
import javax.swing.JMenuBar
import javax.swing.JMenu
import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindow.LocalWindowAdapter
import de.taschi.bulkgpxviewer.settings.dto.MainWindowSettings
import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindow
import de.taschi.bulkgpxviewer.ui.windowbuilder.SettingsWindow
import javax.swing.JFileChooser
import java.awt.HeadlessException
import java.lang.RuntimeException
import java.awt.Desktop
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.border.SoftBevelBorder
import java.awt.GridLayout
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import java.awt.Insets
import de.taschi.bulkgpxviewer.geo.WaypointIndex
import javax.swing.JList
import javax.swing.JComboBox
import javax.swing.DefaultComboBoxModel
import javax.swing.ListSelectionModel
import de.taschi.bulkgpxviewer.settings.dto.Settings
import de.taschi.bulkgpxviewer.ui.ColorListItemRenderer
import javax.swing.JColorChooser
import de.taschi.bulkgpxviewer.ui.windowbuilder.ColorChooserDialog.ReturnCode
import de.taschi.bulkgpxviewer.ui.windowbuilder.EditingPanel
import javax.swing.SwingConstants
import javax.swing.ImageIcon
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor
import javax.swing.ListCellRenderer
import javax.swing.border.Border
import javax.swing.BorderFactory
import de.taschi.bulkgpxviewer.geo.WaypointIndex.WaypointIndexBuilder
import de.taschi.bulkgpxviewer.math.UnitConverter
import java.math.BigDecimal
import java.math.RoundingMode
import de.taschi.bulkgpxviewer.math.HaversineCalculator
import java.util.function.BinaryOperator
import java.util.function.ToDoubleFunction
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager.Calculator
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.lang.IllegalArgumentException
import kotlin.Throws
import org.w3c.dom.NodeList
import java.nio.file.PathMatcher
import java.nio.file.FileSystems
import java.nio.charset.Charset
import java.nio.file.Paths
import com.google.inject.Injector
import com.google.inject.Guice
import de.taschi.bulkgpxviewer.CoreGuiceModule
import javax.swing.UIManager
import com.google.inject.AbstractModule
import de.taschi.bulkgpxviewer.ui.Messages
import io.jenetics.jpx.Length
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Duration
import java.util.*
import java.util.function.Function
import javax.inject.Inject

/**
 * Helper class for calculating and caching detailed statistics
 * for GPX tracks, like distance, speed, altitude etc as data rows (arrays of data per GPX node)
 */
class TrackStatisticsManager constructor() {
    /** Caches  */
    private val distanceDiffs: HashMap<TrackSegment, List<Double>> = HashMap()
    private val totalDistances: HashMap<TrackSegment, List<Double>> = HashMap()
    private val totalTimes: HashMap<TrackSegment, List<Duration>> = HashMap()
    private val timeDiffs: HashMap<TrackSegment, List<Duration>> = HashMap()
    private val speeds: HashMap<TrackSegment, List<Double>> = HashMap()
    private val elevations: HashMap<TrackSegment, List<Double>> = HashMap()
    private val gradients: HashMap<TrackSegment, List<Double>> = HashMap()

    @Inject
    private val haversineCalculator: HaversineCalculator? = null

    @Inject
    private val unitConverter: UnitConverter? = null
    fun getTotalDistance(segment: TrackSegment?): Double {
        return getDistanceDifferences(segment).stream().collect(
            Collectors.summingDouble(
                ToDoubleFunction({ it: Double? -> (it)!! })
            )
        )
    }

    fun getDistanceDifferences(segment: TrackSegment?): List<Double> {
        return resolveCache(
            distanceDiffs,
            segment,
            Calculator<Double>({ segment: TrackSegment? -> calculateDistanceDifferences(segment) })
        )
    }

    private fun calculateDistanceDifferences(segment: TrackSegment?): List<Double?> {
        if (segment == null) {
            return emptyList<Double>()
        }
        val waypoints: List<WayPoint> = segment.getPoints()
        val result: MutableList<Double?> = ArrayList(waypoints.size)
        for (i in waypoints.indices) {
            if (i == 0) {
                result.add(0.0)
            } else {
                val prev: WayPoint = waypoints.get(i - 1)
                val now: WayPoint = waypoints.get(i)
                result.add(haversineCalculator!!.getDistance(prev, now))
            }
        }
        return result
    }

    /**
     * Maps the list of waypoints in a segment to a list of distance from start to waypoint X
     * @param segment
     * @return
     */
    fun getTotalDistances(segment: TrackSegment?): List<Double> {
        return resolveCache(
            totalDistances,
            segment,
            Calculator<Double>({ segment: TrackSegment? -> calculateTotalDistances(segment) })
        )
    }

    private fun calculateTotalDistances(segment: TrackSegment?): List<Double?> {
        if (segment == null) {
            return emptyList<Double>()
        }
        val waypoints: List<WayPoint> = segment.getPoints()
        if (waypoints.size == 0) {
            return emptyList<Double>()
        }
        val result: MutableList<Double?> = ArrayList(waypoints.size)
        var distanceSoFar: Double = 0.0
        var prevWaypoint: WayPoint = waypoints.get(0)
        for (waypoint: WayPoint in waypoints) {
            distanceSoFar += haversineCalculator!!.getDistance(prevWaypoint, waypoint)
            result.add(distanceSoFar)
            prevWaypoint = waypoint
        }
        return result
    }

    /**
     * Maps the list of waypoints in a segment to a list of time elapsed from start until this waypoint.
     * The first value will be 0.
     * @param segment
     * @return
     */
    fun getTotalTimes(segment: TrackSegment?): List<Duration> {
        return resolveCache(
            totalTimes,
            segment,
            Calculator<Duration>({ segment: TrackSegment? -> calculateTotalTimes(segment) })
        )
    }

    private fun calculateTotalTimes(segment: TrackSegment?): List<Duration?> {
        if (segment == null) {
            return emptyList<Duration>()
        }
        val waypoints: List<WayPoint> = segment.getPoints()
        if (waypoints.isEmpty()) {
            return emptyList<Duration>()
        }
        val times: List<ZonedDateTime?> = waypoints.stream()
            .map(Function({ it: WayPoint -> it.getTime() }))
            .map(Function({ it: Optional<ZonedDateTime?> -> it.orElse(null) }))
            .collect(Collectors.toList())

        // We need to check if all waypoints actually have a timestamp, since timestamps are an optiona
        // feature of GPX and not all GPX file contain them.
        if (times.contains(null)) {
            // TODO better error handling
            return waypoints.stream().map(Function({ it: WayPoint? -> Duration.ZERO })).collect(Collectors.toList())
        }
        val firstTime: ZonedDateTime? = times.get(0)
        return times.stream()
            .map(Function({ it: ZonedDateTime? -> Duration.between(firstTime, it) }))
            .collect(Collectors.toList())
    }

    /**
     * Maps the list of waypoints in a segment to a list of speed between this waypoint and the previous one.
     * The first value will be 0.
     *
     * (The speed is given in km/h and needs to be converted in the UI layer if necessary.)
     * @param segment
     * @return
     */
    fun getSpeeds(segment: TrackSegment?): List<Double> {
        return resolveCache(speeds, segment, Calculator<Double>({ segment: TrackSegment? -> calculateSpeeds(segment) }))
    }

    private fun calculateSpeeds(segment: TrackSegment?): List<Double?> {
        if (segment == null) {
            return emptyList<Double>()
        }
        val distances: List<Double> = getDistanceDifferences(segment)
        val times: List<Duration> = getTimeDifferences(segment)
        if (distances.size != times.size) {
            log.error(
                "Speed calculation impossible: data rows have incompatible sizes ({} and {}])",  //$NON-NLS-1$
                distances.size, times.size
            )
            return emptyList<Double>()
        }
        val result: MutableList<Double?> = ArrayList(distances.size)
        for (i in distances.indices) {
            val distance: Double = distances.get(i)
            val time: Duration = times.get(i)
            val millis: Long = time.toMillis()
            if (millis == 0L) {
                result.add(0.0)
            } else {
                val speed: Double = distance / millis * 3600000
                if (speed < STANDING_SPEED_THRESHOLD) {
                    if (i > 0) {
                        result.set(i - 1, 0.0)
                    }
                    result.add(0.0)
                } else {
                    result.add(speed)
                }
            }
        }
        return result
    }

    fun getTimeDifferences(segment: TrackSegment?): List<Duration> {
        return resolveCache(
            timeDiffs,
            segment,
            Calculator<Duration>({ segment: TrackSegment? -> calculateTimeDifferences(segment) })
        )
    }

    private fun calculateTimeDifferences(segment: TrackSegment?): List<Duration?> {
        if (segment == null) {
            return emptyList<Duration>()
        }
        val waypoints: List<WayPoint> = segment.getPoints()
        val times: List<ZonedDateTime?> = waypoints.stream()
            .map(Function({ it: WayPoint -> it.getTime() }))
            .map(Function({ it: Optional<ZonedDateTime?> -> it.orElse(null) }))
            .collect(Collectors.toList())
        if (times.contains(null)) {
            // TODO better error handling
            log.error("gpx point without timestamp!") //$NON-NLS-1$
        }
        val result: MutableList<Duration?> = ArrayList(waypoints.size)
        for (i in waypoints.indices) {
            if (i == 0) {
                result.add(Duration.ZERO)
            } else {
                val prev: ZonedDateTime? = times.get(i - 1)
                val now: ZonedDateTime? = times.get(i)
                result.add(Duration.between(prev, now))
            }
        }
        return result
    }

    fun getElevations(segment: TrackSegment?): List<Double> {
        return resolveCache(
            elevations,
            segment,
            Calculator<Double>({ segment: TrackSegment -> calculateElevations(segment) })
        )
    }

    private fun calculateElevations(segment: TrackSegment): List<Double?> {
        val waypoints: List<WayPoint> = segment.getPoints()
        val elevations: List<Double?> = waypoints.stream()
            .map(Function({ it: WayPoint -> it.getElevation().orElse(ZERO_LENGTH).to(Length.Unit.METER) }))
            .collect(Collectors.toUnmodifiableList())
        return elevations
    }

    /**
     * Gets a list in which element X represents the gradient (in percent) between
     * waypoint X - 1 and waypoint X.
     *
     * The first element is 0.
     *
     * @param segment
     * @return
     */
    fun getGradients(segment: TrackSegment?): List<Double> {
        return resolveCache(
            gradients,
            segment,
            Calculator<Double>({ segment: TrackSegment -> calculateGradients(segment) })
        )
    }

    private fun calculateGradients(segment: TrackSegment): List<Double?> {
        val distanceDifferences: List<Double> = getDistanceDifferences(segment)
        val elevations: List<Double> = smootheWithoutZeroSnap(getElevations(segment), 50)
        if (distanceDifferences.size != elevations.size) {
            val errorMessage: String = String.format(
                "Can not build dataset for gradients: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
                distanceDifferences.size, elevations.size
            )
            log.error(errorMessage)
            throw RuntimeException(errorMessage)
        }
        val result: ArrayList<Double?> = ArrayList(distanceDifferences.size)
        result.add(0.0)
        for (i in 1 until elevations.size) {
            val distance: Double = distanceDifferences.get(i)
            if (distance == 0.0) {
                result.add(0.0)
            } else {
                val first: Double = elevations.get(i - 1)
                val second: Double = elevations.get(i)

                // factor 1000 is necessary because distances are in kilometers,
                // while altitudes are in meters
                val gradient: Double = (second - first) * 1000 / distance
                result.add(gradient)
            }
        }
        return result
    }

    fun getHeightProfileAsXY(segment: TrackSegment?): XYDataset {
        val altitudes: List<Double> = getElevations(segment)
        val distances: List<Double> = getTotalDistances(segment)
        val result: XYSeries =
            XYSeries(Messages.getString("TrackStatisticsManager.HeightProfileDiagramLabel")) //$NON-NLS-1$
        if (altitudes.size != distances.size) {
            val errorMessage: String = String.format(
                "Can not build dataset for speed over time: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
                altitudes.size, distances.size
            )
            log.error(errorMessage)
            throw RuntimeException(errorMessage)
        }
        for (i in altitudes.indices) {
            result.add(distances.get(i), altitudes.get(i))
        }
        return XYSeriesCollection(result)
    }

    fun getDistanceOverTimeAsXY(segment: TrackSegment?): XYDataset {
        val times: List<Double> = getTotalTimes(segment).stream()
            .map(Function({ it: Duration -> it.getSeconds().toDouble() }))
            .collect(Collectors.toList())
        val distances: List<Double> = getTotalDistances(segment)
        return buildXYDataset(
            Messages.getString("TrackStatisticsManager.DistanceOverTimeDiagramLabel"),
            times,
            distances
        ) //$NON-NLS-1$
    }

    /**
     * Returns a JFreeChart data set of speed over time for a given TrackSegment.
     *
     * Times are in minutes, distances are in km or miles depending on UnitSystem parameter.
     *
     * @param segment
     * @return
     */
    fun getSpeedOverTimeAsXY(segment: TrackSegment?, unitSystem: UnitSystem?): XYDataset {
        val times: List<Double> = getTotalTimes(segment).stream()
            .map(Function({ it: Duration -> it.toMillis() / 1000.0 }))
            .map(Function({ it: Double -> it / 60.0 }))
            .collect(Collectors.toUnmodifiableList())
        var speeds: List<Double> = getSpeeds(segment).stream()
            .map(Function({ it: Double -> if (unitSystem == UnitSystem.IMPERIAL) unitConverter!!.kilometersToMiles(it) else it }))
            .collect(Collectors.toUnmodifiableList())
        speeds = smootheWithZeroSnap(speeds, 5)
        return buildXYDataset(
            Messages.getString("TrackStatisticsManager.SpeedOverTimeDiagramLabel"),
            times,
            speeds
        ) //$NON-NLS-1$
    }

    fun getGradientOverDistanceAsXY(segment: TrackSegment?): XYDataset {
        val gradients: List<Double> = getGradients(segment)
        //gradients = smootheWithoutZeroSnap(gradients, 50);
        val distances: List<Double> = getTotalDistances(segment)
        return buildXYDataset(
            Messages.getString("TrackStatisticsManager.GradientOverDistanceDiagramLabel"),
            distances,
            gradients
        ) //$NON-NLS-1$
    }

    private fun <T> resolveCache(
        cache: HashMap<TrackSegment, List<T>>,
        segment: TrackSegment?,
        calculator: Calculator<T>
    ): List<T> {
        if (segment == null || segment.isEmpty()) {
            return emptyList()
        }
        val cached: List<T>? = cache.get(segment)
        if (cached != null) {
            return cached
        } else {
            val calculated: List<T> = calculator.calculate(segment)
            cache.put(segment, calculated)
            return calculated
        }
    }

    private fun smootheWithoutZeroSnap(input: List<Double>, halfWindowSize: Int): List<Double> {
        val output: MutableList<Double> = ArrayList(input.size)
        for (i in input.indices) {
            output.add(average(input, i - halfWindowSize, i + halfWindowSize))
        }
        return output
    }

    private fun smootheWithZeroSnap(input: List<Double>, halfWindowSize: Int): List<Double> {
        val output: MutableList<Double> = ArrayList(input.size)
        for (i in input.indices) {
            val original: Double = input.get(i)
            if (original < STANDING_SPEED_THRESHOLD) {
                output.add(0.0)
            } else {
                output.add(average(input, i - halfWindowSize, i + halfWindowSize))
            }
        }
        return output
    }

    private fun average(input: List<Double>, min: Int, max: Int): Double {
        var min: Int = min
        var max: Int = max
        min = Math.max(min, 0)
        max = Math.min(max, input.size)
        if (min > max) {
            throw IllegalArgumentException()
        }
        if (min == max) {
            return input.get(min)
        }
        var sum: Double = 0.0
        var num: Int = 0
        for (i in min until max) {
            num++
            sum += input.get(i)
        }
        return sum / num
    }

    private open interface Calculator<T> {
        fun calculate(t: TrackSegment?): List<T>
    }

    companion object {
        /**
         * If speed between two points is below this threshold, it will be rounded down to 0.
         * This compensates for GPS imprecisions creating the impression of movement while
         * the GPS receiver is not actually moving.
         */
        private val STANDING_SPEED_THRESHOLD: Double = 2.0
        private val ZERO_LENGTH: Length = Length.of(0.0, Length.Unit.METER)
        private val log: Logger = LogManager.getLogger(
            TrackStatisticsManager::class.java
        )

        private fun buildXYDataset(name: String?, xAxis: List<Double>, yAxis: List<Double>): XYDataset {
            val result: XYSeries = XYSeries(name)
            if (xAxis.size != yAxis.size) {
                val errorMessage: String = String.format(
                    "Can not build dataset: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
                    xAxis.size, yAxis.size
                )
                log.error(errorMessage)
                throw RuntimeException(errorMessage)
            }
            for (i in xAxis.indices) {
                result.add(xAxis.get(i), yAxis.get(i))
            }
            return XYSeriesCollection(result)
        }
    }
}