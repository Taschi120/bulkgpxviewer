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
