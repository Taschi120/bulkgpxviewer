package de.taschi.bulkgpxviewer.ui.sidepanel;

import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;

import com.google.inject.Inject;

import de.taschi.bulkgpxviewer.Application;
import de.taschi.bulkgpxviewer.files.TagManager;

public class TagNode extends DefaultMutableTreeNode implements GpxFileRelatedNode {

	private static final long serialVersionUID = -2085495184375672455L;
	
	@Inject
	private TagManager tagManager;
	
	private final GpxFileTreeNode parent;
	
	public TagNode(GpxFileTreeNode parent) {
		this.parent = parent;
		Application.getInjector().injectMembers(this);
		
		update();
	}
	
	@Override
	public GpxFileTreeNode getGpxFileTreeNode() {
		return parent;
	}

	@Override
	public void update() {
		var tags = tagManager.getTagsForGpxFile(parent.getGpxFile());
		
		var format = "Tags: %s";
		var tagList = "none";
		if(!tags.isEmpty()) {
			tagList = tags.stream()
					.map(tagManager::getLocalizedName)
					.collect(Collectors.joining(", "));
		}
		
		setUserObject(String.format(format, tagList));
		
	}
}
