package de.taschi.bulkgpxviewer.ui.sidepanel
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
 */
import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.files.LoadedFileChangeListener
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.ui.Messages
import org.apache.logging.log4j.LogManager
import java.awt.BorderLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.nio.file.Path
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.ScrollPaneConstants
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

class SidePanel : JPanel() {

    @Inject
    private lateinit var loadedFileManager: LoadedFileManager

    private val treeView: JTree?
    private var rootNode: DefaultMutableTreeNode? = null
    private val yearNodes: HashMap<Int, DefaultMutableTreeNode?> = HashMap<Int, DefaultMutableTreeNode?>()
    private val trackNodes: HashMap<Path, GpxFileTreeNode> = HashMap<Path, GpxFileTreeNode>()
    private var unknownYearNode: DefaultMutableTreeNode? = null
    private val filePopupMenu: GpxFilePopupMenu

    init {
        Application.getInjector().injectMembers(this)
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
        loadedFileManager.addChangeListener(object : LoadedFileChangeListener {
            override fun onLoadedFileChange() {
                createTreeModel()
            }
        })
    }

    @Synchronized
    private fun createTreeModel() {
        LOG.info("Recreating tree model") //$NON-NLS-1$
        if (rootNode == null) {
            rootNode = DefaultMutableTreeNode(Messages.getString("SidePanel.allFiles")) //$NON-NLS-1$
        } else {
            rootNode!!.removeAllChildren()
        }
        val tracks: List<GpxFile> = loadedFileManager.getLoadedTracks()
        cullUnnecessaryTrackNodes(tracks)
        for (track in tracks) {
            val yearNode: DefaultMutableTreeNode? = makeOrUpdateYearNode(track, rootNode)
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
    private fun cullUnnecessaryTrackNodes(loadedTracks: List<GpxFile>) {
        val loadedTrackPaths: Set<Path> = loadedTracks.map { it.fileName }.toSet()
        val pathsInTree: Set<Path> = HashSet<Path>(trackNodes.keys)
        for (p in pathsInTree) {
            if (!loadedTrackPaths.contains(p)) {
                val node = trackNodes[p]
                (node!!.parent as DefaultMutableTreeNode).remove(node)
                trackNodes.remove(p)
            }
        }
    }

    private fun makeOrUpdateYearNode(track: GpxFile, parent: DefaultMutableTreeNode?): DefaultMutableTreeNode? {
        var yearNode: DefaultMutableTreeNode?
        val startedAt: Optional<Instant> = track.startedAt
        if (startedAt.isPresent) {
            val startDate: ZonedDateTime = startedAt.get().atZone(ZoneId.systemDefault())
            val year: Int = startDate.getYear()
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

    private fun makeOrUpdateTrackNode(track: GpxFile, parent: DefaultMutableTreeNode?) {
        var result = trackNodes[track.fileName]
        if (result == null) {
            LOG.debug("Making new tree node for {}", track.fileName.toString()) //$NON-NLS-1$
            result = GpxFileTreeNode(track)
            trackNodes[track.fileName] = result
            parent!!.add(result)
        } else {
            LOG.debug("Updating tree node for {}", track.fileName.toString()) //$NON-NLS-1$
            result.update()
        }
    }

    private fun cullUnnecessaryYearTreeNodes() {
        val years: List<Int> = ArrayList(yearNodes.keys)
        for (year in years) {
            val node: DefaultMutableTreeNode? = yearNodes[year]
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
                val row: Int = treeView!!.getRowForLocation(e.x, e.y)
                treeView.setSelectionRow(row)
                val selectionModel: TreeSelectionModel = treeView.selectionModel
                val selectionPath: TreePath = selectionModel.leadSelectionPath
                if (selectionPath != null) {
                    val selected = selectionPath.lastPathComponent
                    if (selected is GpxFileTreeNode) {
                        filePopupMenu.openOnTreeItem(treeView, selected, e.x, e.y)
                    }
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                // if exactly one GPX file is now selected, update graph panels
                val selectionModel: TreeSelectionModel = treeView!!.selectionModel
                if (selectionModel.selectionCount == 1) {
                    val selected: Any = selectionModel.leadSelectionPath.lastPathComponent
                    if (selected is GpxFileRelatedNode) {
                        val file: GpxFile? = selected.gpxFile
                        log.info("Selected GPX file is now {}", file!!.fileName)
                        Application.getMainWindow()!!.setSelectedGpxFile(Optional.ofNullable<GpxFile>(file))
                    } else {
                        Application.getMainWindow()!!.setSelectedGpxFile(Optional.empty<GpxFile>())
                    }
                } else {
                    Application.getMainWindow()!!.setSelectedGpxFile(Optional.empty<GpxFile>())
                }
            }
        }
    }

    companion object {
        private val LOG = LogManager.getLogger(SidePanel::class.java)
        private const val serialVersionUID = -4050409521285757121L
        private val log = LogManager.getLogger(SidePanel::class.java)
    }
}