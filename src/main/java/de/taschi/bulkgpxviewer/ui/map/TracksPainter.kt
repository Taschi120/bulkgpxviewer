package de.taschi.bulkgpxviewer.ui.map

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
import java.util.Arrays
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.Color
import java.awt.BasicStroke
import de.taschi.bulkgpxviewer.settings.ColorConverter
import java.util.stream.Collectors
import java.awt.geom.Point2D
import lombok.extern.log4j.Log4j2
import java.awt.Image
import javax.imageio.ImageIO
import java.awt.event.MouseAdapter
import java.util.HashSet
import java.util.Collections
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
import java.util.HashMap
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
import java.util.ResourceBundle
import java.util.MissingResourceException
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
import de.taschi.bulkgpxviewer.ui.windowbuilder.EditingPanel
import de.taschi.bulkgpxviewer.geo.WaypointIndex
import javax.swing.JList
import javax.swing.JComboBox
import javax.swing.DefaultComboBoxModel
import javax.swing.ListSelectionModel
import de.taschi.bulkgpxviewer.settings.dto.Settings
import de.taschi.bulkgpxviewer.ui.ColorListItemRenderer
import javax.swing.JColorChooser
import de.taschi.bulkgpxviewer.ui.windowbuilder.ColorChooserDialog.ReturnCode
import javax.swing.SwingConstants
import javax.swing.ImageIcon
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor
import javax.swing.ListCellRenderer
import javax.swing.border.Border
import javax.swing.BorderFactory
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
import lombok.Cleanup
import org.w3c.dom.NodeList
import java.util.LinkedList
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
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.settings.SettingsManager
import io.jenetics.jpx.Track
import org.jxmapviewer.painter.Painter
import java.util.function.Function
import java.util.function.Supplier

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
 */ /**
 * Paints a route. This class has been shamelessly stolen from the JXMapViewer samples
 * at [://github.com/msteiger/jxmapviewer2/blob/master/examples/src/sample2_waypoints/RoutePainter.java][https]
 * and slightly adapted.
 *
 * @author Martin Steiger (original), S. Hillebrand (adapted)
 */
class TracksPainter(private val parent: MapPanel) : Painter<JXMapViewer> {
    private val antiAlias = true
    private var provider: Supplier<Collection<GpxFile?>?>? = allRouteProvider

    @Inject
    private val loadedFileManager: LoadedFileManager? = null

    @Inject
    private val trackColorUtil: TrackColorUtil? = null

    @Inject
    private val settingsManager: SettingsManager? = null

    init {
        Application.Companion.getInjector().injectMembers(this)
    }

    val allRouteProvider: Supplier<Collection<GpxFile?>?>
        get() = Supplier { loadedFileManager!!.loadedTracks }

    fun getSingleTrackProvider(track: GpxFile?): Supplier<Collection<GpxFile?>> {
        return Supplier { Arrays.asList(track) }
    }

    override fun paint(g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        var g = g
        g = g.create() as Graphics2D

        // convert from viewport to world bitmap
        val rect = map.viewportBounds
        g.translate(-rect.x, -rect.y)
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        for (track in provider!!.get()!!) {
            if (track === parent.selectedFile) continue  // selected file needs to be drawn last!
            drawGpxFileWithOutline(track, g, map, w, h)
        }
        if (parent.selectedFile != null) {
            drawGpxFileWithOutline(parent.selectedFile, g, map, w, h)
        }
        g.dispose()
    }

    private fun drawGpxFileWithOutline(track: GpxFile?, g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        // do the drawing
        g.color = Color.BLACK
        g.stroke = BasicStroke(4)
        drawGpxFileSinglePass(g, map, track)
        val color: Color?
        color = if (parent.selectedFile == null) {
            trackColorUtil!!.getColorForTrack(track)
        } else if (parent.selectedFile === track) { // pointer comparison should be ok here
            ColorConverter.convert(settingsManager.getSettings().selectedRouteColor)
        } else {
            ColorConverter.convert(settingsManager.getSettings().unselectedRouteColor)
        }
        // do the drawing again
        g.color = color
        g.stroke = BasicStroke(2)
        drawGpxFileSinglePass(g, map, track)
    }

    private fun drawGpxFileSinglePass(g: Graphics2D, map: JXMapViewer, gpxViewerTrack: GpxFile?) {
        for (track in gpxViewerTrack!!.gpx.tracks) {
            drawTrack(g, map, track)
        }
    }

    private fun drawTrack(g: Graphics2D, map: JXMapViewer, track: Track) {
        for (segment in track.segments) {
            drawSegment(g, map, segment)
        }
    }

    private fun drawSegment(g: Graphics2D, map: JXMapViewer, segment: TrackSegment) {
        var lastX = 0
        var lastY = 0
        var first = true
        val wayPoints = segment.points

        // FIXME this is a horrible performance drain, refactor ASAP
        val geoPositions = wayPoints.stream()
            .map(Function<WayPoint, GeoPosition> { `in`: WayPoint ->
                GpxToJxMapper.Companion.getInstance().waypointToGeoPosition(`in`)
            })
            .collect(Collectors.toList())
        for (gp in geoPositions) {
            // convert geo-coordinate to world bitmap pixel
            val pt = map.tileFactory.geoToPixel(gp, map.zoom)
            if (first) {
                first = false
            } else {
                g.drawLine(lastX, lastY, pt.x.toInt(), pt.y.toInt())
            }
            lastX = pt.x.toInt()
            lastY = pt.y.toInt()
        }
    }

    /**
     * Changes the track provider. Remember that repaint() needs to be called afterwards on the
     * JXMapViewer or JXMapKit instance.
     * @param provider
     */
    fun setProvider(provider: Supplier<Collection<GpxFile?>?>?) {
        this.provider = provider
    }
}