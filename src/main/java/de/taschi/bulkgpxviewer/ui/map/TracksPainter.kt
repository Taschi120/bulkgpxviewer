package de.taschi.bulkgpxviewer.ui.map

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper
import de.taschi.bulkgpxviewer.settings.ColorConverter
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.ui.TrackColorUtil
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.painter.Painter
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.util.*
import java.util.stream.Collectors

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

/**
 * Paints a route. This class is very heavily based on the JXMapViewer sample
 * at https://github.com/msteiger/jxmapviewer2/blob/master/examples/src/sample2_waypoints/RoutePainter.java
 * and slightly adapted.
 *
 * @author S. Hillebrand, based on previous work by Martin Steiger
 */
class TracksPainter(private val parent: MapPanel) : Painter<JXMapViewer> {

    @Inject
    private lateinit var loadedFileManager: LoadedFileManager

    @Inject
    private lateinit var trackColorUtil: TrackColorUtil

    @Inject
    private lateinit var settingsManager: SettingsManager

    private val antiAlias = true
    private var provider: () -> Collection<GpxFile> = allRouteProvider


    init {
        Application.getInjector().injectMembers(this)
    }

    val allRouteProvider: () -> Collection<GpxFile>
        get() = { loadedFileManager.getLoadedTracks() }

    fun makeSingleTrackProvider(track: GpxFile): () -> Collection<GpxFile> {
        return { listOf(track) }
    }

    override fun paint(g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        var g = g
        g = g.create() as Graphics2D

        // convert from viewport to world bitmap
        val rect = map.viewportBounds
        g.translate(-rect.x, -rect.y)
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        for (track in provider()) {
            if (track === parent.selectedFile) continue  // selected file needs to be drawn last!
            drawGpxFileWithOutline(track, g, map, w, h)
        }
        val selectedFile = parent.selectedFile
        if (selectedFile != null) {
            drawGpxFileWithOutline(selectedFile, g, map, w, h)
        }
        g.dispose()
    }

    private fun drawGpxFileWithOutline(track: GpxFile, g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        // do the drawing
        g.color = Color.BLACK
        g.stroke = BasicStroke(4f)
        drawGpxFileSinglePass(g, map, track)

        val color = if (parent.selectedFile == null) {
            trackColorUtil.getColorForTrack(track)
        } else if (parent.selectedFile === track) { // pointer comparison should be ok here
            ColorConverter.convert(settingsManager.settings!!.selectedRouteColor!!)
        } else {
            ColorConverter.convert(settingsManager.settings!!.unselectedRouteColor!!)
        }

        // do the drawing again
        g.color = color
        g.stroke = BasicStroke(2f)
        drawGpxFileSinglePass(g, map, track)
    }

    private fun drawGpxFileSinglePass(g: Graphics2D, map: JXMapViewer, gpxViewerTrack: GpxFile?) {
        for (track in gpxViewerTrack!!.gpx.tracks) {
            drawTrack(g, map, track)
        }
    }

    private fun drawTrack(g: Graphics2D, map: JXMapViewer, track: Track) {
        for (segment in track.segments) {
            drawSegment(g, map, segment)
        }
    }

    private fun drawSegment(g: Graphics2D, map: JXMapViewer, segment: TrackSegment) {
        var lastX = 0
        var lastY = 0
        var first = true
        val wayPoints = segment.points

        // FIXME this is a horrible performance drain, refactor ASAP
        val geoPositions = wayPoints.stream()
            .map { wp -> GpxToJxMapper.waypointToGeoPosition(wp) }
            .collect(Collectors.toList())
        for (gp in geoPositions) {
            // convert geo-coordinate to world bitmap pixel
            val pt = map.tileFactory.geoToPixel(gp, map.zoom)
            if (first) {
                first = false
            } else {
                g.drawLine(lastX, lastY, pt.x.toInt(), pt.y.toInt())
            }
            lastX = pt.x.toInt()
            lastY = pt.y.toInt()
        }
    }

    /**
     * Changes the track provider. Remember that repaint() needs to be called afterwards on the
     * JXMapViewer or JXMapKit instance.
     * @param provider
     */
    fun setProvider(provider: () -> Collection<GpxFile>) {
        this.provider = provider
    }
}