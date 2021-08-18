package de.taschi.bulkgpxviewer.geo;

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

import java.time.Duration;

public class DurationFormatter {
	
	private static DurationFormatter INSTANCE = null;
	
	private DurationFormatter() {
		// Singleton, prevent instantiation
	}
	
	public static DurationFormatter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DurationFormatter();
		}
		return INSTANCE;
	}
	
	public String format(Duration duration) {
		long seconds = duration.getSeconds();
		return String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
	}
}
