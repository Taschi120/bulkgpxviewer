package de.taschi.bulkgpxviewer;


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

import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindow;


public class Application {
	
	private static final Logger LOG = LogManager.getLogger(Application.class);
	
	private static MainWindow mainWindow;
	
	public static MainWindow getMainWindow() {
		return mainWindow;
	}
	
	public static void main(String[] args) {
		LOG.info("Application startup"); //$NON-NLS-1$
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.error("Error while setting system look and feel", e); //$NON-NLS-1$
		}
		mainWindow = new MainWindow();
		mainWindow.setVisible(true);
	}
}
