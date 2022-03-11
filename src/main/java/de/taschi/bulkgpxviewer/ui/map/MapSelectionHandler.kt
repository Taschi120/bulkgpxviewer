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
import java.awt.Image
import javax.imageio.ImageIO
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
import org.apache.logging.log4j.LogManager
import java.awt.event.*
import java.util.ArrayList

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
 * This listener handles the selection of wapypoints on the map during editing mode.
 */
class MapSelectionHandler(private val target: JXMapViewer) : MouseAdapter() {
    private var active = false
    private var track: GpxFile? = null

    /** currently selected points  */
    private var selectedPoints: Set<WayPoint> = HashSet()

    /** unmodifiable copy of selectedPoints, cached for performance reasons  */
    var selectedWayPoints = Collections.unmodifiableSet(selectedPoints)
        private set
    private val selectionChangeListeners: MutableList<WaypointSelectionChangeListener> = ArrayList()
    override fun mouseClicked(e: MouseEvent) {
        if (!active) {
            return
        }
        val wp = getNearestWaypointToMouseCoords(e.point)
        if (wp != null) {
            if (selectedPoints.contains(wp)) {
                log.info("Deselecting waypoint at {}|{}", wp.latitude, wp.longitude) //$NON-NLS-1$
                selectedPoints.remove(wp)
            } else if (selectedPoints.size < 2) {
                log.info("Selecting waypoint at {}|{}", wp.latitude, wp.longitude) //$NON-NLS-1$
                selectedPoints.add(wp)
            }
            selectedWayPoints = Collections.unmodifiableSet(selectedPoints)
            fireSelectionChangeListeners()
        }
    }

    private fun fireSelectionChangeListeners() {
        SwingUtilities.invokeLater {
            selectionChangeListeners.stream()
                .forEach { it: WaypointSelectionChangeListener -> it.selectionChanged(selectedPoints) }
        }
    }

    private fun getNearestWaypointToMouseCoords(mouse: Point2D): WayPoint? {
        val points = track.getAllWayPoints()
        val geoPositions = track.getAllGeoPositions()
        if (points!!.isEmpty()) {
            log.warn("Track is empty, cannot find nearest point") //$NON-NLS-1$
            return null
        }
        var nearestWaypoint = points!![0]
        var nearestDistance = Double.MAX_VALUE
        for (i in points!!.indices) {
            val comparison = points!![i]
            val position = geoPositions!![i]
            val point = target.convertGeoPositionToPoint(position)
            val distance = point.distance(mouse)
            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestWaypoint = comparison
            }
        }
        return if (nearestDistance <= SELECTION_THRESHOLD) {
            nearestWaypoint
        } else {
            null
        }
    }

    /**
     * Activates this listener
     * @param track the track which is being edited
     */
    fun activate(track: GpxFile?) {
        log.info("Entering edit mode for track {}", track.getFileName()) //$NON-NLS-1$
        this.track = track
        active = true
    }

    /**
     * Deactivates this listener
     */
    fun deactivate() {
        log.info("Exiting edit mode") //$NON-NLS-1$
        track = null
        active = false
        selectedPoints = emptySet()
    }

    fun addSelectionChangeListener(listener: WaypointSelectionChangeListener) {
        selectionChangeListeners.add(listener)
    }

    fun removeSelectionChangeListener(listener: WaypointSelectionChangeListener): Boolean {
        return selectionChangeListeners.remove(listener)
    }

    /**
     * Change the current selection
     * @param selection
     */
    fun setSelection(selection: List<WayPoint>) {
        selectedPoints = selection.stream().distinct().collect(Collectors.toSet())
        selectedWayPoints = Collections.unmodifiableSet(selectedPoints)
        fireSelectionChangeListeners()
    }

    companion object {
        /**
         * How far away from the waypoint the cursor can be when selecting a waypoint, in pixels
         */
        const val SELECTION_THRESHOLD = 20
        private val log = LogManager.getLogger(
            MapSelectionHandler::class.java
        )
    }
}