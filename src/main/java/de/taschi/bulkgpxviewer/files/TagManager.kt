package de.taschi.bulkgpxviewer.files

import com.fasterxml.jackson.jr.ob.JSON
import com.google.inject.Singleton
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.IOException
import java.util.*

@Singleton
class TagManager constructor() {
    /**
     * A list of "canonical" tags built into the software
     */
    private var builtInTags: List<Tag> = ArrayList()

    /**
     * A list of all user-defined tags the application has "seen"
     * since start-up
     */
    private val knownUserTags: MutableList<Tag> = ArrayList()

    init {

        try {
            javaClass.classLoader.getResourceAsStream("tags.json").use { inputStream ->
                builtInTags = JSON.std.listOfFrom(Tag::class.java, inputStream)
            }
        } catch (e: IOException) {
            log.error("Error while loading built-in tags", e)
        }
    }

    /**
     * Gets a list of all "known" tags, for purposes of e. g. autocompletion
     * @return
     */
    val knownTags: List<Tag>
        get() {
            return Collections.unmodifiableList(builtInTags)
        }

    /**
     * Notify the tag manager that a tag exists
     * @param tag
     */
    @Synchronized
    fun notifyForNewTag(tag: Tag) {
        if (!builtInTags.contains(tag) && !knownUserTags.contains(tag)) {
            log.info("Remembering user tag: $tag")
            knownUserTags.add(tag)
        }
    }

    @Synchronized
    fun fromString(name: String): Tag {
        for (t: Tag in builtInTags) {
            if ((t.name == name)) {
                return t
            }
        }
        for (t2: Tag in knownUserTags) {
            if ((t2.name == name)) {
                return t2
            }
        }
        val t3: Tag = Tag()
        t3.name = name
        t3.isUserDefined = true
        notifyForNewTag(t3)
        return t3
    }

    fun getLocalizedName(tag: Tag): String? {
        // TODO implement me!
        return tag.name
    }

    private fun getMyExtensionNode(gpxFile: GpxFile): Optional<Element> {
        return gpxFile.gpx.extensions
            .map { findFirstChildTagWithName(it, "extensions") }
                // the null assertion should be fine here - if the value is "null", Optional#map won't be called
            .map { findFirstChildTagWithName(it!!, "bulkgpxviewer") }


    }

    private fun findFirstChildTagWithName(doc: Document, name: String): Element? {
        log.debug("Searching for node named {}", name)
        for (i in 0 until doc.childNodes.length) {
            val node: Node = doc.childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                log.debug("Element is named {}", element.tagName)
                if ((element.tagName == name)) {
                    return element
                }
            }
        }
        return null
    }

    private fun findFirstChildTagWithName(searchRootNode: Node, name: String): Element? {
        log.debug("Searching for node named {}", name)
        for (i in 0 until searchRootNode.childNodes.length) {
            val node: Node = searchRootNode.childNodes.item(i)
            if (node.nodeType == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                log.debug("Element is named {}", element.getTagName())
                if ((element.tagName == name)) {
                    return element
                }
            }
        }
        return null
    }

    fun getTagsForGpxFile(gpxFile: GpxFile): List<Tag> {
        val extNode = getMyExtensionNode(gpxFile)
        if (extNode.isPresent) {
            val children = extNode.get().childNodes
            val result: ArrayList<Tag> = ArrayList()
            for (i in 0 until children.length) {
                val node: Node = children.item(i)
                if (node is Element) {
                    val element: Element = node
                    if ((element.tagName == "tag")) {
                        val tag: Tag = fromString(element.textContent)
                        result.add(tag)
                        log.info("Found tag {}", tag)
                    }
                }
            }
            log.info("Found {} tags", result.size)
            return result
        } else {
            return Collections.unmodifiableList(emptyList<Tag>())
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TagManager::class.java)
    }
}