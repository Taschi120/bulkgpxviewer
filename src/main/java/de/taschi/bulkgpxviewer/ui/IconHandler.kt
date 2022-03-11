package de.taschi.bulkgpxviewer.ui

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.imageio.ImageIO
import javax.swing.ImageIcon

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
 */   object IconHandler {
    private val log: Logger = LogManager.getLogger(IconHandler::class.java)
    fun loadIcon(name: String): ImageIcon? {
        try {
            val result: ImageIcon =
                ImageIcon(ImageIO.read(ClassLoader.getSystemResource("icons/" + name + ".png"))) //$NON-NLS-1$ //$NON-NLS-2$
            return result
        } catch (e: Exception) {
            log.error("Error while loading icon " + name, e) //$NON-NLS-1$
            return null
        }
    }
}