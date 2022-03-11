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
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.ui.Messages
import java.lang.Exception
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
 */   class SettingsWindow(mainWindow: MainWindow?) : JDialog(mainWindow.getFrame()) {
    private var colorsIntl: MutableList<Color?>? = null
    private val contentPanel = JPanel()
    val colorList: JList<Color?>? = null
    private var unitSystem: JComboBox<*>? = null

    @Inject
    private val settingsManager: SettingsManager? = null

    /**
     * Create the dialog.
     */
    init {
        Application.Companion.getInjector().injectMembers(this)
        title = Messages.getString("SettingsWindow.Settings") //$NON-NLS-1$
        addWindowListener(object : WindowAdapter() {
            override fun windowOpened(e: WindowEvent) {
                initModel()
            }
        })
        setBounds(100, 100, 450, 300)
        contentPane.layout = BorderLayout()
        contentPanel.border = EmptyBorder(5, 5, 5, 5)
        contentPane.add(contentPanel, BorderLayout.CENTER)
        contentPanel.layout = BorderLayout(0, 0)
        run {
            val tabbedPane = JTabbedPane(JTabbedPane.TOP)
            contentPanel.add(tabbedPane)
            run {
                val panel = JPanel()
                panel.border = EmptyBorder(5, 5, 5, 5)
                tabbedPane.addTab(Messages.getString("SettingsWindow.GeneralTab"), null, panel, null) //$NON-NLS-1$
                val gbl_panel = GridBagLayout()
                gbl_panel.columnWidths = intArrayOf(0, 0)
                gbl_panel.rowHeights = intArrayOf(0, 0)
                gbl_panel.columnWeights = doubleArrayOf(0.0, 1.0)
                gbl_panel.rowWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
                panel.layout = gbl_panel
                run {
                    val lblNewLabel = JLabel(Messages.getString("SettingsWindow.Units")) //$NON-NLS-1$
                    val gbc_lblNewLabel = GridBagConstraints()
                    gbc_lblNewLabel.insets = Insets(0, 0, 0, 5)
                    gbc_lblNewLabel.anchor = GridBagConstraints.EAST
                    gbc_lblNewLabel.gridx = 0
                    gbc_lblNewLabel.gridy = 0
                    panel.add(lblNewLabel, gbc_lblNewLabel)
                }
                run {
                    unitSystem = JComboBox<Any>()
                    unitSystem.setModel(
                        DefaultComboBoxModel<Any?>(
                            arrayOf(
                                Messages.getString("SettingsWindow.Metric"),
                                Messages.getString("SettingsWindow.Imperial")
                            )
                        )
                    ) //$NON-NLS-1$ //$NON-NLS-2$
                    val gbc_unitSystem = GridBagConstraints()
                    gbc_unitSystem.fill = GridBagConstraints.HORIZONTAL
                    gbc_unitSystem.gridx = 1
                    gbc_unitSystem.gridy = 0
                    panel.add(unitSystem, gbc_unitSystem)
                }
            }
            run {
                val panel = JPanel()
                tabbedPane.addTab(Messages.getString("SettingsWindow.TrailColors"), null, panel, null) //$NON-NLS-1$
                val gbl_panel = GridBagLayout()
                gbl_panel.columnWidths = intArrayOf(0, 0)
                gbl_panel.rowHeights = intArrayOf(0, 0, 0)
                gbl_panel.columnWeights = doubleArrayOf(1.0, 0.0)
                gbl_panel.rowWeights = doubleArrayOf(1.0, 1.0, Double.MIN_VALUE)
                panel.layout = gbl_panel
                run {
                    this.colorList = JList()
                    colorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                    val gbc_list = GridBagConstraints()
                    gbc_list.gridheight = 2
                    gbc_list.insets = Insets(0, 0, 0, 5)
                    gbc_list.fill = GridBagConstraints.BOTH
                    gbc_list.gridx = 0
                    gbc_list.gridy = 0
                    panel.add(this.colorList, gbc_list)
                }
                run {
                    val panel_1 = JPanel()
                    val gbc_panel_1 = GridBagConstraints()
                    gbc_panel_1.gridheight = 2
                    gbc_panel_1.insets = Insets(0, 0, 5, 0)
                    gbc_panel_1.fill = GridBagConstraints.BOTH
                    gbc_panel_1.gridx = 1
                    gbc_panel_1.gridy = 0
                    panel.add(panel_1, gbc_panel_1)
                    val gbl_panel_1 = GridBagLayout()
                    gbl_panel_1.columnWidths = intArrayOf(89, 89, 0)
                    gbl_panel_1.rowHeights = intArrayOf(23, 0, 0)
                    gbl_panel_1.columnWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
                    gbl_panel_1.rowWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
                    panel_1.layout = gbl_panel_1
                    run {
                        val btnNewButton_1 = JButton(Messages.getString("SettingsWindow.Add")) //$NON-NLS-1$
                        btnNewButton_1.addActionListener(ActionListener { onAdd() })
                        val gbc_btnNewButton_1 = GridBagConstraints()
                        gbc_btnNewButton_1.gridwidth = 2
                        gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL
                        gbc_btnNewButton_1.anchor = GridBagConstraints.NORTH
                        gbc_btnNewButton_1.insets = Insets(0, 0, 5, 5)
                        gbc_btnNewButton_1.gridx = 0
                        gbc_btnNewButton_1.gridy = 0
                        panel_1.add(btnNewButton_1, gbc_btnNewButton_1)
                    }
                    run {
                        val btnNewButton = JButton(Messages.getString("SettingsWindow.Remove")) //$NON-NLS-1$
                        btnNewButton.addActionListener(object : ActionListener {
                            override fun actionPerformed(e: ActionEvent) {
                                onRemove()
                            }
                        })
                        val gbc_btnNewButton = GridBagConstraints()
                        gbc_btnNewButton.gridwidth = 2
                        gbc_btnNewButton.insets = Insets(0, 0, 0, 5)
                        gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL
                        gbc_btnNewButton.anchor = GridBagConstraints.NORTH
                        gbc_btnNewButton.gridx = 0
                        gbc_btnNewButton.gridy = 1
                        panel_1.add(btnNewButton, gbc_btnNewButton)
                    }
                }
            }
        }
        run {
            val buttonPane = JPanel()
            buttonPane.layout = FlowLayout(FlowLayout.RIGHT)
            contentPane.add(buttonPane, BorderLayout.SOUTH)
            run {
                val okButton = JButton(Messages.getString("SettingsWindow.OK")) //$NON-NLS-1$
                okButton.addActionListener(object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        onOk()
                    }
                })
                okButton.actionCommand = "OK" //$NON-NLS-1$
                buttonPane.add(okButton)
                getRootPane().defaultButton = okButton
            }
            run {
                val cancelButton = JButton(Messages.getString("SettingsWindow.Cancel")) //$NON-NLS-1$
                cancelButton.addActionListener(object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        onCancel()
                    }
                })
                cancelButton.actionCommand = "Cancel" //$NON-NLS-1$
                buttonPane.add(cancelButton)
            }
        }
    }

    protected fun onCancel() {
        isVisible = false
    }

    protected fun onOk() {
        val settings = settingsManager.getSettings()
        settings.routeColors = ColorConverter.convertToSettings(colorsIntl)
        if (unitSystem!!.selectedIndex == 0) {
            settings.unitSystem = UnitSystem.METRIC
        } else {
            settings.unitSystem = UnitSystem.IMPERIAL
        }
        settingsManager!!.saveSettings()
        settingsManager.fireNotifications()
        isVisible = false
    }

    private fun initModel() {
        val settings = settingsManager.getSettings()
        colorsIntl = ColorConverter.convertToAwt(settings.routeColors)
        colorList!!.setCellRenderer(ColorListItemRenderer())
        colorList.setListData(Vector(colorsIntl))
        if (settings.unitSystem == UnitSystem.METRIC) {
            unitSystem!!.setSelectedIndex(0)
        } else {
            unitSystem!!.setSelectedIndex(1)
        }
    }

    private fun onAdd() {
        val c: Color = ColorChooserDialog.Companion.showColorPicker()
        if (c != null) {
            colorsIntl!!.add(c)
            colorList!!.setListData(Vector(colorsIntl))
        }
    }

    private fun onRemove() {
        val c = colorList!!.selectedValue
        colorsIntl!!.remove(c)
        colorList.setListData(Vector(colorsIntl))
    }

    companion object {
        private const val serialVersionUID = 1202795436996433035L

        /**
         * Launch the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val dialog = SettingsWindow(null)
                dialog.defaultCloseOperation = DISPOSE_ON_CLOSE
                dialog.isVisible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}