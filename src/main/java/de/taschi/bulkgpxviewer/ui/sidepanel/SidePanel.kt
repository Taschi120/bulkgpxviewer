package de.taschi.bulkgpxviewer.ui.sidepanel

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
import lombok.extern.log4j.Log4j2
import java.awt.Image
import javax.imageio.ImageIO
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
import de.taschi.bulkgpxviewer.ui.Messages
import org.apache.logging.log4j.LogManager
import java.awt.event.*
import java.util.*

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
 */@Log4j2
class SidePanel : JPanel() {
    private val treeView: JTree?
    private var rootNode: DefaultMutableTreeNode? = null
    private val yearNodes = HashMap<Int, DefaultMutableTreeNode>()
    private val trackNodes = HashMap<Path?, GpxFileTreeNode>()
    private var unknownYearNode: DefaultMutableTreeNode? = null
    private val filePopupMenu: GpxFilePopupMenu

    @Inject
    private val loadedFileManager: LoadedFileManager? = null

    init {
        Application.Companion.getInjector().injectMembers(this)
        layout = BorderLayout()
        createTreeModel()
        treeView = JTree(rootNode)
        val scrollPane = JScrollPane(
            treeView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )
        filePopupMenu = GpxFilePopupMenu()
        treeView.addMouseListener(SidePanelMouseListener())
        add(scrollPane, BorderLayout.CENTER)
        loadedFileManager!!.addChangeListener { createTreeModel() }
    }

    @Synchronized
    private fun createTreeModel() {
        LOG.info("Recreating tree model") //$NON-NLS-1$
        if (rootNode == null) {
            rootNode = DefaultMutableTreeNode(Messages.getString("SidePanel.allFiles")) //$NON-NLS-1$
        } else {
            rootNode!!.removeAllChildren()
        }
        val tracks = loadedFileManager!!.loadedTracks
        cullUnnecessaryTrackNodes(tracks)
        for (track in tracks!!) {
            val yearNode = makeOrUpdateYearNode(track, rootNode!!)
            makeOrUpdateTrackNode(track, yearNode)
        }
        cullUnnecessaryYearTreeNodes()
        if (treeView != null) {
            (treeView.model as DefaultTreeModel).reload()
        }
    }

    /**
     * Removes all track nodes which do not correspond with currently loaded files
     */
    private fun cullUnnecessaryTrackNodes(loadedTracks: List<GpxFile?>?) {
        val loadedTrackPaths =
            loadedTracks!!.stream().map { obj: GpxFile? -> obj.getFileName() }.collect(Collectors.toSet())
        val pathsInTree: Set<Path?> = HashSet(trackNodes.keys)
        for (p in pathsInTree) {
            if (!loadedTrackPaths.contains(p)) {
                val node = trackNodes[p]
                (node!!.parent as DefaultMutableTreeNode).remove(node)
                trackNodes.remove(p)
            }
        }
    }

    private fun makeOrUpdateYearNode(track: GpxFile?, parent: DefaultMutableTreeNode): DefaultMutableTreeNode? {
        var yearNode: DefaultMutableTreeNode?
        val startedAt = track.getStartedAt()
        if (startedAt!!.isPresent) {
            val startDate = startedAt!!.get().atZone(ZoneId.systemDefault())
            val year = startDate.year
            yearNode = yearNodes[year]
            if (yearNode == null) {
                LOG.info("Making node for year $year") //$NON-NLS-1$
                yearNode = DefaultMutableTreeNode(year)
                yearNodes[year] = yearNode
                rootNode!!.add(yearNode)
            }
        } else {
            if (unknownYearNode == null) {
                unknownYearNode = DefaultMutableTreeNode(Messages.getString("SidePanel.unknownYear")) //$NON-NLS-1$
                rootNode!!.add(unknownYearNode)
            }
            yearNode = unknownYearNode
        }
        rootNode!!.add(yearNode)
        return yearNode
    }

    private fun makeOrUpdateTrackNode(track: GpxFile?, parent: DefaultMutableTreeNode?) {
        var result = trackNodes[track.getFileName()]
        if (result == null) {
            LOG.debug("Making new tree node for {}", track.getFileName().toString()) //$NON-NLS-1$
            result = GpxFileTreeNode(track)
            trackNodes[track.getFileName()] = result
            parent!!.add(result)
        } else {
            LOG.debug("Updating tree node for {}", track.getFileName().toString()) //$NON-NLS-1$
            result.update()
        }
    }

    private fun cullUnnecessaryYearTreeNodes() {
        val years: List<Int> = ArrayList(yearNodes.keys)
        for (year in years) {
            val node = yearNodes[year]
            if (node != null && rootNode!!.isNodeChild(node) && node.childCount == 0) {
                rootNode!!.remove(node)
                yearNodes.remove(year)
            }
        }
        if (unknownYearNode != null && unknownYearNode!!.childCount == 0) {
            if (rootNode!!.getIndex(unknownYearNode) != -1) {
                rootNode!!.remove(unknownYearNode)
            }
            unknownYearNode = null
        }
    }

    fun updateModel() {
        createTreeModel()
    }

    private inner class SidePanelMouseListener : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            if (SwingUtilities.isRightMouseButton(e)) {
                // check if we need to open context menu
                val row = treeView!!.getRowForLocation(e.x, e.y)
                treeView.setSelectionRow(row)
                val selectionModel = treeView.selectionModel
                val selectionPath = selectionModel.leadSelectionPath
                if (selectionPath != null) {
                    val selected = selectionPath.lastPathComponent
                    if (selected is GpxFileTreeNode) {
                        filePopupMenu.openOnTreeItem(treeView, selected, e.x, e.y)
                    }
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                // if exactly one GPX file is now selected, update graph panels
                val selectionModel = treeView!!.selectionModel
                if (selectionModel.selectionCount == 1) {
                    val selected = selectionModel.leadSelectionPath.lastPathComponent
                    if (selected is GpxFileRelatedNode) {
                        val file = selected.gpxFile
                        SidePanel.log.info("Selected GPX file is now {}", file.fileName)
                        Application.Companion.getMainWindow()!!.setSelectedGpxFile(Optional.of(file))
                    } else {
                        Application.Companion.getMainWindow()!!.setSelectedGpxFile(Optional.empty<GpxFile?>())
                    }
                } else {
                    Application.Companion.getMainWindow()!!.setSelectedGpxFile(Optional.empty<GpxFile?>())
                }
            }
        }
    }

    companion object {
        private val LOG = LogManager.getLogger(SidePanel::class.java)
        private const val serialVersionUID = -4050409521285757121L
    }
}