package de.taschi.bulkgpxviewer.ui.sidepanel;

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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.ui.IconHandler;
import de.taschi.bulkgpxviewer.ui.Messages;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GpxFilePopupMenu extends JPopupMenu {
	
	private static final long serialVersionUID = -3214637893458879098L;
	
	private GpxFile track;
	private GpxFileTreeNode node;
	
	private JMenuItem edit;
	private JMenuItem rename;
	private JMenuItem addTag;

	@Inject
	private LoadedFileManager loadedFileManager;

	public GpxFilePopupMenu() {
		super();
		
		Application.getInjector().injectMembers(this);
		
		edit = new JMenuItem(Messages.getString("GpxFilePopupMenu.Edit")); //$NON-NLS-1$
		edit.setIcon(IconHandler.loadIcon("edit-line")); //$NON-NLS-1$
		edit.addActionListener(this::onEdit);
		add(edit);
		
		rename = new JMenuItem(Messages.getString("GpxFilePopupMenu.Rename")); //$NON-NLS-1$
		rename.setIcon(IconHandler.loadIcon("input-cursor-move")); //$NON-NLS-1$
		rename.addActionListener(this::onRename);
		add(rename);
		
		addTag = new JMenuItem(Messages.getString("GpxFilePopupMenu.AddTag")); //$NON-NLS-1$
		addTag.setIcon(IconHandler.loadIcon("price-tag-3-line")); //$NON-NLS-1$
		add(addTag);
	}
	
	public void openOnTreeItem(JTree tree, GpxFileTreeNode node, int x, int y) {
		this.node = node;
		this.track = node.getTrack();
		show(tree, x, y);
	}

	private void onRename(ActionEvent evt) {
		var path = track.getFileName();
		log.info("Starting debug for {}", path); //$NON-NLS-1$
		
		var oldFileName = path.getFileName().toString();
		
		if(oldFileName.endsWith(".gpx")) { //$NON-NLS-1$
			oldFileName = oldFileName.substring(0, oldFileName.length() - 4);
		}
	
		var newFileName = JOptionPane.showInputDialog(String.format(Messages.getString("GpxFilePopupMenu.EnterNewFileName"), oldFileName), //$NON-NLS-1$
				oldFileName);
		
		if (StringUtils.isEmpty(newFileName)) {
			log.info("Renaming cancelled."); //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, Messages.getString("GpxFilePopupMenu.RenamingCancelled")); //$NON-NLS-1$
			return;
		}
		
		if (newFileName.contains("/") || newFileName.contains("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
			log.info("Invalid file name {}", newFileName); //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, Messages.getString("GpxFilePopupMenu.InvalidFileName")); //$NON-NLS-1$
			return;
		}
		
		if (!newFileName.endsWith(".gpx")) { //$NON-NLS-1$
			log.info("Adding .gpx ending to file name {}"); //$NON-NLS-1$
			newFileName += ".gpx"; //$NON-NLS-1$
		}
		
		var newPath = path.getParent().resolve(newFileName);
		
		if (Files.exists(newPath)) {
			log.error("Rename failed because file '{}' already exists", newPath); //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, String.format(Messages.getString("GpxFilePopupMenu.CouldNotRenameFileAlreadyExists"), newPath)); //$NON-NLS-1$
			return;
		}
		
		try {
			Files.move(path, newPath);
		} catch (IOException e) {
			log.error("Could not rename file", e); //$NON-NLS-1$
			JOptionPane.showMessageDialog(null, String.format(Messages.getString("GpxFilePopupMenu.ErrorWhileRenaming"), e.getLocalizedMessage())); //$NON-NLS-1$
			return;
		}
		
		try {
			loadedFileManager.refresh();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, String.format(Messages.getString("GpxFilePopupMenu.ErrorWhileReloading") //$NON-NLS-1$
					, e.getLocalizedMessage()));
		}
	}

	private void onEdit(ActionEvent e) {
		Application.getMainWindow().startEditingMode(track);
	}
	
}
