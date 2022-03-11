package de.taschi.bulkgpxviewer.ui.windowbuilder
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
 */import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.ui.Messages
import io.jenetics.jpx.Track
import lombok.extern.log4j.Log4j2
import java.awt.Container
import java.lang.Exception
import java.util.ArrayList
import java.util.function.Function
import java.util.function.Predicate
import javax.swing.border.BevelBorder

@Log4j2
class EditingPanel : JPanel() {
    private var track: GpxFile? = null
    private var selectionHandler: MapSelectionHandler? = null
    private var selection: List<WayPoint> = emptyList<WayPoint>()
    private val firstSelectedPointLabel: JLabel
    private val firstSelectionTransportBack: JButton
    private val firstSelectionTransportForward: JButton
    private val secondSelectionTransportBack: JButton
    private val secondSelectionDeselect: JButton
    private val cropAfter: JButton
    private val secondSelectionTransportForward: JButton
    private val cropBetween: JButton
    private val cropBefore: JButton
    private val firstSelectionDeselect: JButton
    private val secondSelectedPointLabel: JLabel
    private val save: JButton
    private val cancel: JButton

    /**
     * Create the panel.
     */
    init {
        setBorder(SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null))
        setLayout(BorderLayout(0, 0))
        val panel = JPanel()
        add(panel, BorderLayout.SOUTH)
        panel.setLayout(GridLayout(0, 1, 0, 0))
        val panel_2 = JPanel()
        val flowLayout: FlowLayout = panel_2.getLayout() as FlowLayout
        flowLayout.setAlignment(FlowLayout.RIGHT)
        panel.add(panel_2)
        cancel = JButton(Messages.getString("EditingPanel.Cancel")) //$NON-NLS-1$
        cancel.addActionListener(ActionListener { evt: ActionEvent -> onCancel(evt) })
        panel_2.add(cancel)
        save = JButton(Messages.getString("EditingPanel.Save")) //$NON-NLS-1$
        save.addActionListener(ActionListener { evt: ActionEvent -> onSave(evt) })
        panel_2.add(save)
        val panel_3 = JPanel()
        panel_3.setBorder(EmptyBorder(3, 3, 3, 3))
        add(panel_3, BorderLayout.CENTER)
        val gbl_panel_3 = GridBagLayout()
        gbl_panel_3.columnWidths = intArrayOf(0, 0, 0, 0, 0, 0)
        gbl_panel_3.rowHeights = intArrayOf(0, 0, 0)
        gbl_panel_3.columnWeights = doubleArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0)
        gbl_panel_3.rowWeights = doubleArrayOf(0.0, 0.0, 0.0)
        panel_3.setLayout(gbl_panel_3)
        val lblNewLabel = JLabel(Messages.getString("EditingPanel.PointA")) //$NON-NLS-1$
        val gbc_lblNewLabel = GridBagConstraints()
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST
        gbc_lblNewLabel.insets = Insets(0, 0, 5, 5)
        gbc_lblNewLabel.gridx = 0
        gbc_lblNewLabel.gridy = 0
        panel_3.add(lblNewLabel, gbc_lblNewLabel)
        firstSelectedPointLabel = JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST") //$NON-NLS-1$
        val gbc_firstSelectedPointLabel = GridBagConstraints()
        gbc_firstSelectedPointLabel.anchor = GridBagConstraints.WEST
        gbc_firstSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL
        gbc_firstSelectedPointLabel.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectedPointLabel.gridx = 1
        gbc_firstSelectedPointLabel.gridy = 0
        panel_3.add(firstSelectedPointLabel, gbc_firstSelectedPointLabel)
        firstSelectionTransportBack = JButton(IconHandler.loadIcon("arrow-left-s-line")) //$NON-NLS-1$
        val gbc_firstSelectionTransportBack = GridBagConstraints()
        gbc_firstSelectionTransportBack.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectionTransportBack.gridx = 2
        gbc_firstSelectionTransportBack.gridy = 0
        firstSelectionTransportBack.addActionListener(ActionListener { e: ActionEvent? ->
            onFirstSelectionTransportBack(
                e
            )
        })
        panel_3.add(firstSelectionTransportBack, gbc_firstSelectionTransportBack)
        firstSelectionDeselect = JButton(IconHandler.loadIcon("close-circle-line")) //$NON-NLS-1$
        val gbc_firstSelectionDeselect = GridBagConstraints()
        gbc_firstSelectionDeselect.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectionDeselect.gridx = 3
        gbc_firstSelectionDeselect.gridy = 0
        firstSelectionDeselect.addActionListener(ActionListener { e: ActionEvent? -> onFirstSelectionDeselect(e) })
        panel_3.add(firstSelectionDeselect, gbc_firstSelectionDeselect)
        firstSelectionTransportForward = JButton(IconHandler.loadIcon("arrow-right-s-line")) //$NON-NLS-1$
        val gbc_firstSelectionTransportForward = GridBagConstraints()
        gbc_firstSelectionTransportForward.insets = Insets(0, 0, 5, 5)
        gbc_firstSelectionTransportForward.gridx = 4
        gbc_firstSelectionTransportForward.gridy = 0
        firstSelectionTransportForward.addActionListener(ActionListener { e: ActionEvent? ->
            onFirstSelectionTransportForward(
                e
            )
        })
        panel_3.add(firstSelectionTransportForward, gbc_firstSelectionTransportForward)
        cropBefore = JButton(Messages.getString("EditingPanel.CropBefore")) //$NON-NLS-1$
        cropBefore.setIcon(IconHandler.loadIcon("scissors-cut-line"))
        val gbc_cropBefore = GridBagConstraints()
        gbc_cropBefore.fill = GridBagConstraints.HORIZONTAL
        gbc_cropBefore.insets = Insets(0, 0, 5, 5)
        gbc_cropBefore.gridx = 5
        gbc_cropBefore.gridy = 0
        cropBefore.addActionListener(ActionListener { e: ActionEvent? -> onCropBefore(e) })
        panel_3.add(cropBefore, gbc_cropBefore)
        cropBetween = JButton(Messages.getString("EditingPanel.CropBetween")) //$NON-NLS-1$
        cropBetween.setIcon(IconHandler.loadIcon("scissors-cut-line"))
        val gbc_cropBetween = GridBagConstraints()
        gbc_cropBetween.fill = GridBagConstraints.HORIZONTAL
        gbc_cropBetween.insets = Insets(0, 0, 5, 5)
        gbc_cropBetween.gridx = 5
        gbc_cropBetween.gridy = 1
        cropBetween.addActionListener(ActionListener { e: ActionEvent? -> onCropBetween(e) })
        panel_3.add(cropBetween, gbc_cropBetween)
        val label2 = JLabel(Messages.getString("EditingPanel.PointB")) //$NON-NLS-1$
        val gbc_label2 = GridBagConstraints()
        gbc_label2.anchor = GridBagConstraints.WEST
        gbc_label2.insets = Insets(0, 0, 5, 5)
        gbc_label2.gridx = 0
        gbc_label2.gridy = 2
        panel_3.add(label2, gbc_label2)
        secondSelectedPointLabel =
            JLabel("12\u00B034'56\"N, 12\u00B035'56\"W @ Jan 1st, 2021, 17:55 CEST") //$NON-NLS-1$
        val gbc_secondSelectedPointLabel = GridBagConstraints()
        gbc_secondSelectedPointLabel.fill = GridBagConstraints.HORIZONTAL
        gbc_secondSelectedPointLabel.anchor = GridBagConstraints.WEST
        gbc_secondSelectedPointLabel.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectedPointLabel.gridx = 1
        gbc_secondSelectedPointLabel.gridy = 2
        panel_3.add(secondSelectedPointLabel, gbc_secondSelectedPointLabel)
        secondSelectionTransportBack = JButton(IconHandler.loadIcon("arrow-left-s-line")) //$NON-NLS-1$
        val gbc_secondSelectionTransportBack = GridBagConstraints()
        gbc_secondSelectionTransportBack.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectionTransportBack.gridx = 2
        gbc_secondSelectionTransportBack.gridy = 2
        secondSelectionTransportBack.addActionListener(ActionListener { actionevent1: ActionEvent? ->
            onSecondSelectionTransportBack(
                actionevent1
            )
        })
        panel_3.add(secondSelectionTransportBack, gbc_secondSelectionTransportBack)
        secondSelectionDeselect = JButton(IconHandler.loadIcon("close-circle-line")) //$NON-NLS-1$
        val gbc_secondSelectionDeselect = GridBagConstraints()
        gbc_secondSelectionDeselect.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectionDeselect.gridx = 3
        gbc_secondSelectionDeselect.gridy = 2
        secondSelectionDeselect.addActionListener(ActionListener { e: ActionEvent? -> onSecondSelectionDeselect(e) })
        panel_3.add(secondSelectionDeselect, gbc_secondSelectionDeselect)
        secondSelectionTransportForward = JButton(IconHandler.loadIcon("arrow-right-s-line")) //$NON-NLS-1$
        val gbc_secondSelectionTransportForward = GridBagConstraints()
        gbc_secondSelectionTransportForward.insets = Insets(0, 0, 5, 5)
        gbc_secondSelectionTransportForward.gridx = 4
        gbc_secondSelectionTransportForward.gridy = 2
        secondSelectionTransportForward.addActionListener(ActionListener { e: ActionEvent? ->
            onSecondSelectionTransportForward(
                e
            )
        })
        panel_3.add(secondSelectionTransportForward, gbc_secondSelectionTransportForward)
        cropAfter = JButton(Messages.getString("EditingPanel.CropAfter")) //$NON-NLS-1$
        cropAfter.setIcon(IconHandler.loadIcon("scissors-cut-line"))
        val gbc_cropAfter = GridBagConstraints()
        gbc_cropAfter.fill = GridBagConstraints.HORIZONTAL
        gbc_cropAfter.insets = Insets(0, 0, 5, 5)
        gbc_cropAfter.gridx = 5
        gbc_cropAfter.gridy = 2
        cropAfter.addActionListener(ActionListener { e: ActionEvent? -> onCropAfter(e) })
        panel_3.add(cropAfter, gbc_cropAfter)
    }

    fun setSelection(selection: Set<WayPoint?>?) {
        this.selection = ArrayList<WayPoint>(selection)
        disableAndEnableButtons()
        updateLabels()
    }

    fun setTrack(track: GpxFile?) {
        this.track = track
    }

    fun setSelectionHandler(handler: MapSelectionHandler?) {
        selectionHandler = handler
    }

    /**
     * update the component labels
     */
    private fun updateLabels() {
        val size = selection.size
        if (size == 0) {
            firstSelectedPointLabel.setText(Messages.getString("EditingPanel.Empty")) //$NON-NLS-1$
            secondSelectedPointLabel.setText(Messages.getString("EditingPanel.Empty")) //$NON-NLS-1$
        } else if (size == 1) {
            firstSelectedPointLabel.setText(makeDescriptorString(selection[0]))
            secondSelectedPointLabel.setText(Messages.getString("EditingPanel.Empty")) //$NON-NLS-1$
        } else if (size == 2) {
            selection.sort(java.util.Comparator<WayPoint> { o1: WayPoint?, o2: WayPoint? ->
                track.indexOfWayPoint(o2).orElseThrow().compareTo(track.indexOfWayPoint(o1).orElseThrow())
            })
            firstSelectedPointLabel.setText(makeDescriptorString(selection[0]))
            secondSelectedPointLabel.setText(makeDescriptorString(selection[1]))
        }
    }

    private fun makeDescriptorString(wayPoint: WayPoint): String {
        val lat = doubleToDegMinSec(
            wayPoint.getLatitude().toDegrees(),
            Messages.getString("EditingPanel.South"),
            Messages.getString("EditingPanel.North")
        ) //$NON-NLS-1$ //$NON-NLS-2$
        val lon = doubleToDegMinSec(
            wayPoint.getLongitude().toDegrees(),
            Messages.getString("EditingPanel.West"),
            Messages.getString("EditingPanel.East")
        ) //$NON-NLS-1$ //$NON-NLS-2$
        val time: String = wayPoint.getTime().map<String>(Function<ZonedDateTime, String> { temporal: ZonedDateTime? ->
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(temporal)
        }).orElse(
            Messages.getString("EditingPanel.UnknownDate")
        ) //$NON-NLS-1$
        return String.format(Messages.getString("EditingPanel.WaypointLabelFormat"), lat, lon, time) //$NON-NLS-1$
    }

    private fun doubleToDegMinSec(lat: Double, negativeSide: String?, positiveSide: String?): String {
        var lat = lat
        val side = if (lat < 0) negativeSide else positiveSide
        lat = Math.abs(lat)
        val deg = Math.floor(lat).toInt()
        lat -= deg.toDouble()
        lat *= 60.0
        val min = Math.floor(lat).toInt()
        lat -= min.toDouble()
        lat *= 60.0
        val sec = Math.floor(lat).toInt()
        return String.format(Messages.getString("EditingPanel.DegMinSecFormat"), deg, min, sec, side) //$NON-NLS-1$
    }

    /**
     * Update enabled / disabled state of all buttons
     */
    private fun disableAndEnableButtons() {
        val firstTransportEnabled = !selection.isEmpty()
        val secondTransportEnabled = selection.size > 1
        firstSelectionTransportBack.setEnabled(firstTransportEnabled)
        firstSelectionTransportForward.setEnabled(firstTransportEnabled)
        firstSelectionDeselect.setEnabled(firstTransportEnabled)
        secondSelectionTransportBack.setEnabled(secondTransportEnabled)
        secondSelectionTransportForward.setEnabled(secondTransportEnabled)
        secondSelectionDeselect.setEnabled(secondTransportEnabled)
        cropBefore.setEnabled(firstTransportEnabled)
        cropAfter.setEnabled(firstTransportEnabled)
        cropBetween.setEnabled(secondTransportEnabled)
    }

    private fun onSave(evt: ActionEvent) {
        try {
            track.save()
            JOptionPane.showMessageDialog(
                Application.Companion.getMainWindow().getFrame(),
                Messages.getString("EditingPanel.SaveSuccessful")
            ) //$NON-NLS-1$
        } catch (e: Exception) {
            Container.log.error("Error while saving GPX file", e) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                Application.Companion.getMainWindow().getFrame(),
                String.format(Messages.getString("EditingPanel.ErrorWhileSaving"), e.localizedMessage)
            ) //$NON-NLS-1$
        }
    }

    private fun onCancel(evt: ActionEvent) {
        var reallyCancel = false
        reallyCancel = if (track.isChanged()) {
            val result: Int = JOptionPane.showConfirmDialog(
                Application.Companion.getMainWindow().getFrame(),
                Messages.getString("EditingPanel.ConfirmCancelWithoutSaving"),
                Messages.getString("EditingPanel.ReallyQuit"),
                JOptionPane.YES_NO_OPTION
            ) //$NON-NLS-1$ //$NON-NLS-2$
            result == JOptionPane.YES_OPTION
        } else {
            true
        }
        if (reallyCancel) {
            Application.Companion.getMainWindow()!!.exitEditingMode()
        }
    }

    private fun onFirstSelectionTransportBack(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val item: WayPoint = selection[0]
            val index: WaypointIndex = track.indexOfWayPoint(item).get()
            if (index.getWaypointId() > 0) {
                val newSelection: WayPoint = track.getGpx()
                    .getTracks().get(index.getTrackId())
                    .getSegments().get(index.getSegmentId())
                    .getPoints().get(index.getWaypointId() - 1)
                selection.set(0, newSelection)
                selectionHandler.setSelection(selection)
            }
        }
    }

    private fun onFirstSelectionTransportForward(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val item: WayPoint = selection[0]
            val index: WaypointIndex = track.indexOfWayPoint(item).get()
            val segment: TrackSegment = track.getGpx()
                .getTracks().get(index.getTrackId())
                .getSegments().get(index.getSegmentId())
            val points: List<WayPoint> = segment.getPoints()
            if (index.getWaypointId() < points.size - 1) {
                val newSelection: WayPoint = points[index.getWaypointId() + 1]
                selection.set(0, newSelection)
                selectionHandler.setSelection(selection)
            }
        }
    }

    private fun onFirstSelectionDeselect(e: ActionEvent) {
        if (!selection.isEmpty()) {
            selection.removeAt(0)
            selectionHandler.setSelection(selection)
        }
    }

    private fun onSecondSelectionTransportBack(actionevent1: ActionEvent) {
        if (selection.size >= 2) {
            val item: WayPoint = selection[1]
            val index: WaypointIndex = track.indexOfWayPoint(item).get()
            if (index.getWaypointId() > 0) {
                val newSelection: WayPoint = track.getGpx()
                    .getTracks().get(index.getTrackId())
                    .getSegments().get(index.getSegmentId())
                    .getPoints().get(index.getWaypointId() - 1)
                selection.set(1, newSelection)
                selectionHandler.setSelection(selection)
            }
        }
    }

    private fun onSecondSelectionDeselect(e: ActionEvent) {
        if (selection.size >= 2) {
            selection.removeAt(1)
            selectionHandler.setSelection(selection)
        }
    }

    private fun onSecondSelectionTransportForward(e: ActionEvent) {
        if (selection.size >= 2) {
            val item: WayPoint = selection[1]
            val index: WaypointIndex = track.indexOfWayPoint(item).get()
            val segment: TrackSegment = track.getGpx()
                .getTracks().get(index.getTrackId())
                .getSegments().get(index.getSegmentId())
            val points: List<WayPoint> = segment.getPoints()
            if (index.getWaypointId() < points.size - 1) {
                val newSelection: WayPoint = points[index.getWaypointId() + 1]
                selection.set(1, newSelection)
                selectionHandler.setSelection(selection)
            }
        }
    }

    private fun onCropBefore(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val endpoint: WayPoint = selection[0]
            val index: WaypointIndex = track.indexOfWayPoint(endpoint).get()
            val toBeDeleted: List<WayPoint> = track.getGpx().getTracks().get(index.getTrackId())
                .getSegments().get(index.getSegmentId()).getPoints().subList(0, index.getWaypointId())
            val gpx1: GPX = track.getGpx().toBuilder()
                .trackFilter()
                .map(Function { track: Track ->
                    track.toBuilder()
                        .map(Function { segment: TrackSegment ->
                            segment.toBuilder()
                                .filter(Predicate<WayPoint> { it: WayPoint -> !toBeDeleted.contains(it) })
                                .build()
                        })
                        .build()
                })
                .build()
                .build()
            track.setGpx(gpx1)
        }
    }

    private fun onCropAfter(e: ActionEvent) {
        if (!selection.isEmpty()) {
            val endPoint: WayPoint = selection[selection.size - 1]
            val index: WaypointIndex = track.indexOfWayPoint(endPoint).get()
            val points: List<WayPoint> = track.getGpx().getTracks().get(index.getTrackId())
                .getSegments().get(index.getSegmentId()).getPoints()
            val toBeDeleted: List<WayPoint> = points.subList(index.getWaypointId() + 1, points.size)
            val gpx1: GPX = track.getGpx().toBuilder()
                .trackFilter()
                .map(Function { track: Track ->
                    track.toBuilder()
                        .map(Function { segment: TrackSegment ->
                            segment.toBuilder()
                                .filter(Predicate<WayPoint> { it: WayPoint -> !toBeDeleted.contains(it) })
                                .build()
                        })
                        .build()
                })
                .build()
                .build()
            track.setGpx(gpx1)
        }
    }

    private fun onCropBetween(e: ActionEvent) {
        if (selection.size == 2) {
            val point1: WayPoint = selection[0]
            val point2: WayPoint = selection[1]
            val idx1: WaypointIndex = track.indexOfWayPoint(point1).get()
            val idx2: WaypointIndex = track.indexOfWayPoint(point2).get()
            if (idx1.isOnSameSegment(idx2)) {
                var i1: Int = idx1.getWaypointId()
                var i2: Int = idx2.getWaypointId()
                if (i1 > i2) {
                    val o = i2
                    i2 = i1
                    i1 = o
                }
                val points: List<WayPoint> = track.getGpx().getTracks().get(idx1.getTrackId())
                    .getSegments().get(idx1.getSegmentId()).getPoints()
                val toBeDeleted: List<WayPoint> = points.subList(i1 + 1, i2)
                val gpx1: GPX = track.getGpx().toBuilder()
                    .trackFilter()
                    .map(Function { track: Track ->
                        track.toBuilder()
                            .map(Function { segment: TrackSegment ->
                                segment.toBuilder()
                                    .filter(Predicate<WayPoint> { it: WayPoint -> !toBeDeleted.contains(it) })
                                    .build()
                            })
                            .build()
                    })
                    .build()
                    .build()
                track.setGpx(gpx1)
            } else {
                Container.log.error("Selections not on same track: {} and {}", idx1, idx2) //$NON-NLS-1$
            }
        }
    }

    companion object {
        private const val serialVersionUID = 6937011900054488316L
    }
}