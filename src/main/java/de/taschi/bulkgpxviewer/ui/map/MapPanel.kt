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
 */import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import io.jenetics.jpx.Track
import lombok.Getter
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Stream

/**
 * A JPanel which wraps the [JXMapKit] instance
 */
class MapPanel : JPanel() {
    private val mapKit: JXMapKit
    val routesPainter: TracksPainter
    val selectionPainter: SelectionPainter
    private val compositePainter: CompositePainter

    /**
     * Set the selected (highlighted) file. Use "null" as the parameter to unselect.
     * @param selectedFile
     */
    @Getter
    var selectedFile: GpxFile? = null
        set(selectedFile) {
            field = selectedFile
            repaint()
        }

    @Inject
    private val loadedFileManager: LoadedFileManager? = null
    val selectionHandler: MapSelectionHandler

    init {
        Application.Companion.getInjector().injectMembers(this)
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
        mapKit.getMainMap().setOverlayPainter(compositePainter)
        loadedFileManager.addChangeListener(LoadedFileChangeListener { mapKit.repaint() })
        autoSetZoomAndLocation()
        selectionHandler = MapSelectionHandler(mapKit.getMainMap())
        selectionHandler.addSelectionChangeListener { selection: Set<WayPoint> ->
            selectionPainter.setSelectionFromWayPoints(
                selection
            )
        }
        getMapKit().getMainMap().addMouseListener(selectionHandler)
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
            val bb = GpsBoundingBox()
            tracks.stream().map<GPX>(Function<GpxFile, GPX> { obj: GpxFile -> obj.getGpx() })
                .flatMap<Track>(Function<GPX, Stream<out Track>> { obj: GPX -> obj.tracks() })
                .flatMap<TrackSegment>(Function<Track, Stream<out TrackSegment>> { obj: Track -> obj.segments() })
                .flatMap<WayPoint>(Function<TrackSegment, Stream<out WayPoint>> { obj: TrackSegment -> obj.points() })
                .map<GeoPosition>(Function<WayPoint, GeoPosition> { `in`: WayPoint? ->
                    GpxToJxMapper.Companion.getInstance().waypointToGeoPosition(`in`)
                })
                .forEach(Consumer<GeoPosition> { p: GeoPosition? -> bb.clamp(p) })
            mapKit.setAddressLocation(GeoPosition(bb.getCenterLat(), bb.getCenterLong()))
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
        private const val serialVersionUID = -1865773680018087837L
    }
}