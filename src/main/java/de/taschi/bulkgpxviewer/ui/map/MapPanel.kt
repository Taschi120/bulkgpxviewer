package de.taschi.bulkgpxviewer.ui.map
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

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.files.LoadedFileChangeListener
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.geo.GpsBoundingBox
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper
import io.jenetics.jpx.WayPoint
import org.apache.logging.log4j.LogManager
import org.jxmapviewer.JXMapKit
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.TileFactoryInfo
import java.awt.BorderLayout
import javax.swing.JPanel

/**
 * A JPanel which wraps the [JXMapKit] instance
 */
class MapPanel : JPanel() {
    @Inject
    private lateinit var loadedFileManager: LoadedFileManager

    private val mapKit: JXMapKit
    val routesPainter: TracksPainter
    val selectionPainter: SelectionPainter
    private val compositePainter: CompositePainter

    /**
     * Set the selected (highlighted) file. Use "null" as the parameter to unselect.
     * @param selectedFile
     */
    var selectedFile: GpxFile? = null
        set(selectedFile) {
            field = selectedFile
            repaint()
        }


    val selectionHandler: MapSelectionHandler

    init {
        Application.getInjector().injectMembers(this)
        setLayout(BorderLayout())
        mapKit = JXMapKit()
        this.add(mapKit, BorderLayout.CENTER)

        // Create a TileFactoryInfo for OpenStreetMap
        val info: TileFactoryInfo = OSMTileFactoryInfo()
        val tileFactory = DefaultTileFactory(info)
        mapKit.setTileFactory(tileFactory)

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8)
        routesPainter = TracksPainter(this)
        selectionPainter = SelectionPainter()
        compositePainter = CompositePainter()
        compositePainter.addPainter(routesPainter)
        compositePainter.addPainter(selectionPainter)
        mapKit.mainMap.overlayPainter = compositePainter
        loadedFileManager.addChangeListener(object : LoadedFileChangeListener {
            override fun onLoadedFileChange() {
                mapKit.repaint()
            }
        })
        autoSetZoomAndLocation()
        selectionHandler = MapSelectionHandler(mapKit.getMainMap())
        selectionHandler.addSelectionChangeListener(object: WaypointSelectionChangeListener {
            override fun selectionChanged(selection: Set<WayPoint>?) {
                selectionPainter.setSelectionFromWayPoints(selection ?: emptySet())
            }
        })
        getMapKit().mainMap.addMouseListener(selectionHandler)
    }

    /**
     * Moves the location to the center point of the area covered by the currently loaded tracks.
     *
     * TODO Better logic, including for setting the zoom level, is needed here.
     */
    fun autoSetZoomAndLocation() {
        val tracks: List<GpxFile> = loadedFileManager.getLoadedTracks()
        if (tracks.isEmpty()) {
            // default location
            mapKit.setZoom(8)
            mapKit.setAddressLocation(GeoPosition(50.11, 8.68))
        } else {
            val boundingBox = GpsBoundingBox()
            tracks.stream().map { file -> file.gpx }
                .flatMap { gpx -> gpx.tracks() }
                .flatMap { track -> track.segments() }
                .flatMap { segment -> segment.points() }
                .map { wp -> GpxToJxMapper.waypointToGeoPosition(wp) }
                .forEach { pos -> boundingBox.expandToFit(pos) }
            mapKit.addressLocation = GeoPosition(boundingBox.centerLat, boundingBox.centerLong)
            log.info("Setting automatic location to ${boundingBox.centerLat} | ${boundingBox.centerLong}")
            mapKit.setZoom(8)
        }
    }

    fun forceSettingsRefresh() {
        repaint()
    }

    fun getMapKit(): JXMapKit {
        return mapKit
    }

    companion object {
        private val log = LogManager.getLogger(MapPanel::class.java)
        private const val serialVersionUID = -1865773680018087837L
    }
}