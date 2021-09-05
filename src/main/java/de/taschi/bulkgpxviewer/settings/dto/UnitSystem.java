package de.taschi.bulkgpxviewer.settings.dto;

import de.taschi.bulkgpxviewer.ui.Messages;

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

public enum UnitSystem {
	METRIC(Messages.getString("UnitSystem.Metric"), Messages.getString("UnitSystem.Unit_kilometers"), Messages.getString("UnitSystem.Unit_kph")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	IMPERIAL(Messages.getString("UnitSystem.Imperial"), Messages.getString("UnitSystem.Unit_miles"), Messages.getString("UnitSystem.Unit_mph")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	private final String displayName, defaultDisplayUnit, defaultSpeedUnit;
	
	private UnitSystem(String displayName, String defaultDistanceUnit, String defaultSpeedUnit) {
		this.displayName = displayName;
		this.defaultDisplayUnit = defaultDistanceUnit;
		this.defaultSpeedUnit = defaultSpeedUnit;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDefaultDisplayUnit() {
		return defaultDisplayUnit;
	}

	public String getDefaultSpeedUnit() {
		return defaultSpeedUnit;
	}
	
}
