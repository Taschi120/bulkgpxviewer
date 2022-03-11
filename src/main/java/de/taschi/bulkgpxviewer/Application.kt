package de.taschi.bulkgpxviewer

import com.google.inject.Guice
import com.google.inject.Injector
import de.taschi.bulkgpxviewer.ui.windowbuilder.MainWindow
import org.slf4j.LoggerFactory
import javax.swing.UIManager

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

class Application private constructor() {
    private lateinit var mainWindowIntl: MainWindow
    private val injectorIntl: Injector

    init {
        injectorIntl = Guice.createInjector(CoreGuiceModule())
        log.info("Guice dependency injector created")
    }

    private fun start() {
        log.info("Application startup") //$NON-NLS-1$
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            log.error("Error while setting system look and feel", e) //$NON-NLS-1$
        }
        mainWindowIntl = MainWindow()
        mainWindowIntl.setVisible(true)
    }

    companion object {
        private val log = LoggerFactory.getLogger(Application::class.java)
        private var INSTANCE: Application? = null
        private val instance: Application
            private get() {
                if (INSTANCE == null) {
                    INSTANCE = Application()
                }
                return INSTANCE!!
            }

        fun getMainWindow(): MainWindow? {
            return instance.mainWindowIntl
        }

        fun getInjector(): Injector {
            return instance.injectorIntl
        }

        @JvmStatic
        fun main(args: Array<String>) {
            instance.start()
        }
    }
}