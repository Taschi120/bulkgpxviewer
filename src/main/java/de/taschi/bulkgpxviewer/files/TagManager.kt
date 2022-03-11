package de.taschi.bulkgpxviewer.files

import com.fasterxml.jackson.jr.ob.JSON
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
import com.google.inject.Singleton
import lombok.Synchronized
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.util.*
import java.util.function.Function

@Log4j2
@Singleton
class TagManager constructor() {
    /**
     * A list of "canonical" tags built into the software
     */
    private var builtInTags: List<Tag> = ArrayList()

    /**
     * A list of all user-defined tags the application has "seen"
     * since start-up
     */
    private val knownUserTags: MutableList<Tag> = ArrayList()

    init {
        try {
            @Cleanup val inputStream: InputStream = javaClass.getClassLoader().getResourceAsStream("tags.json")
            builtInTags = JSON.std.listOfFrom(Tag::class.java, inputStream)
        } catch (e: IOException) {
            TagManager.log.error("Error while loading built-in tags", e)
        }
    }

    /**
     * Gets a list of all "known" tags, for purposes of e. g. autocompletion
     * @return
     */
    val knownTags: List<Tag>
        get() {
            return Collections.unmodifiableList(builtInTags)
        }

    /**
     * Notify the tag manager that a tag exists
     * @param tag
     */
    @Synchronized
    fun notifyForNewTag(tag: Tag) {
        if (!builtInTags.contains(tag) && !knownUserTags.contains(tag)) {
            TagManager.log.info("Remembering user tag: " + tag)
            knownUserTags.add(tag)
        }
    }

    @Synchronized
    fun fromString(name: String): Tag {
        for (t: Tag in builtInTags) {
            if ((t.getName() == name)) {
                return t
            }
        }
        for (t2: Tag in knownUserTags) {
            if ((t2.getName() == name)) {
                return t2
            }
        }
        val t3: Tag = Tag()
        t3.setName(name)
        t3.setUserDefined(true)
        notifyForNewTag(t3)
        return t3
    }

    fun getLocalizedName(tag: Tag?): String {
        // TODO implement me!
        return tag.getName()
    }

    private fun getMyExtensionNode(gpxFile: GpxFile): Optional<Element?> {
        return gpxFile.getGpx().getExtensions()
            .map(Function({ it: Document -> findFirstChildTagWithName(it, "extensions") }))
            .map(Function({ it: Element? -> findFirstChildTagWithName(it, "bulkgpxviewer") }))
    }

    private fun findFirstChildTagWithName(doc: Document, name: String): Element? {
        TagManager.log.debug("Searching for node named {}", name)
        for (i in 0 until doc.getChildNodes().getLength()) {
            val node: Node = doc.getChildNodes().item(i)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                TagManager.log.debug("Element is named {}", element.getTagName())
                if ((element.getTagName() == name)) {
                    return element
                }
            }
        }
        return null
    }

    private fun findFirstChildTagWithName(rnode: Node?, name: String): Element? {
        TagManager.log.debug("Searching for node named {}", name)
        for (i in 0 until rnode!!.getChildNodes().getLength()) {
            val node: Node = rnode.getChildNodes().item(i)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                TagManager.log.debug("Element is named {}", element.getTagName())
                if ((element.getTagName() == name)) {
                    return element
                }
            }
        }
        return null
    }

    fun getTagsForGpxFile(gpxFile: GpxFile): List<Tag> {
        val extNode: Optional<Element?> = getMyExtensionNode(gpxFile)
        if (extNode.isPresent()) {
            val children: NodeList = extNode.get().getChildNodes()
            val result: ArrayList<Tag> = ArrayList()
            for (i in 0 until children.getLength()) {
                val node: Node = children.item(i)
                if (node is Element) {
                    val element: Element = node
                    if ((element.getTagName() == "tag")) {
                        val tag: Tag = fromString(element.getTextContent())
                        result.add(tag)
                        TagManager.log.info("Found tag {}", tag)
                    }
                }
            }
            TagManager.log.info("Found {} tags", result.size)
            return result
        } else {
            return Collections.unmodifiableList(emptyList())
        }
    }
}