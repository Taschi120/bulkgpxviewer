package de.taschi.bulkgpxviewer;

import com.google.inject.AbstractModule;

import de.taschi.bulkgpxviewer.files.LoadedFileManager;
import de.taschi.bulkgpxviewer.files.TagManager;
import de.taschi.bulkgpxviewer.math.DurationCalculator;
import de.taschi.bulkgpxviewer.math.DurationFormatter;
import de.taschi.bulkgpxviewer.math.HaversineCalculator;
import de.taschi.bulkgpxviewer.math.RouteLengthCalculator;
import de.taschi.bulkgpxviewer.math.SpeedCalculator;
import de.taschi.bulkgpxviewer.math.TrackStatisticsManager;
import de.taschi.bulkgpxviewer.math.UnitConverter;
import de.taschi.bulkgpxviewer.settings.SettingsManager;

public class CoreGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		// de.taschi.bulkgpxviewer.files
		bind(LoadedFileManager.class).toInstance(new LoadedFileManager());
		bind(TagManager.class).toInstance(new TagManager());

		// de.taschi.bulkgpxviewer.math
		bind(DurationCalculator.class).toInstance(new DurationCalculator());
		bind(DurationFormatter.class).toInstance(new DurationFormatter());
		bind(HaversineCalculator.class).toInstance(new HaversineCalculator());
		bind(RouteLengthCalculator.class).toInstance(new RouteLengthCalculator());
		bind(SpeedCalculator.class).toInstance(new SpeedCalculator());
		bind(TrackStatisticsManager.class).toInstance(new TrackStatisticsManager());
		bind(UnitConverter.class).toInstance(new UnitConverter());
		
		// de.taschi.bulkgpxviewer.settings
		bind(SettingsManager.class).toInstance(new SettingsManager());
		
	}

}
