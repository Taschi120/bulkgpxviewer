package de.taschi.bulkgpsviewer.settings.dto;

import java.util.List;

public class Settings {

	private String lastUsedDirectory;
	
	private List<SettingsColor> routeColors;

	public String getLastUsedDirectory() {
		return lastUsedDirectory;
	}

	public void setLastUsedDirectory(String lastUsedDirectory) {
		this.lastUsedDirectory = lastUsedDirectory;
	}

	public List<SettingsColor> getRouteColors() {
		return routeColors;
	}

	public void setRouteColors(List<SettingsColor> routeColors) {
		this.routeColors = routeColors;
	}
	
	
}
