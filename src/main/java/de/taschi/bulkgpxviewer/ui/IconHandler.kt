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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IconHandler {
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
