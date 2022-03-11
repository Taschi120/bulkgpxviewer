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
 */import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.ui.Messages
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import java.awt.event.MouseEvent
import java.time.Instant
import java.util.*
import java.util.function.Function
import javax.swing.tree.TreePath
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class SidePanel : JPanel() {
    private val treeView: JTree?
    private var rootNode: DefaultMutableTreeNode? = null
    private val yearNodes: HashMap<Int, DefaultMutableTreeNode?> = HashMap<Int, DefaultMutableTreeNode?>()
    private val trackNodes: HashMap<Path, GpxFileTreeNode> = HashMap<Path, GpxFileTreeNode>()
    private var unknownYearNode: DefaultMutableTreeNode? = null
    private val filePopupMenu: GpxFilePopupMenu

    @Inject
    private val loadedFileManager: LoadedFileManager? = null

    init {
        Application.Companion.getInjector().injectMembers(this)
        setLayout(BorderLayout())
        createTreeModel()
        treeView = JTree(rootNode)
        val scrollPane = JScrollPane(
            treeView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )
        filePopupMenu = GpxFilePopupMenu()
        treeView.addMouseListener(SidePanelMouseListener())
        add(scrollPane, BorderLayout.CENTER)
        loadedFileManager.addChangeListener(LoadedFileChangeListener { createTreeModel() })
    }

    @Synchronized
    private fun createTreeModel() {
        LOG.info("Recreating tree model") //$NON-NLS-1$
        if (rootNode == null) {
            rootNode = DefaultMutableTreeNode(Messages.getString("SidePanel.allFiles")) //$NON-NLS-1$
        } else {
            rootNode.removeAllChildren()
        }
        val tracks: List<GpxFile> = loadedFileManager.getLoadedTracks()
        cullUnnecessaryTrackNodes(tracks)
        for (track in tracks) {
            val yearNode: DefaultMutableTreeNode? = makeOrUpdateYearNode(track, rootNode)
            makeOrUpdateTrackNode(track, yearNode)
        }
        cullUnnecessaryYearTreeNodes()
        if (treeView != null) {
            (treeView.getModel() as DefaultTreeModel).reload()
        }
    }

    /**
     * Removes all track nodes which do not correspond with currently loaded files
     */
    private fun cullUnnecessaryTrackNodes(loadedTracks: List<GpxFile>) {
        val loadedTrackPaths: Set<Path> =
            loadedTracks.stream().map<Path>(Function<GpxFile, Path> { obj: GpxFile -> obj.getFileName() })
                .collect<Set<Path>, Any>(Collectors.toSet<Path>())
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
        val startedAt: Optional<Instant> = track.getStartedAt()
        if (startedAt.isPresent) {
            val startDate: ZonedDateTime = startedAt.get().atZone(ZoneId.systemDefault())
            val year: Int = startDate.getYear()
            yearNode = yearNodes[year]
            if (yearNode == null) {
                LOG.info("Making node for year $year") //$NON-NLS-1$
                yearNode = DefaultMutableTreeNode(year)
                yearNodes[year] = yearNode
                rootNode.add(yearNode)
            }
        } else {
            if (unknownYearNode == null) {
                unknownYearNode = DefaultMutableTreeNode(Messages.getString("SidePanel.unknownYear")) //$NON-NLS-1$
                rootNode.add(unknownYearNode)
            }
            yearNode = unknownYearNode
        }
        rootNode.add(yearNode)
        return yearNode
    }

    private fun makeOrUpdateTrackNode(track: GpxFile, parent: DefaultMutableTreeNode?) {
        var result = trackNodes[track.getFileName()]
        if (result == null) {
            LOG.debug("Making new tree node for {}", track.getFileName().toString()) //$NON-NLS-1$
            result = GpxFileTreeNode(track)
            trackNodes[track.getFileName()] = result
            parent.add(result)
        } else {
            LOG.debug("Updating tree node for {}", track.getFileName().toString()) //$NON-NLS-1$
            result.update()
        }
    }

    private fun cullUnnecessaryYearTreeNodes() {
        val years: List<Int> = ArrayList(yearNodes.keys)
        for (year in years) {
            val node: DefaultMutableTreeNode? = yearNodes[year]
            if (node != null && rootNode.isNodeChild(node) && node.getChildCount() == 0) {
                rootNode.remove(node)
                yearNodes.remove(year)
            }
        }
        if (unknownYearNode != null && unknownYearNode.getChildCount() == 0) {
            if (rootNode.getIndex(unknownYearNode) != -1) {
                rootNode.remove(unknownYearNode)
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
                val row: Int = treeView.getRowForLocation(e.x, e.y)
                treeView.setSelectionRow(row)
                val selectionModel: TreeSelectionModel = treeView.getSelectionModel()
                val selectionPath: TreePath = selectionModel.getLeadSelectionPath()
                if (selectionPath != null) {
                    val selected = selectionPath.lastPathComponent
                    if (selected is GpxFileTreeNode) {
                        filePopupMenu.openOnTreeItem(treeView, selected, e.x, e.y)
                    }
                }
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                // if exactly one GPX file is now selected, update graph panels
                val selectionModel: TreeSelectionModel = treeView.getSelectionModel()
                if (selectionModel.getSelectionCount() == 1) {
                    val selected: Any = selectionModel.getLeadSelectionPath().getLastPathComponent()
                    if (selected is GpxFileRelatedNode) {
                        val file: GpxFile? = selected.gpxFile
                        log.info("Selected GPX file is now {}", file.getFileName())
                        Application.Companion.getMainWindow().setSelectedGpxFile(Optional.of<GpxFile>(file))
                    } else {
                        Application.Companion.getMainWindow().setSelectedGpxFile(Optional.empty<GpxFile>())
                    }
                } else {
                    Application.Companion.getMainWindow().setSelectedGpxFile(Optional.empty<GpxFile>())
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