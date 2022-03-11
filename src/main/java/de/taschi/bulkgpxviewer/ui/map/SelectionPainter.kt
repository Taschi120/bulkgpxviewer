package de.taschi.bulkgpxviewer.ui.map

import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper
import de.taschi.bulkgpxviewer.geo.WaypointIndex
import io.jenetics.jpx.WayPoint
import org.apache.logging.log4j.LogManager
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.painter.Painter
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.Waypoint
import java.awt.Graphics2D
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.util.Optional
import java.util.stream.Collectors
import javax.imageio.ImageIO
import kotlin.math.roundToInt

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
 */ /**
 * Highlights a set of points on the map
 */
class SelectionPainter : Painter<JXMapViewer> {
    private var markerA: Image
    private var markerB: Image
    private var markerNeutral: Image
    private var imageSize = 0
    private var halfImageSize = 0
    var isEnabled = false
    private val antiAlias = true
    private var track: GpxFile? = null
    private var selection = emptyList<WayPoint>()
    private var selectionAsGeoPositions = emptyList<GeoPosition>()

    init {
        markerNeutral = loadImageResource("ui/marker16.png")
        markerA = loadImageResource("ui/marker_a16.png")
        markerB = loadImageResource("ui/marker_b16.png")

        // Assuming both markers have the same size.
        imageSize = markerA.getWidth(null)
        halfImageSize = imageSize / 2
    }

    override fun paint(g: Graphics2D, map: JXMapViewer, w: Int, h: Int) {
        var g = g
        if (track == null || !isEnabled) return
        g = g.create() as Graphics2D

        // convert from viewport to world bitmap
        val rect = map.viewportBounds
        g.translate(-rect.x, -rect.y)
        if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        if (selection.size == 1) {
            val position = selectionAsGeoPositions[0]
            val point = map.tileFactory.geoToPixel(position, map.zoom)
            // center points
            val x = Math.round(point.x).toInt()
            val y = Math.round(point.y).toInt()
            paintMarker(g, map, x, y, markerNeutral)
        } else if (selection.size == 2) {
            var position = selectionAsGeoPositions[0]
            var point = map.tileFactory.geoToPixel(position, map.zoom)
            var x = point.x.roundToInt()
            var y = point.y.roundToInt()
            paintMarker(g, map, x, y, markerA)
            position = selectionAsGeoPositions[1]
            point = map.tileFactory.geoToPixel(position, map.zoom)
            x = Math.round(point.x).toInt()
            y = Math.round(point.y).toInt()
            paintMarker(g, map, x, y, markerB)
        }
        g.dispose()
    }

    private fun paintMarker(g: Graphics2D, map: JXMapViewer, x: Int, y: Int, marker: Image?) {
        g.drawImage(
            marker,
            x - halfImageSize, y - halfImageSize, x + halfImageSize, y + halfImageSize,
            0, 0, imageSize, imageSize,
            map
        )
    }

    fun setTrack(track: GpxFile?) {
        this.track = track
    }

    fun setSelectionFromWayPoints(selection: Set<WayPoint>) {
        log.info("Updating selection, {} items now selected", selection.size) //$NON-NLS-1$
        val selectionAsList = selection.toMutableList()

        val getIndex: (WayPoint) -> WaypointIndex = { wp: WayPoint ->
            Optional.ofNullable(track)
                .flatMap { it.indexOfWayPoint(wp) }
                .orElseGet { WaypointIndex(null, 0, 0, 0) }
        }

        selectionAsList.sortBy(getIndex)

        this.selection = selectionAsList
        selectionAsGeoPositions = this.selection.stream()
            .map { wp -> GpxToJxMapper.waypointToGeoPosition(wp) }
            .collect(Collectors.toList())
    }

    private fun loadImageResource(name: String): Image {
        try {
            return ImageIO.read(ClassLoader.getSystemResource(name))
        } catch (e: Exception) {
            log.error("Error loading resource $name", e)
            return BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB).apply {
                val g = graphics
                g.fillRect(0, 0, 16, 16)
            }
        }
    }

    companion object {
        private val log = LogManager.getLogger(
            SelectionPainter::class.java
        )
    }
}