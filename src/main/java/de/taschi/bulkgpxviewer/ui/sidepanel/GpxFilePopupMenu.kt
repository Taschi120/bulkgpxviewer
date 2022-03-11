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
import java.nio.charset.Charset
import com.google.inject.Injector
import com.google.inject.Guice
import de.taschi.bulkgpxviewer.CoreGuiceModule
import javax.swing.UIManager
import com.google.inject.AbstractModule
import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.ui.Messages
import org.apache.commons.lang3.StringUtils
import java.nio.file.*

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
class GpxFilePopupMenu : JPopupMenu() {
    private var track: GpxFile? = null
    private var node: GpxFileTreeNode? = null
    private val edit: JMenuItem
    private val rename: JMenuItem
    private val addTag: JMenuItem

    @Inject
    private val loadedFileManager: LoadedFileManager? = null

    init {
        Application.Companion.getInjector().injectMembers(this)
        edit = JMenuItem(Messages.getString("GpxFilePopupMenu.Edit")) //$NON-NLS-1$
        edit.icon = IconHandler.loadIcon("edit-line") //$NON-NLS-1$
        edit.addActionListener { e: ActionEvent -> onEdit(e) }
        add(edit)
        rename = JMenuItem(Messages.getString("GpxFilePopupMenu.Rename")) //$NON-NLS-1$
        rename.icon = IconHandler.loadIcon("input-cursor-move") //$NON-NLS-1$
        rename.addActionListener { evt: ActionEvent -> onRename(evt) }
        add(rename)
        addTag = JMenuItem(Messages.getString("GpxFilePopupMenu.AddTag")) //$NON-NLS-1$
        addTag.icon = IconHandler.loadIcon("price-tag-3-line") //$NON-NLS-1$
        add(addTag)
    }

    fun openOnTreeItem(tree: JTree?, node: GpxFileTreeNode, x: Int, y: Int) {
        this.node = node
        track = node.track
        show(tree, x, y)
    }

    private fun onRename(evt: ActionEvent) {
        val path = track.getFileName()
        GpxFilePopupMenu.log.info("Starting debug for {}", path) //$NON-NLS-1$
        var oldFileName = path!!.fileName.toString()
        if (oldFileName.endsWith(".gpx")) { //$NON-NLS-1$
            oldFileName = oldFileName.substring(0, oldFileName.length - 4)
        }
        var newFileName = JOptionPane.showInputDialog(
            String.format(Messages.getString("GpxFilePopupMenu.EnterNewFileName"), oldFileName),  //$NON-NLS-1$
            oldFileName
        )
        if (StringUtils.isEmpty(newFileName)) {
            GpxFilePopupMenu.log.info("Renaming cancelled.") //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, Messages.getString("GpxFilePopupMenu.RenamingCancelled")) //$NON-NLS-1$
            return
        }
        if (newFileName.contains("/") || newFileName.contains("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
            GpxFilePopupMenu.log.info("Invalid file name {}", newFileName) //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, Messages.getString("GpxFilePopupMenu.InvalidFileName")) //$NON-NLS-1$
            return
        }
        if (!newFileName.endsWith(".gpx")) { //$NON-NLS-1$
            GpxFilePopupMenu.log.info("Adding .gpx ending to file name {}") //$NON-NLS-1$
            newFileName += ".gpx" //$NON-NLS-1$
        }
        val newPath = path!!.parent.resolve(newFileName)
        if (Files.exists(newPath)) {
            GpxFilePopupMenu.log.error("Rename failed because file '{}' already exists", newPath) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                null,
                String.format(Messages.getString("GpxFilePopupMenu.CouldNotRenameFileAlreadyExists"), newPath)
            ) //$NON-NLS-1$
            return
        }
        try {
            Files.move(path, newPath)
        } catch (e: IOException) {
            GpxFilePopupMenu.log.error("Could not rename file", e) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                null,
                String.format(Messages.getString("GpxFilePopupMenu.ErrorWhileRenaming"), e.localizedMessage)
            ) //$NON-NLS-1$
            return
        }
        try {
            loadedFileManager!!.refresh()
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(
                null, String.format(
                    Messages.getString("GpxFilePopupMenu.ErrorWhileReloading") //$NON-NLS-1$
                    , e.localizedMessage
                )
            )
        }
    }

    private fun onEdit(e: ActionEvent) {
        Application.Companion.getMainWindow()!!.startEditingMode(track)
    }

    companion object {
        private const val serialVersionUID = -3214637893458879098L
    }
}