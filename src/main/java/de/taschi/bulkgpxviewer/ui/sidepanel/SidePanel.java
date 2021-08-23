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

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;
import de.taschi.bulkgpxviewer.ui.Messages;

public class SidePanel extends JPanel {
	
	private static final Logger LOG = LogManager.getLogger(SidePanel.class);
	
	private static final long serialVersionUID = -4050409521285757121L;
	
	private JTree treeView;
	private DefaultMutableTreeNode rootNode;
	
	private HashMap<Integer, DefaultMutableTreeNode> yearNodes = new HashMap<>();
	private HashMap<Path, GpxFileTreeNode> trackNodes = new HashMap<>();
	private DefaultMutableTreeNode unknownYearNode = null;
	
	private GpxFilePopupMenu filePopupMenu;
	
	public SidePanel() {
		super();
		
		setLayout(new BorderLayout());
		
		
		createTreeModel();
		treeView = new JTree(rootNode);
		
		JScrollPane scrollPane = new JScrollPane(treeView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
	            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		filePopupMenu = new GpxFilePopupMenu();
		treeView.addMouseListener(new MouseListener());
		
		add(scrollPane, BorderLayout.CENTER);
		
		LoadedFileManager.getInstance().addChangeListener(this::createTreeModel);
	}

	private synchronized void createTreeModel() {
		
		LOG.info("Recreating tree model"); //$NON-NLS-1$
		
		if (rootNode == null) {
			rootNode = new DefaultMutableTreeNode(Messages.getString("SidePanel.AllFiles")); //$NON-NLS-1$
		} else {
			rootNode.removeAllChildren();
		}
		
		List<GpxViewerTrack> tracks = LoadedFileManager.getInstance().getLoadedTracks();
		
		cullUnnecessaryTrackNodes(tracks);
		
		for (GpxViewerTrack track : tracks) {
			DefaultMutableTreeNode yearNode = makeOrUpdateYearNode(track, rootNode);
			makeOrUpdateTrackNode(track, yearNode);
		}
		
		cullUnnecessaryYearTreeNodes();
		
		if (treeView != null) {
			((DefaultTreeModel) treeView.getModel()).reload();
		}
	}
	
	/**
	 * Removes all track nodes which do not correspond with currently loaded files
	 */
	private void cullUnnecessaryTrackNodes(List<GpxViewerTrack> loadedTracks) {
		Set<Path> loadedTrackPaths = loadedTracks.stream().map(GpxViewerTrack::getFileName).collect(Collectors.toSet());
		Set<Path> pathsInTree = new HashSet<Path>(trackNodes.keySet());
		
		for(Path p : pathsInTree) {
			if (!loadedTrackPaths.contains(p)) {
				GpxFileTreeNode node = trackNodes.get(p);
				((DefaultMutableTreeNode) node.getParent()).remove(node);
				
				trackNodes.remove(p);
			}
		}
	}

	private DefaultMutableTreeNode makeOrUpdateYearNode(GpxViewerTrack track, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode yearNode;
		Optional<Instant> startedAt = track.getStartedAt();
		
		if (startedAt.isPresent()) {
			ZonedDateTime startDate = startedAt.get().atZone(ZoneId.systemDefault());
			int year = startDate.getYear();
			yearNode = yearNodes.get(year);
			
			if (yearNode == null) {
				LOG.info("Making node for year " + year); //$NON-NLS-1$
				yearNode = new DefaultMutableTreeNode(year);
				yearNodes.put(year, yearNode);
				rootNode.add(yearNode);
			}
			
		} else {
			if (unknownYearNode == null) {
				unknownYearNode = new DefaultMutableTreeNode(Messages.getString("SidePanel.unknownYear"));
				rootNode.add(unknownYearNode);
			}
			yearNode = unknownYearNode;
		}
		
		rootNode.add(yearNode);
		return yearNode;
	}

	private void makeOrUpdateTrackNode(GpxViewerTrack track, DefaultMutableTreeNode parent) {
		GpxFileTreeNode result = trackNodes.get(track.getFileName());
		if (result == null) {
			LOG.debug("Making new tree node for {}", track.getFileName().toString());
			result = new GpxFileTreeNode(track);
			trackNodes.put(track.getFileName(), result);
			parent.add(result);
		} else {
			LOG.debug("Updating tree node for {}", track.getFileName().toString());
			result.update();
		}
	}
	
	private void cullUnnecessaryYearTreeNodes() {
		List<Integer> years = new ArrayList<>(yearNodes.keySet());
		for (Integer year : years) {
			DefaultMutableTreeNode node = yearNodes.get(year);
			if(node.getChildCount() == 0) {
				((DefaultMutableTreeNode)node.getParent()).remove(year);
				yearNodes.remove(year);
			}
		}
		
		if (unknownYearNode != null && unknownYearNode.getChildCount() == 0) {
			if (rootNode.getIndex(unknownYearNode) != -1) {
				rootNode.remove(unknownYearNode);
			}
			unknownYearNode = null;
		}
	}

	public void updateModel() {
		createTreeModel();
	}
	
	private class MouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {

		    if (SwingUtilities.isRightMouseButton(e)) {

		        var row = treeView.getRowForLocation(e.getX(), e.getY());
		        treeView.setSelectionRow(row);
		        
		        var selectionModel = treeView.getSelectionModel();
		        var selectionPath = selectionModel.getLeadSelectionPath();
		        
		        if (selectionPath != null) {
			        var selected = selectionPath.getLastPathComponent();
			        
			        if(selected instanceof GpxFileTreeNode) {
				        filePopupMenu.openOnTreeItem(treeView, (GpxFileTreeNode) selected, e.getX(), e.getY());
			        }
			    }
		    }
		}
	}
	
}
