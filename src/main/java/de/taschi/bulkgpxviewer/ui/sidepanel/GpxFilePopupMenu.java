package de.taschi.bulkgpxviewer.ui.sidepanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;

public class GpxFilePopupMenu extends JPopupMenu {
	
	private static final long serialVersionUID = -3214637893458879098L;
	
	private GpxViewerTrack track;
	private GpxFileTreeNode node;
	
	private JMenuItem edit;
	private JMenuItem rename;
	private JMenuItem addTag;

	public GpxFilePopupMenu() {
		super();
		
		edit = new JMenuItem("Edit");
		add(edit);
		
		rename = new JMenuItem("Rename...");
		add(rename);
		
		addTag = new JMenuItem("Add tag...");
		add(addTag);
	}
	
	public void openOnTreeItem(JTree tree, GpxFileTreeNode node, int x, int y) {
		this.node = node;
		this.track = node.getTrack();
		show(tree, x, y);
	}

}
