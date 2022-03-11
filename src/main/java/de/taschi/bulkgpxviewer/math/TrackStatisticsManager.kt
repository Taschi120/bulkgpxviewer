package de.taschi.bulkgpxviewer.math

import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.Messages
import io.jenetics.jpx.Length
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Function
import java.util.function.ToDoubleFunction
import java.util.stream.Collectors
import javax.inject.Inject

/**
 * Helper class for calculating and caching detailed statistics
 * for GPX tracks, like distance, speed, altitude etc as data rows (arrays of data per GPX node)
 *
 * FIXME Most of the lambdas in this class are utterly broken after Kotlin conversion, and need some thorough
 * refactoring.
 */
class TrackStatisticsManager constructor() {
    /** Caches  */
    private val distanceDiffs: HashMap<TrackSegment, List<Double>> = HashMap()
    private val totalDistances: HashMap<TrackSegment, List<Double>> = HashMap()
    private val totalTimes: HashMap<TrackSegment, List<Duration>> = HashMap()
    private val timeDiffs: HashMap<TrackSegment, List<Duration>> = HashMap()
    private val speeds: HashMap<TrackSegment, List<Double>> = HashMap()
    private val elevations: HashMap<TrackSegment, List<Double>> = HashMap()
    private val gradients: HashMap<TrackSegment, List<Double>> = HashMap()

    @Inject
    private lateinit var haversineCalculator: HaversineCalculator

    @Inject
    private lateinit var unitConverter: UnitConverter

    fun getTotalDistance(segment: TrackSegment?): Double {
        return getDistanceDifferences(segment).stream().collect(
            Collectors.summingDouble(
                ToDoubleFunction({ it: Double? -> (it)!! })
            )
        )
    }

    // TODO: This could definitely be made more elegant
    fun getDistanceDifferences(segment: TrackSegment?): List<Double> {
        return resolveCache(
            distanceDiffs,
            segment,
            object : Calculator<Double> {
                override fun calculate(t: TrackSegment?): List<Double> {
                    return calculateDistanceDifferences(segment).map { it!! }
                }
            }
        )
    }

    private fun calculateDistanceDifferences(segment: TrackSegment?): List<Double?> {
        if (segment == null) {
            return emptyList<Double>()
        }
        val waypoints: List<WayPoint> = segment.points
        val result: MutableList<Double?> = ArrayList(waypoints.size)
        for (i in waypoints.indices) {
            if (i == 0) {
                result.add(0.0)
            } else {
                val prev: WayPoint = waypoints[i - 1]
                val now: WayPoint = waypoints[i]
                result.add(haversineCalculator.getDistance(prev, now))
            }
        }
        return result
    }

    /**
     * Maps the list of waypoints in a segment to a list of distance from start to waypoint X
     * @param segment
     * @return
     */
    fun getTotalDistances(segment: TrackSegment?): List<Double> {
        return resolveCache(
            totalDistances,
            segment,
            object : Calculator<Double> {
                override fun calculate(t: TrackSegment?): List<Double> {
                    return calculateTotalDistances(segment)
                }
            }
        )
    }

    private fun calculateTotalDistances(segment: TrackSegment?): List<Double> {

        if (segment == null) {
            return emptyList()
        }

        val waypoints: List<WayPoint> = segment.points
        if (waypoints.isEmpty()) {
            return emptyList()
        }
        val result: MutableList<Double> = ArrayList(waypoints.size)
        var distanceSoFar = 0.0
        var prevWaypoint: WayPoint = waypoints[0]
        for (waypoint: WayPoint in waypoints) {
            distanceSoFar += haversineCalculator.getDistance(prevWaypoint, waypoint)
            result.add(distanceSoFar)
            prevWaypoint = waypoint
        }
        return result
    }

    /**
     * Maps the list of waypoints in a segment to a list of time elapsed from start until this waypoint.
     * The first value will be 0.
     * @param segment
     * @return
     */
    fun getTotalTimes(segment: TrackSegment?): List<Duration> {
        return resolveCache(
            totalTimes,
            segment,
            object : Calculator<Duration> {
                override fun calculate(t: TrackSegment?): List<Duration> {
                    return calculateTotalTimes(segment)
                }
            }
        )
    }

