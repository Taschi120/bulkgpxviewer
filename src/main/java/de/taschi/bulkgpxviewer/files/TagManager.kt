package de.taschi.bulkgpxviewer.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.jr.ob.JSON;
import com.google.inject.Singleton;

import lombok.Cleanup;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Singleton
public class TagManager {
	/**
	 * A list of "canonical" tags built into the software
	 */
	private List<Tag> builtInTags = new ArrayList<>();
	
	/**
	 * A list of all user-defined tags the application has "seen"
	 * since start-up
	 */
	private List<Tag> knownUserTags = new ArrayList<>();
	
	public TagManager() {
		try {
			@Cleanup
			var inputStream = getClass().getClassLoader().getResourceAsStream("tags.json");
			builtInTags = JSON.std.listOfFrom(Tag.class, inputStream);
		} catch (IOException e) {
			log.error("Error while loading built-in tags", e);
		}
	}
	
	/**
	 * Gets a list of all "known" tags, for purposes of e. g. autocompletion
	 * @return
	 */
	public List<Tag> getKnownTags() {
		return Collections.unmodifiableList(builtInTags);
	}
	
	/**
	 * Notify the tag manager that a tag exists
	 * @param tag
	 */
	@Synchronized
	public void notifyForNewTag(Tag tag) {
		if(!builtInTags.contains(tag) && !knownUserTags.contains(tag)) {
			log.info("Remembering user tag: " + tag);
			knownUserTags.add(tag);
		}
	}
	
	@Synchronized
	public Tag fromString(String name) {
		for (Tag t : builtInTags) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		
		for (Tag t2: knownUserTags) {
			if (t2.getName().equals(name)) {
				return t2;
			}
		}
		
		var t3 = new Tag();
		t3.setName(name);
		t3.setUserDefined(true);
		notifyForNewTag(t3);
		return t3;
	}
	
	public String getLocalizedName(Tag tag) {
		// TODO implement me!
		return tag.getName();
	}
	
	
	private Optional<Element> getMyExtensionNode(GpxFile gpxFile) {
		return gpxFile.getGpx().getExtensions()
				.map(it -> findFirstChildTagWithName(it, "extensions"))
				.map(it -> findFirstChildTagWithName(it, "bulkgpxviewer"));
				
	}
	
	private Element findFirstChildTagWithName(Document doc, String name) {
		log.debug("Searching for node named {}", name);
		for (int i = 0; i < doc.getChildNodes().getLength(); i++) {

			var node = doc.getChildNodes().item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				var element = (Element) node;
				log.debug("Element is named {}", element.getTagName());
				if (element.getTagName().equals(name)) {
					return element;
				}
			}
		}
		
		return null;
	}
	
	private Element findFirstChildTagWithName(Node rnode, String name) {
		log.debug("Searching for node named {}", name);
		for (int i = 0; i < rnode.getChildNodes().getLength(); i++) {

			var node = rnode.getChildNodes().item(i);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				var element = (Element) node;
				log.debug("Element is named {}", element.getTagName());
				if (element.getTagName().equals(name)) {
					return element;
				}
			}
		}
		
		return null;
	}
	
	public List<Tag> getTagsForGpxFile(GpxFile gpxFile) {
		var extNode = getMyExtensionNode(gpxFile);
		if (extNode.isPresent()) {
			var children = extNode.get().getChildNodes();
			
			var result = new ArrayList<Tag>();
					
			for (int i = 0; i < children.getLength(); i++) {
				var node = children.item(i);
				
				if (node instanceof Element) {
					var element = (Element) node;
					
					if(element.getTagName().equals("tag")) {
						var tag = fromString(element.getTextContent());
						result.add(tag);
						
						log.info("Found tag {}", tag);
					}
				}
			}	
			
			log.info("Found {} tags", result.size());
			return result;
		} else {
			return Collections.unmodifiableList(Collections.emptyList());
		}
	}

	
}
