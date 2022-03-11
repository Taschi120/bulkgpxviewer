package de.taschi.bulkgpxviewer.ui.sidepanel

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.*
import java.util.stream.Collectors
import javax.swing.tree.DefaultMutableTreeNode

class TagNode(override val gpxFileTreeNode: GpxFileTreeNode) : DefaultMutableTreeNode(), GpxFileRelatedNode {
    @Inject
    private val tagManager: TagManager? = null

    init {
        Application.getInjector().injectMembers(this)
        update()
    }

    override fun update() {
        val tags = tagManager!!.getTagsForGpxFile(gpxFileTreeNode.gpxFile!!)
        val format = "Tags: %s"
        var tagList: String? = "none"

        if (tags.isNotEmpty()) {
            tagList = tags
                .map { tag -> tagManager.getLocalizedName(tag) }
                .joinToString(", ")
        }
        setUserObject(String.format(format, tagList))
    }

    companion object {
        private const val serialVersionUID = -2085495184375672455L
    }
}