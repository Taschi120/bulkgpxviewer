package de.taschi.bulkgpxviewer.ui

import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.ui.graphs.DistanceOverTimePanel
import de.taschi.bulkgpxviewer.ui.graphs.GradientOverDistancePanel
import de.taschi.bulkgpxviewer.ui.graphs.HeightProfilePanel
import de.taschi.bulkgpxviewer.ui.graphs.SpeedOverTimePanel
import io.jenetics.jpx.GPX
import org.apache.logging.log4j.LogManager
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JTabbedPane
import kotlin.io.path.Path

/**
 * Helper class for testing [GraphPanel]. Pass a GPX file as the first command line argument
 * to use this. (This is not a unit test!)
 */
object GraphPanelTestWindow {
    private val log = LogManager.getLogger(
        GraphPanelTestWindow::class.java
    )

    @JvmStatic
    fun main(args: Array<String>) {
        // test purposes only!
        val frame = JFrame()
        frame.layout = BorderLayout()
        val tabber = JTabbedPane()
        frame.add(tabber, BorderLayout.CENTER)
        val speedOverTimePanel = SpeedOverTimePanel()
        tabber.add("Speed over time", speedOverTimePanel)
        val distanceOverTimePanel = DistanceOverTimePanel()
        tabber.add("Distance over time", distanceOverTimePanel)
        val heightProfilePanel = HeightProfilePanel()
        tabber.add("Height profile", heightProfilePanel)
        val gradientPanel = GradientOverDistancePanel()
        tabber.add("Gradients", gradientPanel)
        frame.setSize(600, 400)
        frame.isVisible = true
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        try {
            val gpx = GPX.read(args[0])
            val track = GpxFile(null, gpx)
            val segment = track.getGpx().tracks[0].segments[0]
            speedOverTimePanel.setGpxTrackSegment(segment)
            distanceOverTimePanel.setGpxTrackSegment(segment)
            heightProfilePanel.setGpxTrackSegment(segment)
            gradientPanel.setGpxTrackSegment(segment)
        } catch (e: Exception) {
            log.error("blah", e)
        }
    }
}