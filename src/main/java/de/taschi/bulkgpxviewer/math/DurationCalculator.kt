package de.taschi.bulkgpxviewer.math

import com.google.inject.Singleton
import de.taschi.bulkgpxviewer.math.DurationCalculator
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.*

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

@Singleton
class DurationCalculator constructor() {
    /**
     * Get the recorded duration of all tracks of a [GPX] file, disregarding any gaps between
     * segments or tracks.
     *
     * I. e. if the file contains two segment of 30 minutes duration each, and there is a 30 minute gap
     * between the two segments, the result of this function will be 60 minutes.
     *
     * @param gpx The Gpx file
     * @return An optional containing the duration if all track segments had timestamp info, or an
     * empty optional if at least one segment had no time stamp info.
     */
    fun getRecordedDurationForGpxFile(gpx: GPX): Optional<Duration> {
        var result: Duration = Duration.ZERO
        for (track: Track in gpx.tracks) {
            val duration = getRecordedDurationForTrack(track)
            if (duration.isEmpty) {
                return duration
            } else {
                result = result.plus(duration.get())
            }
        }
        return Optional.of(result)
    }

    /**
     * Get the recorded duration of a [Track], disregarding any gaps between segments.
     *
     * I. e. if the Track contains two segment of 30 minutes duration each, and there is a 30 minute gap
     * between the two segments, the result of this function will be 60 minutes.
     *
     * @param track The track
     * @return An optional containing the duration if all track segments had timestamp info, or an
     * empty optional if at least one segment had no time stamp info.
     */
    fun getRecordedDurationForTrack(track: Track): Optional<Duration> {
        var result: Duration = Duration.ZERO
        for (segment: TrackSegment in track.getSegments()) {
            val duration = getSegmentDuration(segment)
            if (duration.isEmpty) {
                return duration
            } else {
                result = result.plus(duration.get())
            }
        }
        return Optional.of(result)
    }

    /**
     * Get the duration of a [TrackSegment].
     * @param segment The segment
     * @return An optional containing the duration if it could be calculated,
     * or an empty optional if no timestamps were found in the segment.
     */
    private fun getSegmentDuration(segment: TrackSegment): Optional<Duration> {
        val waypoints: List<WayPoint> = segment.points
        if (waypoints.size < 2) {
            log.debug("Time calculation impossible: List has fewer than 2 waypoints") //$NON-NLS-1$
            return Optional.of(Duration.ZERO)
        }
        val start: Instant? = waypoints[0].instant.orElse(null)
        val end: Instant? = waypoints[waypoints.size - 1].instant.orElse(null)

        if (start == null || end == null) {
            log.debug("Time calculation impossible: GPX does not contain timestamps") //$NON-NLS-1$
            return Optional.empty()
        }
        val duration: Duration = Duration.between(start, end)
        return Optional.of(duration)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DurationCalculator::class.java)
    }
}