package de.taschi.bulkgpxviewer.files

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
import com.google.inject.Inject
import io.jenetics.jpx.Track
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.time.Instant
import java.util.*
import java.util.function.Function
import java.util.stream.Stream

/*-
 * #%L
 * bulkgpxviewer
 * %%
 * Copyright (C) 2021 S. Hillebrand
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */   class GpxFile constructor(val fileName: Path, private var gpx: GPX) {

    /**
     * Returns a list of all waypoints in this GPX file, without any guarantees as to order.
     * @return
     */
    var allWayPoints: List<WayPoint>? = null
        private set

    /**
     * Returns a list of all GeoPositions in this GPX file. Order will be consistent with getAllWayPoints, as long as
     * the model is not changed in the interim.
     * @return
     */
    var allGeoPositions: List<GeoPosition>? = null
        private set

    @Inject
    private val loadedFileManager: LoadedFileManager? = null
    var isChanged: Boolean = false
        private set

    init {
        updateCachedFields()
    }

    private fun updateCachedFields() {
        allWayPoints = gpx.getTracks().stream()
            .flatMap(Function<Track, Stream<out TrackSegment>>({ obj: Track -> obj.segments() }))
            .flatMap(Function<TrackSegment, Stream<out WayPoint>>({ obj: TrackSegment -> obj.points() }))
            .collect(Collectors.toUnmodifiableList())
        allGeoPositions = allWayPoints.stream()
            .map(Function<WayPoint, GeoPosition>({ `in`: WayPoint ->
                GpxToJxMapper.Companion.getInstance().waypointToGeoPosition(`in`)
            }))
            .collect(Collectors.toUnmodifiableList())
    }

    val startedAt: Optional<Instant>
        get() {
            for (track: Track in gpx.getTracks()) {
                for (segment: TrackSegment in track.getSegments()) {
                    for (point: WayPoint in segment.getPoints()) {
                        val timestamp: Optional<ZonedDateTime> = point.getTime()
                        if (timestamp.isPresent()) {
                            return timestamp.map(Function({ obj: ZonedDateTime -> obj.toInstant() }))
                        }
                    }
                }
            }
            return Optional.empty()
        }

    @Throws(IOException::class)
    fun save() {
        doBackup()
        log.info("Writing GPX to '{}'", fileName) //$NON-NLS-1$
        GPX.write(getGpx(), fileName)
        log.info("Written GPX to '{}' successfully", fileName) //$NON-NLS-1$
        isChanged = false
    }

    @Throws(IOException::class)
    private fun doBackup() {
        val `in`: File = fileName.toFile()
        val out: File = File(`in`.getAbsolutePath() + ".bak") //$NON-NLS-1$
        log.info("Creating backup of '{}' at '{}'", `in`, out) //$NON-NLS-1$
        FileUtils.copyFile(`in`, out)
    }

    public override fun equals(o: Any?): Boolean {
        if (o is GpxFile) {
            val otherGpx: GPX = o.getGpx()
            return (gpx == otherGpx)
        }
        return false
    }

    public override fun hashCode(): Int {
        return gpx.hashCode()
    }

    fun getGpx(): GPX {
        return gpx
    }

    /**
     * Find the position of a WayPoint inside the data model
     * @param wayPoint
     * @return Optional<WaypointIndex> if found, empty optional otherwise.
    </WaypointIndex> */
    fun indexOfWayPoint(wayPoint: WayPoint): Optional<WaypointIndex?> {
        val tracks: List<Track> = gpx.getTracks()
        for (trackId in tracks.indices) {
            val segments: List<TrackSegment> = tracks.get(trackId).getSegments()
            for (segmentId in segments.indices) {
                val points: List<WayPoint> = segments.get(segmentId).getPoints()
                for (pointId in points.indices) {
                    if ((points.get(pointId) == wayPoint)) {
                        return Optional.of(
                            WaypointIndex.Companion.builder()
                                .gpx(gpx)
                                .trackId(trackId)
                                .segmentId(segmentId)
                                .waypointId(pointId)
                                .build()
                        )
                    }
                }
            }
        }
        return Optional.empty()
    }

    fun setGpx(gpx: GPX) {
        if ((this.gpx == gpx)) {
            return
        }
        isChanged = true
        this.gpx = gpx
        loadedFileManager!!.fireChangeListeners()
    }

    val firstSegment: Optional<TrackSegment>
        get() {
            val tracks: List<Track> = getGpx().getTracks()
            if (tracks.isEmpty()) {
                return Optional.empty()
            }
            val segments: List<TrackSegment> = tracks.get(0).getSegments()
            if (segments.isEmpty()) {
                return Optional.empty()
            }
            return Optional.of(segments.get(0))
        }

    companion object {
        private val log: Logger = LogManager.getLogger(GpxFile::class.java)
    }
}