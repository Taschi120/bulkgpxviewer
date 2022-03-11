package de.taschi.bulkgpxviewer

import com.google.inject.AbstractModule
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.files.TagManager
import de.taschi.bulkgpxviewer.math.*
import de.taschi.bulkgpxviewer.settings.SettingsManager

class CoreGuiceModule constructor() : AbstractModule() {
    override fun configure() {
        // de.taschi.bulkgpxviewer.files
        bind(LoadedFileManager::class.java).toInstance(LoadedFileManager())
        bind(TagManager::class.java).toInstance(TagManager())

        // de.taschi.bulkgpxviewer.math
        bind(DurationCalculator::class.java).toInstance(DurationCalculator())
        bind(DurationFormatter::class.java).toInstance(DurationFormatter())
        bind(HaversineCalculator::class.java).toInstance(HaversineCalculator())
        bind(RouteLengthCalculator::class.java).toInstance(RouteLengthCalculator())
        bind(SpeedCalculator::class.java).toInstance(SpeedCalculator())
        bind(TrackStatisticsManager::class.java).toInstance(TrackStatisticsManager())
        bind(UnitConverter::class.java).toInstance(UnitConverter())

        // de.taschi.bulkgpxviewer.settings
        bind(SettingsManager::class.java).toInstance(SettingsManager())
    }
}