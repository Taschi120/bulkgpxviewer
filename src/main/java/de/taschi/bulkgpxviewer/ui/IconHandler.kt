package de.taschi.bulkgpxviewer.ui;

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

import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;

public class IconHandler {
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(IconHandler.class);

	public static ImageIcon loadIcon(String name) {
		try {
			ImageIcon result = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("icons/" + name + ".png"))); //$NON-NLS-1$ //$NON-NLS-2$
			return result;
		} catch (Exception e) {
			log.error("Error while loading icon " + name, e); //$NON-NLS-1$
			return null;
		}
	}
}
