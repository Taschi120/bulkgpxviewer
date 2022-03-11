package de.taschi.bulkgpxviewer.settings;

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

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSON.Feature;
import com.google.inject.Singleton;

import de.taschi.bulkgpxviewer.settings.dto.MainWindowSettings;
import de.taschi.bulkgpxviewer.settings.dto.Settings;
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor;
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;

@Singleton
public class SettingsManager {

	private static Logger LOG = LogManager.getLogger(SettingsManager.class);

	private static final List<Color> DEFAULT_TRACK_COLORS = Arrays.asList(
			Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.GRAY, 
			Color.WHITE, Color.PINK);

	private Settings settings;

	/** Objects which want to be notified about settings changes */
	private List<SettingsUpdateListener> updateListeners = new ArrayList<>();

	public SettingsManager() {
		LOG.info("Loading settings"); //$NON-NLS-1$
		loadOrInitSettings(getSettingsFile());
	}

	public Settings getSettings() {
		return settings;
	}

	public void addSettingsUpdateListener(SettingsUpdateListener r) {
		if (!updateListeners.contains(r)) {
			updateListeners.add(r);
		}
	}

	public boolean removeSettingsUpdateListener(SettingsUpdateListener r) {
		return updateListeners.remove(r);
	}

	public void fireNotifications() {
		SwingUtilities.invokeLater(() -> {
			for (SettingsUpdateListener r: updateListeners) {
				r.onSettingsUpdated();
			}
		});
	}

	public void saveSettings() {
		try {
			FileUtils.forceMkdirParent(getSettingsFile().toFile());
			JSON.std.write(settings, getSettingsFile().toFile());
		} catch (IOException e) {
			handleException(e);
		}
	}

	private void loadOrInitSettings(Path settingsFile) {
		if(Files.exists(settingsFile)) {
			loadSettings(settingsFile);
			migrateIfNecessary();
			saveSettings();
		} else {
			makeNewSettings();
			saveSettings();
		}
	}

	private void migrateIfNecessary() {
		migrateToV0_2_0();
		migrateToV0_3_0();
	}

	private void migrateToV0_2_0() {
		if (getSettings().getMainWindowSettings() == null) {
			LOG.info("Updating settings to v0.2.0"); //$NON-NLS-1$
			MainWindowSettings s = new MainWindowSettings();
			getSettings().setMainWindowSettings(s);
		}
	}

	private void migrateToV0_3_0() {
		if (getSettings().getUnitSystem() == null) {
			LOG.info("Updating settings to v0.3.0"); //$NON-NLS-1$
			getSettings().setUnitSystem(UnitSystem.METRIC);
		}
		if (getSettings().getSelectedRouteColor() == null) {
			getSettings().setSelectedRouteColor(new SettingsColor(255, 0, 0));
		}
		if (getSettings().getUnselectedRouteColor() == null) {
			getSettings().setUnselectedRouteColor(new SettingsColor(128, 128, 128));
		}
	}

	private void makeNewSettings() {
		settings = new Settings();

		List<SettingsColor> colors = ColorConverter.convertToSettings(DEFAULT_TRACK_COLORS);
		settings.setRouteColors(colors);
		migrateIfNecessary();
	}

	private void loadSettings(Path settingsFile) {
		try {
			String jsonDoc = FileUtils.readFileToString(settingsFile.toFile(), Charset.forName("UTF-8")); //$NON-NLS-1$
			settings = JSON.std
					.with(Feature.PRETTY_PRINT_OUTPUT).
					beanFrom(Settings.class, jsonDoc);
		} catch (IOException e) {
			handleException(e);
		}
	}

	private void handleException(IOException e) {
		LOG.error("Error while trying to load settings:", e); //$NON-NLS-1$
	}

	private Path getSettingsFile() {
		String userDir = FileUtils.getUserDirectoryPath();
		return Paths.get(userDir, ".bulkgpxviewer", "settings.json"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
