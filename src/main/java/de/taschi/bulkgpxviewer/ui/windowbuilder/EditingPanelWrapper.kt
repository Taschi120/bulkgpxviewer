package de.taschi.bulkgpxviewer.ui.windowbuilder

import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.ui.map.MapSelectionHandler
import io.jenetics.jpx.WayPoint
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

/**
 * A wrapper for [EditingPanel] which shows or hides that panel as necessary.
 */
class EditingPanelWrapper constructor() : JPanel() {
    private val editingPanel: EditingPanel
    private val editingInactivePanel: JPanel

    init {
        editingPanel = EditingPanel()
        editingInactivePanel = JPanel()
        editingInactivePanel.setLayout(BorderLayout())
        val label: JLabel = JLabel("Right-click a track and select 'Edit' to start editing.")
        label.setHorizontalAlignment(SwingConstants.CENTER)
        editingInactivePanel.add(label, BorderLayout.CENTER)
        setLayout(BorderLayout())
        this.add(editingInactivePanel, BorderLayout.CENTER)
    }

    fun setSelection(selection: Set<WayPoint?>?) {
        editingPanel.setSelection(selection)
    }

    fun setTrack(track: GpxFile?) {
        editingPanel.setTrack(track)
    }

    fun setSelectionHandler(selectionHandler: MapSelectionHandler?) {
        editingPanel.setSelectionHandler(selectionHandler)
    }

    fun startEditingMode() {
        remove(editingInactivePanel)
        add(editingPanel, BorderLayout.CENTER)
        revalidate()
        repaint()
    }

    fun exitEditingMode() {
        remove(editingPanel)
        add(editingInactivePanel, BorderLayout.CENTER)
        revalidate()
        repaint()
    }

    companion object {
        private val serialVersionUID: Long = 6165429227505288375L
    }
}