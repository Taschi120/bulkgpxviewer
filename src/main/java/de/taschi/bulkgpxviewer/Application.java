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

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindow;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class Application {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(Application.class);
	private static Application INSTANCE;
	
	private MainWindow mainWindow;
	private Injector injector;
	
	private Application() {  
		injector = Guice.createInjector(new CoreGuiceModule());
		log.info("Guice dependency injector created");    
	}
	
	private void start() {
		log.info("Application startup"); //$NON-NLS-1$
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.error("Error while setting system look and feel", e); //$NON-NLS-1$
		}

		mainWindow = new MainWindow();
		mainWindow.setVisible(true);
	}
	
	private static Application getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Application();
		}
		return INSTANCE;
	}
	
	public static MainWindow getMainWindow() {
		return getInstance().getMainWindowIntl();
	}

	private MainWindow getMainWindowIntl() {
		return mainWindow;
	}
	
	public static Injector getInjector() {
		return getInstance().getInjectorIntl();
	}

	private Injector getInjectorIntl() {
		return injector;
	}

	public static void main(String[] args) {
		getInstance().start();
	}
}
