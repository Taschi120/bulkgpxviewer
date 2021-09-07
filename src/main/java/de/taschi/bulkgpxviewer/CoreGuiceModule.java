package de.taschi.bulkgpxviewer;

import com.google.inject.AbstractModule;

import de.taschi.bulkgpxviewer.math.DurationCalculator;
import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindow;

public class CoreGuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MainWindow.class)
			.toInstance(new MainWindow());
		
		bind(DurationCalculator.class)
			.toInstance(new DurationCalculator());
		
	}

}
