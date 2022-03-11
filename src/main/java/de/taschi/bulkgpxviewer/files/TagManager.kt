package de.taschi.bulkgpxviewer.files

import com.fasterxml.jackson.jr.ob.JSON
import com.google.inject.Singleton
import lombok.Cleanup
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.util.*
import java.util.function.Function

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
            @Cleanup val inputStream: InputStream = javaClass.getClassLoader().getResourceAsStream("tags.json")
            builtInTags = JSON.std.listOfFrom(Tag::class.java, inputStream)
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
            return Collections.unmodifiableList<Tag>(builtInTags)
        }

    /**
     * Notify the tag manager that a tag exists
     * @param tag
     */
    @Synchronized
    fun notifyForNewTag(tag: Tag) {
        if (!builtInTags.contains(tag) && !knownUserTags.contains(tag)) {
            log.info("Remembering user tag: " + tag)
            knownUserTags.add(tag)
        }
    }

    @Synchronized
    fun fromString(name: String): Tag {
        for (t: Tag in builtInTags) {
            if ((t.getName() == name)) {
                return t
            }
        }
        for (t2: Tag in knownUserTags) {
            if ((t2.getName() == name)) {
                return t2
            }
        }
        val t3: Tag = Tag()
        t3.setName(name)
        t3.setUserDefined(true)
        notifyForNewTag(t3)
        return t3
    }

    fun getLocalizedName(tag: Tag?): String? {
        // TODO implement me!
        return tag.getName()
    }

    private fun getMyExtensionNode(gpxFile: GpxFile): Optional<Element?> {
        return gpxFile.getGpx().getExtensions()
            .map(Function({ it: Document -> findFirstChildTagWithName(it, "extensions") }))
            .map(Function({ it: Element? -> findFirstChildTagWithName(it, "bulkgpxviewer") }))
    }

    private fun findFirstChildTagWithName(doc: Document, name: String): Element? {
        log.debug("Searching for node named {}", name)
        for (i in 0 until doc.getChildNodes().getLength()) {
            val node: Node = doc.getChildNodes().item(i)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                log.debug("Element is named {}", element.getTagName())
                if ((element.getTagName() == name)) {
                    return element
                }
            }
        }
        return null
    }

    private fun findFirstChildTagWithName(rnode: Node?, name: String): Element? {
        log.debug("Searching for node named {}", name)
        for (i in 0 until rnode!!.getChildNodes().getLength()) {
            val node: Node = rnode.getChildNodes().item(i)
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                val element: Element = node as Element
                log.debug("Element is named {}", element.getTagName())
                if ((element.getTagName() == name)) {
                    return element
                }
            }
        }
        return null
    }

    fun getTagsForGpxFile(gpxFile: GpxFile): List<Tag> {
        val extNode: Optional<Element?> = getMyExtensionNode(gpxFile)
        if (extNode.isPresent()) {
            val children: NodeList = extNode.get().getChildNodes()
            val result: ArrayList<Tag> = ArrayList()
            for (i in 0 until children.getLength()) {
                val node: Node = children.item(i)
                if (node is Element) {
                    val element: Element = node
                    if ((element.getTagName() == "tag")) {
                        val tag: Tag = fromString(element.getTextContent())
                        result.add(tag)
                        log.info("Found tag {}", tag)
                    }
                }
            }
            log.info("Found {} tags", result.size)
            return result
        } else {
            return Collections.unmodifiableList<Tag>(emptyList<Tag>())
        }
    }

    companion object {
        private val log: Logger = LogManager.getLogger(TagManager::class.java)
    }
}