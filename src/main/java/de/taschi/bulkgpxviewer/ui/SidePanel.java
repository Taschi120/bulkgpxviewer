package de.taschi.bulkgpxviewer.ui;

import java.awt.BorderLayout;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.gpx.GpxViewerTrack;

public class SidePanel extends JPanel {
	
	private static final Logger LOG = LogManager.getLogger(SidePanel.class);
	
	private static final long serialVersionUID = -4050409521285757121L;
	
	private JTree treeView;
	private DefaultMutableTreeNode rootNode;

	private DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME;
	
	public SidePanel() {
		super();
		
		setLayout(new BorderLayout());
		
		createTreeModel();
		treeView = new JTree(rootNode);
		
		add(treeView, BorderLayout.CENTER);
		
		LoadedFileManager.getInstance().addChangeListener(this::createTreeModel);
	}

	private void createTreeModel() {
		
		LOG.info("Recreating tree model");
		
		if (rootNode == null) {
			rootNode = new DefaultMutableTreeNode("All files");
		} else {
			rootNode.removeAllChildren();
		}
		
		List<GpxViewerTrack> tracks = LoadedFileManager.getInstance().getLoadedTracks();
		HashMap<Integer, DefaultMutableTreeNode> yearNodes = new HashMap<>();
		
		for (GpxViewerTrack track : tracks) {
			// TODO handle GPX files without date?
			ZonedDateTime startDate = track.getStartedAt().atZone(ZoneId.systemDefault());
			int year = startDate.getYear();
			
			DefaultMutableTreeNode yearNode = yearNodes.get(year);
			
			if (yearNode == null) {
				LOG.info("Making node for year " + year);
				yearNode = new DefaultMutableTreeNode(year);
				yearNodes.put(year, yearNode);
				rootNode.add(yearNode);
			}
			
			yearNode.add(makeNodeFor(track));
		}
		
		if (treeView != null) {
			((DefaultTreeModel) treeView.getModel()).reload();
		}
	}

	private MutableTreeNode makeNodeFor(GpxViewerTrack track) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(track.getFileName().getFileName());
		
		DefaultMutableTreeNode startDate = new DefaultMutableTreeNode("Started at: " + track.getStartedAt().atZone(ZoneId.systemDefault()).format(dtf));
		root.add(startDate);
		
		DefaultMutableTreeNode length = new DefaultMutableTreeNode("Route length: " + track.getRouteLengthInKilometers() + " km");
		root.add(length);
		
		return root;
	}
}
