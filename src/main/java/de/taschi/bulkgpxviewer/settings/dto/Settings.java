package de.taschi.bulkgpxviewer.settings.dto;

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

import java.util.List;

public class Settings {

	private String lastUsedDirectory;
	private List<SettingsColor> routeColors;
	private MainWindowSettings mainWindowSettings;
	private UnitSystem unitSystem;
	private SettingsColor unselectedRouteColor;
	private SettingsColor selectedRouteColor;

	public Settings() {
	}

	public String getLastUsedDirectory() {
		return this.lastUsedDirectory;
	}

	public List<SettingsColor> getRouteColors() {
		return this.routeColors;
	}

	public MainWindowSettings getMainWindowSettings() {
		return this.mainWindowSettings;
	}

	public UnitSystem getUnitSystem() {
		return this.unitSystem;
	}

	public SettingsColor getUnselectedRouteColor() {
		return this.unselectedRouteColor;
	}

	public SettingsColor getSelectedRouteColor() {
		return this.selectedRouteColor;
	}

	public void setLastUsedDirectory(String lastUsedDirectory) {
		this.lastUsedDirectory = lastUsedDirectory;
	}

	public void setRouteColors(List<SettingsColor> routeColors) {
		this.routeColors = routeColors;
	}

	public void setMainWindowSettings(MainWindowSettings mainWindowSettings) {
		this.mainWindowSettings = mainWindowSettings;
	}

	public void setUnitSystem(UnitSystem unitSystem) {
		this.unitSystem = unitSystem;
	}

	public void setUnselectedRouteColor(SettingsColor unselectedRouteColor) {
		this.unselectedRouteColor = unselectedRouteColor;
	}

	public void setSelectedRouteColor(SettingsColor selectedRouteColor) {
		this.selectedRouteColor = selectedRouteColor;
	}

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof Settings)) return false;
		final Settings other = (Settings) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$lastUsedDirectory = this.getLastUsedDirectory();
		final Object other$lastUsedDirectory = other.getLastUsedDirectory();
		if (this$lastUsedDirectory == null ? other$lastUsedDirectory != null : !this$lastUsedDirectory.equals(other$lastUsedDirectory))
			return false;
		final Object this$routeColors = this.getRouteColors();
		final Object other$routeColors = other.getRouteColors();
		if (this$routeColors == null ? other$routeColors != null : !this$routeColors.equals(other$routeColors))
			return false;
		final Object this$mainWindowSettings = this.getMainWindowSettings();
		final Object other$mainWindowSettings = other.getMainWindowSettings();
		if (this$mainWindowSettings == null ? other$mainWindowSettings != null : !this$mainWindowSettings.equals(other$mainWindowSettings))
			return false;
		final Object this$unitSystem = this.getUnitSystem();
		final Object other$unitSystem = other.getUnitSystem();
		if (this$unitSystem == null ? other$unitSystem != null : !this$unitSystem.equals(other$unitSystem))
			return false;
		final Object this$unselectedRouteColor = this.getUnselectedRouteColor();
		final Object other$unselectedRouteColor = other.getUnselectedRouteColor();
		if (this$unselectedRouteColor == null ? other$unselectedRouteColor != null : !this$unselectedRouteColor.equals(other$unselectedRouteColor))
			return false;
		final Object this$selectedRouteColor = this.getSelectedRouteColor();
		final Object other$selectedRouteColor = other.getSelectedRouteColor();
		if (this$selectedRouteColor == null ? other$selectedRouteColor != null : !this$selectedRouteColor.equals(other$selectedRouteColor))
			return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof Settings;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $lastUsedDirectory = this.getLastUsedDirectory();
		result = result * PRIME + ($lastUsedDirectory == null ? 43 : $lastUsedDirectory.hashCode());
		final Object $routeColors = this.getRouteColors();
		result = result * PRIME + ($routeColors == null ? 43 : $routeColors.hashCode());
		final Object $mainWindowSettings = this.getMainWindowSettings();
		result = result * PRIME + ($mainWindowSettings == null ? 43 : $mainWindowSettings.hashCode());
		final Object $unitSystem = this.getUnitSystem();
		result = result * PRIME + ($unitSystem == null ? 43 : $unitSystem.hashCode());
		final Object $unselectedRouteColor = this.getUnselectedRouteColor();
		result = result * PRIME + ($unselectedRouteColor == null ? 43 : $unselectedRouteColor.hashCode());
		final Object $selectedRouteColor = this.getSelectedRouteColor();
		result = result * PRIME + ($selectedRouteColor == null ? 43 : $selectedRouteColor.hashCode());
		return result;
	}

	public String toString() {
		return "Settings(lastUsedDirectory=" + this.getLastUsedDirectory() + ", routeColors=" + this.getRouteColors() + ", mainWindowSettings=" + this.getMainWindowSettings() + ", unitSystem=" + this.getUnitSystem() + ", unselectedRouteColor=" + this.getUnselectedRouteColor() + ", selectedRouteColor=" + this.getSelectedRouteColor() + ")";
	}
}
