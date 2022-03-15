package de.taschi.bulkgpxviewer.settings

import de.taschi.bulkgpxviewer.settings.dto.SettingsColor
import java.awt.Color

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

object ColorConverter {
    fun convert(input: SettingsColor): Color {
        return Color(input.r, input.g, input.b)
    }

    fun convert(input: Color): SettingsColor {
        return SettingsColor(input.red, input.green, input.blue)
    }

    fun convertToAwt(input: List<SettingsColor>): List<Color> {
        return input.map(this::convert)
    }

    fun convertToSettings(input: List<Color>): List<SettingsColor> {
        return input.map(this::convert)
    }
}