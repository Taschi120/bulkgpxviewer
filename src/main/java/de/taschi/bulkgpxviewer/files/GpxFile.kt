package de.taschi.bulkgpxviewer.files

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.geo.GpxToJxMapper
import de.taschi.bulkgpxviewer.geo.WaypointIndex
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import org.apache.commons.io.FileUtils
import org.jxmapviewer.viewer.GeoPosition
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.time.Instant
import java.time.ZonedDateTime
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

class GpxFile constructor(val fileName: Path, var gpx: GPX) {

    /**
     * Returns a list of all waypoints in this GPX file, without any guarantees as to order.
     * @return
     */
    var allWayPoints: List<WayPoint>? = null
        private set

    /**
     * Returns a list of all GeoPositions in this GPX file. Order will be consistent with getAllWayPoints, as long as
     * the model is not changed in the interim.
     * @return
     */
    var allGeoPositions: List<GeoPosition>? = null
        private set

    @Inject
    private val loadedFileManager: LoadedFileManager? = null
    var isChanged: Boolean = false
        private set

    init {
        updateCachedFields()
    }

    private fun updateCachedFields() {
        val lAllWayPoints = gpx.tracks.stream()
            .flatMap { obj: Track -> obj.segments() }
            .flatMap { obj: TrackSegment -> obj.points() }
            .collect(Collectors.toUnmodifiableList())
        val lAllGeoPositions = lAllWayPoints
            .map { GpxToJxMapper.waypointToGeoPosition(it) }

        allWayPoints = lAllWayPoints
        allGeoPositions = lAllGeoPositions
    }

    val startedAt: Optional<Instant>
        get() {
            for (track: Track in gpx.tracks) {
                for (segment: TrackSegment in track.segments) {
                    for (point: WayPoint in segment.points) {
                        val timestamp: Optional<ZonedDateTime> = point.getTime()
                        if (timestamp.isPresent) {
                            return timestamp.map { obj: ZonedDateTime -> obj.toInstant() }
                        }
                    }
                }
            }
            return Optional.empty()
        }

    @Throws(IOException::class)
    fun save() {
        doBackup()
        log.info("Writing GPX to '{}'", fileName) //$NON-NLS-1$
        GPX.write(gpx, fileName)
        log.info("Written GPX to '{}' successfully", fileName) //$NON-NLS-1$
        isChanged = false
    }

    @Throws(IOException::class)
    private fun doBackup() {
        val `in`: File = fileName.toFile()
        val out: File = File(`in`.getAbsolutePath() + ".bak") //$NON-NLS-1$
        log.info("Creating backup of '{}' at '{}'", `in`, out) //$NON-NLS-1$
        FileUtils.copyFile(`in`, out)
    }

    override fun equals(other: Any?): Boolean {
        if (other is GpxFile) {
            val otherGpx: GPX = other.gpx
            return (gpx == otherGpx)
        }
        return false
    }

    override fun hashCode(): Int {
        return gpx.hashCode()
    }

    /**
     * Find the position of a WayPoint inside the data model
     * @param wayPoint
     * @return Optional<WaypointIndex> if found, empty optional otherwise.
    </WaypointIndex> */
    fun indexOfWayPoint(wayPoint: WayPoint): Optional<WaypointIndex> {
        val tracks: List<Track> = gpx.tracks
        for (trackId in tracks.indices) {
            val segments: List<TrackSegment> = tracks.get(trackId).getSegments()
            for (segmentId in segments.indices) {
                val points: List<WayPoint> = segments.get(segmentId).getPoints()
                for (pointId in points.indices) {
                    if ((points.get(pointId) == wayPoint)) {
                        return Optional.of(
                            WaypointIndex.builder()
                                .gpx(gpx)
                                .trackId(trackId)
                                .segmentId(segmentId)
                                .waypointId(pointId)
                                .build()
                        )
                    }
                }
            }
        }
        return Optional.empty()
    }

    val firstSegment: Optional<TrackSegment>
        get() {
            val tracks: List<Track> = gpx.tracks
            if (tracks.isEmpty()) {
                return Optional.empty()
            }
            val segments: List<TrackSegment> = tracks[0].segments
            if (segments.isEmpty()) {
                return Optional.empty()
            }
            return Optional.of(segments[0])
        }

    companion object {
        private val log = LoggerFactory.getLogger(GpxFile::class.java)
    }
}