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

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.ui.Messages;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SidePanel extends JPanel {
	
	private static final Logger LOG = LogManager.getLogger(SidePanel.class);
	
	private static final long serialVersionUID = -4050409521285757121L;
	
	private JTree treeView;
	private DefaultMutableTreeNode rootNode;
	
	private HashMap<Integer, DefaultMutableTreeNode> yearNodes = new HashMap<>();
	private HashMap<Path, GpxFileTreeNode> trackNodes = new HashMap<>();
	private DefaultMutableTreeNode unknownYearNode = null;
	
	private GpxFilePopupMenu filePopupMenu;
	
	@Inject
	private LoadedFileManager loadedFileManager;
	
	public SidePanel() {
		super();
		
		Application.getInjector().injectMembers(this);
		
		setLayout(new BorderLayout());
		
		
		createTreeModel();
		treeView = new JTree(rootNode);
		
		JScrollPane scrollPane = new JScrollPane(treeView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
	            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		filePopupMenu = new GpxFilePopupMenu();
		treeView.addMouseListener(new SidePanelMouseListener());
		
		add(scrollPane, BorderLayout.CENTER);
		
		loadedFileManager.addChangeListener(this::createTreeModel);
	}

	private synchronized void createTreeModel() {
		
		LOG.info("Recreating tree model"); //$NON-NLS-1$
		
		if (rootNode == null) {
			rootNode = new DefaultMutableTreeNode(Messages.getString("SidePanel.allFiles")); //$NON-NLS-1$
		} else {
			rootNode.removeAllChildren();
		}
		
		List<GpxFile> tracks = loadedFileManager.getLoadedTracks();
		
		cullUnnecessaryTrackNodes(tracks);
		
		for (GpxFile track : tracks) {
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
	private void cullUnnecessaryTrackNodes(List<GpxFile> loadedTracks) {
		Set<Path> loadedTrackPaths = loadedTracks.stream().map(GpxFile::getFileName).collect(Collectors.toSet());
		Set<Path> pathsInTree = new HashSet<Path>(trackNodes.keySet());
		
		for(Path p : pathsInTree) {
			if (!loadedTrackPaths.contains(p)) {
				GpxFileTreeNode node = trackNodes.get(p);
				((DefaultMutableTreeNode) node.getParent()).remove(node);
				
				trackNodes.remove(p);
			}
		}
	}

	private DefaultMutableTreeNode makeOrUpdateYearNode(GpxFile track, DefaultMutableTreeNode parent) {
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
				unknownYearNode = new DefaultMutableTreeNode(Messages.getString("SidePanel.unknownYear")); //$NON-NLS-1$
				rootNode.add(unknownYearNode);
			}
			yearNode = unknownYearNode;
		}
		
		rootNode.add(yearNode);
		return yearNode;
	}

	private void makeOrUpdateTrackNode(GpxFile track, DefaultMutableTreeNode parent) {
		GpxFileTreeNode result = trackNodes.get(track.getFileName());
		if (result == null) {
			LOG.debug("Making new tree node for {}", track.getFileName().toString()); //$NON-NLS-1$
			result = new GpxFileTreeNode(track);
			trackNodes.put(track.getFileName(), result);
			parent.add(result);
		} else {
			LOG.debug("Updating tree node for {}", track.getFileName().toString()); //$NON-NLS-1$
			result.update();
		}
	}
	
	private void cullUnnecessaryYearTreeNodes() {
		List<Integer> years = new ArrayList<>(yearNodes.keySet());
		for (Integer year : years) {
			DefaultMutableTreeNode node = yearNodes.get(year);
			if (node != null && rootNode.isNodeChild(node) && node.getChildCount() == 0) {
				rootNode.remove(node);
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
	
	private class SidePanelMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {

		    if (SwingUtilities.isRightMouseButton(e)) {
		    	// check if we need to open context menu
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
		    } else if (SwingUtilities.isLeftMouseButton(e)) {
		    	// if exactly one GPX file is now selected, update graph panels
		    	var selectionModel = treeView.getSelectionModel();
		    	if(selectionModel.getSelectionCount() == 1) {
		    		var selected = selectionModel.getLeadSelectionPath().getLastPathComponent();
		    		if (selected instanceof GpxFileRelatedNode) {
		    			var node = (GpxFileRelatedNode) selected;
		    			var file = node.getGpxFile();
		    			
		    			log.info("Selected GPX file is now {}", file.getFileName());
		    			
		    			Application.getMainWindow().setSelectedGpxFile(Optional.of(file));
		    		} else {
		    			Application.getMainWindow().setSelectedGpxFile(Optional.empty());
		    		}
		    	} else {
	    			Application.getMainWindow().setSelectedGpxFile(Optional.empty());
		    	}
		    }
		}
	}
	
}
