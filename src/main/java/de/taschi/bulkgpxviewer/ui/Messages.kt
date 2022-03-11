package de.taschi.bulkgpxviewer.ui

import java.util.*

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
 */   object Messages {
    private const val BUNDLE_NAME = "de.taschi.bulkgpxviewer.ui.messages" //$NON-NLS-1$
    private val RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME)
    fun getString(key: String): String {
        return try {
            RESOURCE_BUNDLE.getString(key)
        } catch (e: MissingResourceException) {
            "!$key!"
        }
    }
}