package de.taschi.bulkgpxviewer.ui;

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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.geo.GpxViewerTrack;

public class SidePanel extends JPanel {
	
	private static final Logger LOG = LogManager.getLogger(SidePanel.class);
	
	private static final long serialVersionUID = -4050409521285757121L;
	
	private JTree treeView;
	private DefaultMutableTreeNode rootNode;
	
	public SidePanel() {
		super();
		
		setLayout(new BorderLayout());
		
		
		createTreeModel();
		treeView = new JTree(rootNode);
		
		JScrollPane scrollPane = new JScrollPane(treeView, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
	            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
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
		HashMap<Integer, DefaultMutableTreeNode> yearNodes = new HashMap<>();
		DefaultMutableTreeNode unknownYearNode = null;
		
		for (GpxViewerTrack track : tracks) {

			DefaultMutableTreeNode yearNode;
			Instant startedAt = track.getStartedAt();
			if (startedAt != null) {
				ZonedDateTime startDate = startedAt.atZone(ZoneId.systemDefault());
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
			
			yearNode.add(makeNodeFor(track));
		}
		
		if (treeView != null) {
			((DefaultTreeModel) treeView.getModel()).reload();
		}
	}

	private MutableTreeNode makeNodeFor(GpxViewerTrack track) {
		return new GpxFileTreeNode(track);
	}

	public void updateModel() {
		createTreeModel();
	}
}