    private fun calculateTotalTimes(segment: TrackSegment?): List<Duration> {
        if (segment == null) {
            return emptyList<Duration>()
        }
        val waypoints: List<WayPoint> = segment.getPoints()
        if (waypoints.isEmpty()) {
            return emptyList<Duration>()
        }
        val times: List<ZonedDateTime?> = waypoints.stream()
            .map(Function({ it: WayPoint -> it.getTime() }))
            .map(Function({ it: Optional<ZonedDateTime?> -> it.orElse(null) }))
            .collect(Collectors.toList())

        // We need to check if all waypoints actually have a timestamp, since timestamps are an optiona
        // feature of GPX and not all GPX file contain them.
        if (times.contains(null)) {
            // TODO better error handling
            return waypoints.stream().map(Function({ it: WayPoint? -> Duration.ZERO })).collect(Collectors.toList())
        }
        val firstTime: ZonedDateTime? = times.get(0)
        return times.stream()
            .map(Function({ it: ZonedDateTime? -> Duration.between(firstTime, it) }))
            .collect(Collectors.toList())
    }

    /**
     * Maps the list of waypoints in a segment to a list of speed between this waypoint and the previous one.
     * The first value will be 0.
     *
     * (The speed is given in km/h and needs to be converted in the UI layer if necessary.)
     * @param segment
     * @return
     */
    fun getSpeeds(segment: TrackSegment?): List<Double> {
        return resolveCache(speeds, segment, object : Calculator<Double> {
            override fun calculate(t: TrackSegment?): List<Double> {
                return calculateSpeeds(segment)
            }
        })
    }

    private fun calculateSpeeds(segment: TrackSegment?): List<Double> {
        if (segment == null) {
            return emptyList<Double>()
        }
        val distances: List<Double> = getDistanceDifferences(segment)
        val times: List<Duration> = getTimeDifferences(segment)
        if (distances.size != times.size) {
            log.error(
                "Speed calculation impossible: data rows have incompatible sizes ({} and {}])",  //$NON-NLS-1$
                distances.size, times.size
            )
            return emptyList<Double>()
        }
        val result: MutableList<Double> = ArrayList(distances.size)
        for (i in distances.indices) {
            val distance: Double = distances.get(i)
            val time: Duration = times.get(i)
            val millis: Long = time.toMillis()
            if (millis == 0L) {
                result.add(0.0)
            } else {
                val speed: Double = distance / millis * 3600000
                if (speed < STANDING_SPEED_THRESHOLD) {
                    if (i > 0) {
                        result.set(i - 1, 0.0)
                    }
                    result.add(0.0)
                } else {
                    result.add(speed)
                }
            }
        }
        return result
    }

    fun getTimeDifferences(segment: TrackSegment?): List<Duration> {
        return resolveCache(
            timeDiffs,
            segment,
            object : Calculator<Duration> {
                override fun calculate(t: TrackSegment?): List<Duration> {
                    return calculateTimeDifferences(segment)
                }
            }
        )
    }

    private fun calculateTimeDifferences(segment: TrackSegment?): List<Duration> {
        if (segment == null) {
            return emptyList<Duration>()
        }
        val waypoints: List<WayPoint> = segment.getPoints()
        val times: List<ZonedDateTime?> = waypoints.stream()
            .map(Function({ it: WayPoint -> it.getTime() }))
            .map(Function({ it: Optional<ZonedDateTime?> -> it.orElse(null) }))
            .collect(Collectors.toList())
        if (times.contains(null)) {
            // TODO better error handling
            log.error("gpx point without timestamp!") //$NON-NLS-1$
        }
        val result: MutableList<Duration> = ArrayList(waypoints.size)
        for (i in waypoints.indices) {
            if (i == 0) {
                result.add(Duration.ZERO)
            } else {
                val prev: ZonedDateTime? = times.get(i - 1)
                val now: ZonedDateTime? = times.get(i)
                result.add(Duration.between(prev, now))
            }
        }
        return result
    }

