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
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.ui.IconHandler
import de.taschi.bulkgpxviewer.ui.Messages
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JTree

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
        edit.setIcon(IconHandler.loadIcon("edit-line")) //$NON-NLS-1$
        edit.addActionListener(ActionListener { e: ActionEvent -> onEdit(e) })
        add(edit)
        rename = JMenuItem(Messages.getString("GpxFilePopupMenu.Rename")) //$NON-NLS-1$
        rename.setIcon(IconHandler.loadIcon("input-cursor-move")) //$NON-NLS-1$
        rename.addActionListener(ActionListener { evt: ActionEvent -> onRename(evt) })
        add(rename)
        addTag = JMenuItem(Messages.getString("GpxFilePopupMenu.AddTag")) //$NON-NLS-1$
        addTag.setIcon(IconHandler.loadIcon("price-tag-3-line")) //$NON-NLS-1$
        add(addTag)
    }

    fun openOnTreeItem(tree: JTree?, node: GpxFileTreeNode, x: Int, y: Int) {
        this.node = node
        track = node.track
        show(tree, x, y)
    }

    private fun onRename(evt: ActionEvent) {
        val path: Path = track!!.fileName
        log.info("Starting debug for {}", path) //$NON-NLS-1$
        var oldFileName: String = path.fileName.toString()
        if (oldFileName.endsWith(".gpx")) { //$NON-NLS-1$
            oldFileName = oldFileName.substring(0, oldFileName.length - 4)
        }
        var newFileName: String = JOptionPane.showInputDialog(
            String.format(Messages.getString("GpxFilePopupMenu.EnterNewFileName"), oldFileName),  //$NON-NLS-1$
            oldFileName
        )
        if (StringUtils.isEmpty(newFileName)) {
            log.info("Renaming cancelled.") //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, Messages.getString("GpxFilePopupMenu.RenamingCancelled")) //$NON-NLS-1$
            return
        }
        if (newFileName.contains("/") || newFileName.contains("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
            log.info("Invalid file name {}", newFileName) //$NON-NLS-1$
            JOptionPane.showMessageDialog(null, Messages.getString("GpxFilePopupMenu.InvalidFileName")) //$NON-NLS-1$
            return
        }
        if (!newFileName.endsWith(".gpx")) { //$NON-NLS-1$
            log.info("Adding .gpx ending to file name {}") //$NON-NLS-1$
            newFileName += ".gpx" //$NON-NLS-1$
        }
        val newPath: Path = path.getParent().resolve(newFileName)
        if (Files.exists(newPath)) {
            log.error("Rename failed because file '{}' already exists", newPath) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                null,
                String.format(Messages.getString("GpxFilePopupMenu.CouldNotRenameFileAlreadyExists"), newPath)
            ) //$NON-NLS-1$
            return
        }
        try {
            Files.move(path, newPath)
        } catch (e: IOException) {
            log.error("Could not rename file", e) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                null,
                String.format(Messages.getString("GpxFilePopupMenu.ErrorWhileRenaming"), e.localizedMessage)
            ) //$NON-NLS-1$
            return
        }
        try {
            loadedFileManager?.refresh()
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
        Application.getMainWindow()!!.startEditingMode(track)
    }

    companion object {
        private const val serialVersionUID = -3214637893458879098L
        private val log = LoggerFactory.getLogger(GpxFilePopupMenu::class.java)
    }
}