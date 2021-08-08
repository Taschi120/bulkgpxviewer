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
import java.util.List;
import java.util.stream.Collectors;

import de.taschi.bulkgpxviewer.settings.dto.SettingsColor;

public class ColorConverter {
	
	public static Color convert(SettingsColor in) {
		return new Color(in.getR(), in.getG(), in.getB());
	}
	
	public static SettingsColor convert(Color in) {
		return new SettingsColor(in.getRed(), in.getGreen(), in.getBlue());
	}
	
	public static List<Color> convertToAwt(List<SettingsColor> in) {
		return in.stream().map(ColorConverter::convert).collect(Collectors.toList());
	}

	public static List<SettingsColor> convertToSettings(List<Color> in) {
		return in.stream().map(ColorConverter::convert).collect(Collectors.toList());
	}
}