    fun getElevations(segment: TrackSegment?): List<Double> {
        return resolveCache(
            elevations,
            segment,
            object : Calculator<Double> {
                override fun calculate(t: TrackSegment?): List<Double> {
                    return calculateElevations(segment!!)
                }
            }
        )
    }

    private fun calculateElevations(segment: TrackSegment): List<Double> {
        val waypoints: List<WayPoint> = segment.getPoints()
        val elevations: List<Double> = waypoints.stream()
            .map(Function({ it: WayPoint -> it.getElevation().orElse(ZERO_LENGTH).to(Length.Unit.METER) }))
            .collect(Collectors.toUnmodifiableList())
        return elevations
    }

    /**
     * Gets a list in which element X represents the gradient (in percent) between
     * waypoint X - 1 and waypoint X.
     *
     * The first element is 0.
     *
     * @param segment
     * @return
     */
    fun getGradients(segment: TrackSegment?): List<Double> {
        return resolveCache(
            gradients,
            segment,
            object : Calculator<Double> {
                override fun calculate(t: TrackSegment?): List<Double> {
                    return calculateGradients(t!!) // TODO the non-null-assertion is a bodge for future refactoring.
                }
            }
        )
    }

    private fun calculateGradients(segment: TrackSegment): List<Double> {
        val distanceDifferences: List<Double> = getDistanceDifferences(segment)
        val elevations: List<Double> = smootheWithoutZeroSnap(getElevations(segment), 50)
        if (distanceDifferences.size != elevations.size) {
            val errorMessage: String = String.format(
                "Can not build dataset for gradients: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
                distanceDifferences.size, elevations.size
            )
            log.error(errorMessage)
            throw RuntimeException(errorMessage)
        }
        val result: ArrayList<Double> = ArrayList(distanceDifferences.size)
        result.add(0.0)
        for (i in 1 until elevations.size) {
            val distance: Double = distanceDifferences.get(i)
            if (distance == 0.0) {
                result.add(0.0)
            } else {
                val first: Double = elevations.get(i - 1)
                val second: Double = elevations.get(i)

                // factor 1000 is necessary because distances are in kilometers,
                // while altitudes are in meters
                val gradient: Double = (second - first) * 1000 / distance
                result.add(gradient)
            }
        }
        return result
    }

    fun getHeightProfileAsXY(segment: TrackSegment?): XYDataset {
        val altitudes: List<Double> = getElevations(segment)
        val distances: List<Double> = getTotalDistances(segment)
        val result: XYSeries =
            XYSeries(Messages.getString("TrackStatisticsManager.HeightProfileDiagramLabel")) //$NON-NLS-1$
        if (altitudes.size != distances.size) {
            val errorMessage: String = String.format(
                "Can not build dataset for speed over time: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
                altitudes.size, distances.size
            )
            log.error(errorMessage)
            throw RuntimeException(errorMessage)
        }
        for (i in altitudes.indices) {
            result.add(distances.get(i), altitudes.get(i))
        }
        return XYSeriesCollection(result)
    }

    fun getDistanceOverTimeAsXY(segment: TrackSegment?): XYDataset {
        val times: List<Double> = getTotalTimes(segment).stream()
            .map(Function({ it: Duration -> it.getSeconds().toDouble() }))
            .collect(Collectors.toList())
        val distances: List<Double> = getTotalDistances(segment)
        return buildXYDataset(
            Messages.getString("TrackStatisticsManager.DistanceOverTimeDiagramLabel"),
            times,
            distances
        ) //$NON-NLS-1$
    }

