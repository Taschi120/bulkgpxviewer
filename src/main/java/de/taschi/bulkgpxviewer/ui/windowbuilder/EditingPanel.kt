package de.taschi.bulkgpxviewer.ui.windowbuilder

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
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.ui.Messages
import io.jenetics.jpx.Track
import java.lang.Exception
import java.util.ArrayList
import javax.swing.border.BevelBorder

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
class EditingPanel : JPanel() {
    private var track: GpxFile? = null
    private var selectionHandler: MapSelectionHandler? = null
    private var selection = emptyList<WayPoint>()
    private val firstSelectedPointLabel: JLabel
    private val firstSelectionTransportBack: JButton
    private val firstSelectionTransportForward: JButton
    private val secondSelectionTransportBack: JButton
    private val secondSelectionDeselect: JButton
    private val cropAfter: JButton
    private val secondSelectionTransportForward: JButton
    private val cropBetween: JButton
    private val cropBefore: JButton
    private val firstSelectionDeselect: JButton
    private val secondSelectedPointLabel: JLabel
    private val save: JButton
    private val cancel: JButton

    /**
     * Create the panel.
     */
    init {
        border = SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null)
        layout = BorderLayout(0, 0)
        val panel = JPanel()
        add(panel, BorderLayout.SOUTH)
        panel.layout = GridLayout(0, 1, 0, 0)
        val panel_2 = JPanel()
        val flowLayout = panel_2.layout as FlowLayout
        flowLayout.alignment = FlowLayout.RIGHT
        panel.add(panel_2)
        cancel = JButton(Messages.getString("EditingPanel.Cancel")) //$NON-NLS-1$
        cancel.addActionListener { evt: ActionEvent -> onCancel(evt) }
        panel_2.add(cancel)
        save = JButton(Messages.getString("EditingPanel.Save")) //$NON-NLS-1$
        save.addActionListener { evt: ActionEvent -> onSave(evt) }
        panel_2.add(save)
        val panel_3 = JPanel()
        panel_3.border = EmptyBorder(3, 3, 3, 3)
        add(panel_3, BorderLayout.CENTER)
        val gbl_panel_3 = GridBagLayout()
        gbl_panel_3.columnWidths = intArrayOf(0, 0, 0, 0, 0, 0)
        gbl_panel_3.rowHeights = intArrayOf(0, 0, 0)
        gbl_panel_3.columnWeights = doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0)
        gbl_panel_3.rowWeights = doubleArrayOf(0.0, 0.0, 0.0)
        panel_3.layout = gbl_panel_3
        val lblNewLabel = JLabel(Messages.getString("EditingPanel.PointA")) //$NON-NLS-1$
        val gbc_lblNewLabel = GridBagConstraints()
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST
        gbc_lblNewLabel.insets = Insets(0, 0, 5, 5)
        gbc_lblNewLabel.gridx = 0
        gbc_lblNewLabel.gridy = 0
        panel_3.add(lblNewLabel, gbc_lblNewLabel)
        firstSelectedPointLabel = JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST") //$NON-NLS-1$
        val gbc_firstSelectedPointLabel = GridBagConstraints()
        gbc_firstSelectedPointLabel.anchor = GridBagConstraints.WEST
        gbc_firstSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL
        gbc_firstSelectedPointLabel.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectedPointLabel.gridx = 1
        gbc_firstSelectedPointLabel.gridy = 0
        panel_3.add(firstSelectedPointLabel, gbc_firstSelectedPointLabel)
        firstSelectionTransportBack = JButton(IconHandler.loadIcon("arrow-left-s-line")) //$NON-NLS-1$
        val gbc_firstSelectionTransportBack = GridBagConstraints()
        gbc_firstSelectionTransportBack.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectionTransportBack.gridx = 2
        gbc_firstSelectionTransportBack.gridy = 0
        firstSelectionTransportBack.addActionListener { e: ActionEvent -> onFirstSelectionTransportBack(e) }
        panel_3.add(firstSelectionTransportBack, gbc_firstSelectionTransportBack)
        firstSelectionDeselect = JButton(IconHandler.loadIcon("close-circle-line")) //$NON-NLS-1$
        val gbc_firstSelectionDeselect = GridBagConstraints()
        gbc_firstSelectionDeselect.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectionDeselect.gridx = 3
        gbc_firstSelectionDeselect.gridy = 0
        firstSelectionDeselect.addActionListener { e: ActionEvent -> onFirstSelectionDeselect(e) }
        panel_3.add(firstSelectionDeselect, gbc_firstSelectionDeselect)
        firstSelectionTransportForward = JButton(IconHandler.loadIcon("arrow-right-s-line")) //$NON-NLS-1$
        val gbc_firstSelectionTransportForward = GridBagConstraints()
        gbc_firstSelectionTransportForward.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectionTransportForward.gridx = 4
        gbc_firstSelectionTransportForward.gridy = 0
        firstSelectionTransportForward.addActionListener { e: ActionEvent -> onFirstSelectionTransportForward(e) }
        panel_3.add(firstSelectionTransportForward, gbc_firstSelectionTransportForward)
        cropBefore = JButton(Messages.getString("EditingPanel.CropBefore")) //$NON-NLS-1$
        cropBefore.icon = IconHandler.loadIcon("scissors-cut-line")
        val gbc_cropBefore = GridBagConstraints()
        gbc_cropBefore.fill = GridBagConstraints.HORIZONTAL
        gbc_cropBefore.insets = Insets(0, 0, 5, 5)
        gbc_cropBefore.gridx = 5
        gbc_cropBefore.gridy = 0
        cropBefore.addActionListener { e: ActionEvent -> onCropBefore(e) }
        panel_3.add(cropBefore, gbc_cropBefore)
        cropBetween = JButton(Messages.getString("EditingPanel.CropBetween")) //$NON-NLS-1$
        cropBetween.icon = IconHandler.loadIcon("scissors-cut-line")
        val gbc_cropBetween = GridBagConstraints()
        gbc_cropBetween.fill = GridBagConstraints.HORIZONTAL
        gbc_cropBetween.insets = Insets(0, 0, 5, 5)
        gbc_cropBetween.gridx = 5
        gbc_cropBetween.gridy = 1
        cropBetween.addActionListener { e: ActionEvent -> onCropBetween(e) }
        panel_3.add(cropBetween, gbc_cropBetween)
        val label2 = JLabel(Messages.getString("EditingPanel.PointB")) //$NON-NLS-1$
        val gbc_label2 = GridBagConstraints()
        gbc_label2.anchor = GridBagConstraints.WEST
        gbc_label2.insets = Insets(0, 0, 5, 5)
        gbc_label2.gridx = 0
        gbc_label2.gridy = 2
        panel_3.add(label2, gbc_label2)
        secondSelectedPointLabel =
            JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST") //$NON-NLS-1$
        val gbc_secondSelectedPointLabel = GridBagConstraints()
        gbc_secondSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL
        gbc_secondSelectedPointLabel.anchor = GridBagConstraints.WEST
        gbc_secondSelectedPointLabel.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectedPointLabel.gridx = 1
        gbc_secondSelectedPointLabel.gridy = 2
        panel_3.add(secondSelectedPointLabel, gbc_secondSelectedPointLabel)
        secondSelectionTransportBack = JButton(IconHandler.loadIcon("arrow-left-s-line")) //$NON-NLS-1$
        val gbc_secondSelectionTransportBack = GridBagConstraints()
        gbc_secondSelectionTransportBack.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectionTransportBack.gridx = 2
        gbc_secondSelectionTransportBack.gridy = 2
        secondSelectionTransportBack.addActionListener { actionevent1: ActionEvent ->
            onSecondSelectionTransportBack(
                actionevent1
            )
        }
        panel_3.add(secondSelectionTransportBack, gbc_secondSelectionTransportBack)
        secondSelectionDeselect = JButton(IconHandler.loadIcon("close-circle-line")) //$NON-NLS-1$
        val gbc_secondSelectionDeselect = GridBagConstraints()
        gbc_secondSelectionDeselect.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectionDeselect.gridx = 3
        gbc_secondSelectionDeselect.gridy = 2
        secondSelectionDeselect.addActionListener { e: ActionEvent -> onSecondSelectionDeselect(e) }
        panel_3.add(secondSelectionDeselect, gbc_secondSelectionDeselect)
        secondSelectionTransportForward = JButton(IconHandler.loadIcon("arrow-right-s-line")) //$NON-NLS-1$
        val gbc_secondSelectionTransportForward = GridBagConstraints()
        gbc_secondSelectionTransportForward.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectionTransportForward.gridx = 4
        gbc_secondSelectionTransportForward.gridy = 2
        secondSelectionTransportForward.addActionListener { e: ActionEvent -> onSecondSelectionTransportForward(e) }
        panel_3.add(secondSelectionTransportForward, gbc_secondSelectionTransportForward)
        cropAfter = JButton(Messages.getString("EditingPanel.CropAfter")) //$NON-NLS-1$
        cropAfter.icon = IconHandler.loadIcon("scissors-cut-line")
        val gbc_cropAfter = GridBagConstraints()
        gbc_cropAfter.fill = GridBagConstraints.HORIZONTAL
        gbc_cropAfter.insets = Insets(0, 0, 5, 5)
        gbc_cropAfter.gridx = 5
        gbc_cropAfter.gridy = 2
        cropAfter.addActionListener { e: ActionEvent -> onCropAfter(e) }
        panel_3.add(cropAfter, gbc_cropAfter)
    }

    fun setSelection(selection: Set<WayPoint>?) {
        this.selection = ArrayList(selection)
        disableAndEnableButtons()
        updateLabels()
    }

    fun setTrack(track: GpxFile?) {
        this.track = track
    }

    fun setSelectionHandler(handler: MapSelectionHandler?) {
        selectionHandler = handler
    }

    /**
     * update the component labels
     */
    private fun updateLabels() {
        val size = selection.size
        if (size == 0) {
            firstSelectedPointLabel.text = Messages.getString("EditingPanel.Empty") //$NON-NLS-1$
            secondSelectedPointLabel.text = Messages.getString("EditingPanel.Empty") //$NON-NLS-1$
        } else if (size == 1) {
            firstSelectedPointLabel.text = makeDescriptorString(selection[0])
            secondSelectedPointLabel.text = Messages.getString("EditingPanel.Empty") //$NON-NLS-1$
        } else if (size == 2) {
            selection.sort(java.util.Comparator { o1: WayPoint, o2: WayPoint ->
                track!!.indexOfWayPoint(o2).orElseThrow()!!
                    .compareTo(track!!.indexOfWayPoint(o1).orElseThrow())
            })
            firstSelectedPointLabel.text = makeDescriptorString(selection[0])
            secondSelectedPointLabel.text = makeDescriptorString(selection[1])
        }
    }

    private fun makeDescriptorString(wayPoint: WayPoint): String {
        val lat = doubleToDegMinSec(
            wayPoint.latitude.toDegrees(),
            Messages.getString("EditingPanel.South"),
            Messages.getString("EditingPanel.North")
        ) //$NON-NLS-1$ //$NON-NLS-2$
        val lon = doubleToDegMinSec(
            wayPoint.longitude.toDegrees(),
            Messages.getString("EditingPanel.West"),
            Messages.getString("EditingPanel.East")
        ) //$NON-NLS-1$ //$NON-NLS-2$
        val time =
            wayPoint.time.map { temporal: ZonedDateTime? -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(temporal) }
                .orElse(Messages.getString("EditingPanel.UnknownDate")) //$NON-NLS-1$
        return String.format(Messages.getString("EditingPanel.WaypointLabelFormat"), lat, lon, time) //$NON-NLS-1$
    }

    private fun doubleToDegMinSec(lat: Double, negativeSide: String?, positiveSide: String?): String {
        var lat = lat
        val side = if (lat < 0) negativeSide else positiveSide
        lat = Math.abs(lat)
        val deg = Math.floor(lat).toInt()
        lat -= deg.toDouble()
        lat *= 60.0
        val min = Math.floor(lat).toInt()
        lat -= min.toDouble()
        lat *= 60.0
        val sec = Math.floor(lat).toInt()
        return String.format(Messages.getString("EditingPanel.DegMinSecFormat"), deg, min, sec, side) //$NON-NLS-1$
    }

    /**
     * Update enabled / disabled state of all buttons
     */
    private fun disableAndEnableButtons() {
        val firstTransportEnabled = !selection.isEmpty()
        val secondTransportEnabled = selection.size > 1
        firstSelectionTransportBack.isEnabled = firstTransportEnabled
        firstSelectionTransportForward.isEnabled = firstTransportEnabled
        firstSelectionDeselect.isEnabled = firstTransportEnabled
        secondSelectionTransportBack.isEnabled = secondTransportEnabled
        secondSelectionTransportForward.isEnabled = secondTransportEnabled
        secondSelectionDeselect.isEnabled = secondTransportEnabled
        cropBefore.isEnabled = firstTransportEnabled
        cropAfter.isEnabled = firstTransportEnabled
        cropBetween.isEnabled = secondTransportEnabled
    }

    private fun onSave(evt: ActionEvent) {
        try {
            track!!.save()
            JOptionPane.showMessageDialog(
                Application.Companion.getMainWindow().getFrame(),
                Messages.getString("EditingPanel.SaveSuccessful")
            ) //$NON-NLS-1$
        } catch (e: Exception) {
            EditingPanel.log.error("Error while saving GPX file", e) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                Application.Companion.getMainWindow().getFrame(),
                String.format(Messages.getString("EditingPanel.ErrorWhileSaving"), e.localizedMessage)
            ) //$NON-NLS-1$
        }
    }

    private fun onCancel(evt: ActionEvent) {
        var reallyCancel = false
        reallyCancel = if (track.isChanged()) {
            val result = JOptionPane.showConfirmDialog(
                Application.Companion.getMainWindow().getFrame(),
                Messages.getString("EditingPanel.ConfirmCancelWithoutSaving"),
                Messages.getString("EditingPanel.ReallyQuit"),
                JOptionPane.YES_NO_OPTION
            ) //$NON-NLS-1$ //$NON-NLS-2$
            result == JOptionPane.YES_OPTION
        } else {
            true
        }
        if (reallyCancel) {
            Application.Companion.getMainWindow()!!.exitEditingMode()
        }
    }

    private fun onFirstSelectionTransportBack(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val item = selection[0]
            val index = track!!.indexOfWayPoint(item).get()
            if (index.waypointId > 0) {
                val newSelection = track!!.gpx
                    .tracks[index.trackId]
                    .segments[index.segmentId]
                    .points[index.waypointId - 1]
                selection.set(0, newSelection)
                selectionHandler!!.setSelection(selection)
            }
        }
    }

    private fun onFirstSelectionTransportForward(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val item = selection[0]
            val index = track!!.indexOfWayPoint(item).get()
            val segment = track!!.gpx
                .tracks[index.trackId]
                .segments[index.segmentId]
            val points = segment.points
            if (index.waypointId < points.size - 1) {
                val newSelection = points[index.waypointId + 1]
                selection.set(0, newSelection)
                selectionHandler!!.setSelection(selection)
            }
        }
    }

    private fun onFirstSelectionDeselect(e: ActionEvent) {
        if (!selection.isEmpty()) {
            selection.removeAt(0)
            selectionHandler!!.setSelection(selection)
        }
    }

    private fun onSecondSelectionTransportBack(actionevent1: ActionEvent) {
        if (selection.size >= 2) {
            val item = selection[1]
            val index = track!!.indexOfWayPoint(item).get()
            if (index.waypointId > 0) {
                val newSelection = track!!.gpx
                    .tracks[index.trackId]
                    .segments[index.segmentId]
                    .points[index.waypointId - 1]
                selection.set(1, newSelection)
                selectionHandler!!.setSelection(selection)
            }
        }
    }

    private fun onSecondSelectionDeselect(e: ActionEvent) {
        if (selection.size >= 2) {
            selection.removeAt(1)
            selectionHandler!!.setSelection(selection)
        }
    }

    private fun onSecondSelectionTransportForward(e: ActionEvent) {
        if (selection.size >= 2) {
            val item = selection[1]
            val index = track!!.indexOfWayPoint(item).get()
            val segment = track!!.gpx
                .tracks[index.trackId]
                .segments[index.segmentId]
            val points = segment.points
            if (index.waypointId < points.size - 1) {
                val newSelection = points[index.waypointId + 1]
                selection.set(1, newSelection)
                selectionHandler!!.setSelection(selection)
            }
        }
    }

    private fun onCropBefore(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val endpoint = selection[0]
            val index = track!!.indexOfWayPoint(endpoint).get()
            val toBeDeleted: List<WayPoint> = track!!.gpx.tracks[index.trackId]
                .segments[index.segmentId].points.subList(0, index.waypointId)
            val gpx1 = track!!.gpx.toBuilder()
                .trackFilter()
                .map { track: Track ->
                    track.toBuilder()
                        .map { segment: TrackSegment ->
                            segment.toBuilder()
                                .filter { it: WayPoint -> !toBeDeleted.contains(it) }
                                .build()
                        }
                        .build()
                }
                .build()
                .build()
            track!!.gpx = gpx1
        }
    }

    private fun onCropAfter(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val endPoint = selection[selection.size - 1]
            val index = track!!.indexOfWayPoint(endPoint).get()
            val points = track!!.gpx.tracks[index.trackId]
                .segments[index.segmentId].points
            val toBeDeleted: List<WayPoint> = points.subList(index.waypointId + 1, points.size)
            val gpx1 = track!!.gpx.toBuilder()
                .trackFilter()
                .map { track: Track ->
                    track.toBuilder()
                        .map { segment: TrackSegment ->
                            segment.toBuilder()
                                .filter { it: WayPoint -> !toBeDeleted.contains(it) }
                                .build()
                        }
                        .build()
                }
                .build()
                .build()
            track!!.gpx = gpx1
        }
    }

    private fun onCropBetween(e: ActionEvent) {
        if (selection.size == 2) {
            val point1 = selection[0]
            val point2 = selection[1]
            val idx1 = track!!.indexOfWayPoint(point1).get()
            val idx2 = track!!.indexOfWayPoint(point2).get()
            if (idx1.isOnSameSegment(idx2)) {
                var i1 = idx1.waypointId
                var i2 = idx2.waypointId
                if (i1 > i2) {
                    val o = i2
                    i2 = i1
                    i1 = o
                }
                val points = track!!.gpx.tracks[idx1.trackId]
                    .segments[idx1.segmentId].points
                val toBeDeleted: List<WayPoint> = points.subList(i1 + 1, i2)
                val gpx1 = track!!.gpx.toBuilder()
                    .trackFilter()
                    .map { track: Track ->
                        track.toBuilder()
                            .map { segment: TrackSegment ->
                                segment.toBuilder()
                                    .filter { it: WayPoint -> !toBeDeleted.contains(it) }
                                    .build()
                            }
                            .build()
                    }
                    .build()
                    .build()
                track!!.gpx = gpx1
            } else {
                EditingPanel.log.error("Selections not on same track: {} and {}", idx1, idx2) //$NON-NLS-1$
            }
        }
    }

    companion object {
        private const val serialVersionUID = 6937011900054488316L
    }
}