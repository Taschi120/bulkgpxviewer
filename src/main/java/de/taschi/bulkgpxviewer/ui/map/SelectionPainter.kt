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
import org.jxmapviewer.painter.Painter
import java.lang.Exception
import java.util.ArrayList
import java.util.function.Function

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
 * Highlights a set of points on the map
 */
@Log4j2
class SelectionPainter : Painter<JXMapViewer> {
    private var markerA: Image? = null
    private var markerB: Image? = null
    private var markerNeutral: Image? = null
    private var imageSize = 0
    private var halfImageSize = 0
    var isEnabled = false
    private val antiAlias = true
    private var track: GpxFile? = null
    private var selection = emptyList<WayPoint>()
    private var selectionAsGeoPositions = emptyList<GeoPosition>()

    init {
        try {
            markerNeutral = ImageIO.read(ClassLoader.getSystemResource("ui/marker16.png")) //$NON-NLS-1$
            markerA = ImageIO.read(ClassLoader.getSystemResource("ui/marker_a16.png")) //$NON-NLS-1$
            markerB = ImageIO.read(ClassLoader.getSystemResource("ui/marker_b16.png")) //$NON-NLS-1$
        } catch (e: Exception) {
            SelectionPainter.log.error("Could not load marker image", e) //$NON-NLS-1$
            markerA = null
            markerB = null
        }

        // Assuming both markers have the same size.
        if (markerA != null) {
            imageSize = markerA.getWidth(null)
            halfImageSize = imageSize / 2
        } else {
            imageSize = 0
            halfImageSize = 0
        }
    }

    override fun paint(g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        var g = g
        if (track == null || !isEnabled) return
        g = g.create() as Graphics2D

        // convert from viewport to world bitmap
        val rect = map.viewportBounds
        g.translate(-rect.x, -rect.y)
        if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        if (selection.size == 1) {
            val position = selectionAsGeoPositions[0]
            val point = map.tileFactory.geoToPixel(position, map.zoom)
            // center points
            val x = Math.round(point.x).toInt()
            val y = Math.round(point.y).toInt()
            paintMarker(g, map, x, y, markerNeutral)
        } else if (selection.size == 2) {
            var position = selectionAsGeoPositions[0]
            var point = map.tileFactory.geoToPixel(position, map.zoom)
            var x = Math.round(point.x).toInt()
            var y = Math.round(point.y).toInt()
            paintMarker(g, map, x, y, markerA)
            position = selectionAsGeoPositions[1]
            point = map.tileFactory.geoToPixel(position, map.zoom)
            x = Math.round(point.x).toInt()
            y = Math.round(point.y).toInt()
            paintMarker(g, map, x, y, markerB)
        }
        g.dispose()
    }

    private fun paintMarker(g: Graphics2D, map: JXMapViewer, x: Int, y: Int, marker: Image?) {
        g.drawImage(
            marker,
            x - halfImageSize, y - halfImageSize, x + halfImageSize, y + halfImageSize,
            0, 0, imageSize, imageSize,
            map
        )
    }

    fun setTrack(track: GpxFile?) {
        this.track = track
    }

    fun setSelectionFromWayPoints(selection: Set<WayPoint>) {
        SelectionPainter.log.info("Updating selection, {} items now selected", selection.size) //$NON-NLS-1$
        this.selection = ArrayList(selection)
        this.selection.sort(java.util.Comparator { o1: WayPoint, o2: WayPoint ->
            track!!.indexOfWayPoint(o2).orElseThrow()!!
                .compareTo(track!!.indexOfWayPoint(o1).orElseThrow())
        })
        selectionAsGeoPositions = this.selection.stream()
            .map(Function<WayPoint, GeoPosition> { `in`: WayPoint ->
                GpxToJxMapper.Companion.getInstance().waypointToGeoPosition(`in`)
            })
            .collect(Collectors.toList())
    }
}