    /**
     * Returns a JFreeChart data set of speed over time for a given TrackSegment.
     *
     * Times are in minutes, distances are in km or miles depending on UnitSystem parameter.
     *
     * @param segment
     * @return
     */
    fun getSpeedOverTimeAsXY(segment: TrackSegment?, unitSystem: UnitSystem?): XYDataset {
        val times: List<Double> = getTotalTimes(segment).stream()
            .map(Function({ it: Duration -> it.toMillis() / 1000.0 }))
            .map(Function({ it: Double -> it / 60.0 }))
            .collect(Collectors.toUnmodifiableList())
        var speeds: List<Double> = getSpeeds(segment).stream()
            .map(Function({ it: Double -> if (unitSystem == UnitSystem.IMPERIAL) unitConverter.kilometersToMiles(it) else it }))
            .collect(Collectors.toUnmodifiableList())
        speeds = smootheWithZeroSnap(speeds, 5)
        return buildXYDataset(
            Messages.getString("TrackStatisticsManager.SpeedOverTimeDiagramLabel"),
            times,
            speeds
        ) //$NON-NLS-1$
    }

    fun getGradientOverDistanceAsXY(segment: TrackSegment?): XYDataset {
        val gradients: List<Double> = getGradients(segment)
        //gradients = smootheWithoutZeroSnap(gradients, 50);
        val distances: List<Double> = getTotalDistances(segment)
        return buildXYDataset(
            Messages.getString("TrackStatisticsManager.GradientOverDistanceDiagramLabel"),
            distances,
            gradients
        ) //$NON-NLS-1$
    }

    private fun <T> resolveCache(
        cache: HashMap<TrackSegment, List<T>>,
        segment: TrackSegment?,
        calculator: Calculator<T>
    ): List<T> {
        if (segment == null || segment.isEmpty()) {
            return emptyList()
        }
        val cached: List<T>? = cache.get(segment)
        if (cached != null) {
            return cached
        } else {
            val calculated: List<T> = calculator.calculate(segment)
            cache.put(segment, calculated)
            return calculated
        }
    }

    private fun smootheWithoutZeroSnap(input: List<Double>, halfWindowSize: Int): List<Double> {
        val output: MutableList<Double> = ArrayList(input.size)
        for (i in input.indices) {
            output.add(average(input, i - halfWindowSize, i + halfWindowSize))
        }
        return output
    }

    private fun smootheWithZeroSnap(input: List<Double>, halfWindowSize: Int): List<Double> {
        val output: MutableList<Double> = ArrayList(input.size)
        for (i in input.indices) {
            val original: Double = input.get(i)
            if (original < STANDING_SPEED_THRESHOLD) {
                output.add(0.0)
            } else {
                output.add(average(input, i - halfWindowSize, i + halfWindowSize))
            }
        }
        return output
    }

    private fun average(input: List<Double>, min: Int, max: Int): Double {
        var min: Int = min
        var max: Int = max
        min = Math.max(min, 0)
        max = Math.min(max, input.size)
        if (min > max) {
            throw IllegalArgumentException()
        }
        if (min == max) {
            return input.get(min)
        }
        var sum: Double = 0.0
        var num: Int = 0
        for (i in min until max) {
            num++
            sum += input.get(i)
        }
        return sum / num
    }

    // TODO This interface is a bad idea and should probably be deleted outright. Without any way to do comparison
    // between two instances of this interface, it is useless for the purposes of caching anyway.
    private open interface Calculator<T> {
        fun calculate(t: TrackSegment?): List<T>
    }

    companion object {
        private val log = LoggerFactory.getLogger(TrackStatisticsManager::class.java        )

        /**
         * If speed between two points is below this threshold, it will be rounded down to 0.
         * This compensates for GPS imprecisions creating the impression of movement while
         * the GPS receiver is not actually moving.
         */
        private val STANDING_SPEED_THRESHOLD: Double = 2.0
        private val ZERO_LENGTH: Length = Length.of(0.0, Length.Unit.METER)

        private fun buildXYDataset(name: String?, xAxis: List<Double>, yAxis: List<Double>): XYDataset {
            val result: XYSeries = XYSeries(name)
            if (xAxis.size != yAxis.size) {
                val errorMessage: String = String.format(
                    "Can not build dataset: inconsistent data row lengths (%s and %s)",  //$NON-NLS-1$
                    xAxis.size, yAxis.size
                )
                log.error(errorMessage)
                throw RuntimeException(errorMessage)
            }
            for (i in xAxis.indices) {
                result.add(xAxis.get(i), yAxis.get(i))
            }
            return XYSeriesCollection(result)
        }
    }
}