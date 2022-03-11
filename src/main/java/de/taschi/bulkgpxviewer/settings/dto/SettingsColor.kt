package de.taschi.bulkgpxviewer.settings.dto

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
class SettingsColor {
    constructor() : super() {
        r = 0
        g = 0
        b = 0
    }

    constructor(r: Int, g: Int, b: Int) : super() {
        this.r = r
        this.g = g
        this.b = b
    }

    var r: Int
    var g: Int
    var b: Int
}