package de.taschi.bulkgpsviewer.settings;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.jr.ob.JSON;

import de.taschi.bulkgpsviewer.settings.dto.Settings;
import de.taschi.bulkgpsviewer.settings.dto.SettingsColor;

public class SettingsManager {
	private static SettingsManager INSTANCE;
	
	public static SettingsManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SettingsManager();
		}
		return INSTANCE;
	}
	
	private static final List<Color> DEFAULT_TRACK_COLORS = Arrays.asList(
			Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.GRAY, 
			Color.WHITE, Color.PINK);
	
	private Settings settings;
	
	private SettingsManager() {
		loadOrInitSettings(getSettingsFile());
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	private void loadOrInitSettings(Path settingsFile) {
		if(Files.exists(settingsFile)) {
			loadSettings(settingsFile);
		} else {
			makeNewSettings();
			saveSettings();
		}
	}

	public void saveSettings() {
		try {
			FileUtils.forceMkdirParent(getSettingsFile().toFile());
			JSON.std.write(settings, getSettingsFile().toFile());
		} catch (IOException e) {
			handleException(e);
		}
	}

	private void makeNewSettings() {
		settings = new Settings();
		
		List<SettingsColor> colors = ColorConverter.convertToSettings(DEFAULT_TRACK_COLORS);
		settings.setRouteColors(colors);
	}

	private void loadSettings(Path settingsFile) {
		try {
			String jsonDoc = FileUtils.readFileToString(settingsFile.toFile(), Charset.forName("UTF-8"));
			settings = JSON.std.beanFrom(Settings.class, jsonDoc);
		} catch (IOException e) {
			handleException(e);
		}
	}

	private void handleException(IOException e) {
		System.err.println("Error while trying to load settings:");
		e.printStackTrace();
	}

	private Path getSettingsFile() {
		String userDir = FileUtils.getUserDirectoryPath();
		return Paths.get(userDir, ".bulkgpxviewer", "settings.json");
	}
}
