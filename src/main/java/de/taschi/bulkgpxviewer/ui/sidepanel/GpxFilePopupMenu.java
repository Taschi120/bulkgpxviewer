package de.taschi.bulkgpxviewer.ui.sidepanel;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import org.apache.commons.lang3.StringUtils;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import de.taschi.bulkgpxviewer.ui.Messages;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GpxFilePopupMenu extends JPopupMenu {
	
	private static final long serialVersionUID = -3214637893458879098L;
	
	private GpxViewerTrack track;
	private GpxFileTreeNode node;
	
	private JMenuItem edit;
	private JMenuItem rename;
	private JMenuItem addTag;

	public GpxFilePopupMenu() {
		super();
		
		edit = new JMenuItem(Messages.getString("GpxFilePopupMenu.Edit")); //$NON-NLS-1$
		add(edit);
		
		rename = new JMenuItem(Messages.getString("GpxFilePopupMenu.Rename")); //$NON-NLS-1$
		rename.addActionListener(this::onRename);
		add(rename);
		
		addTag = new JMenuItem(Messages.getString("GpxFilePopupMenu.AddTag")); //$NON-NLS-1$
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
			LoadedFileManager.getInstance().refresh();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, String.format(Messages.getString("GpxFilePopupMenu.ErrorWhileReloading") //$NON-NLS-1$
					, e.getLocalizedMessage()));
		}
	}
	
}
