package de.taschi.bulkgpxviewer.settings

import com.fasterxml.jackson.jr.ob.JSON
import com.google.inject.Singleton
import de.taschi.bulkgpxviewer.settings.dto.MainWindowSettings
import de.taschi.bulkgpxviewer.settings.dto.Settings
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.SwingUtilities

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
 */@Singleton
class SettingsManager constructor() {
    var settings: Settings? = null
        private set

    /** Objects which want to be notified about settings changes  */
    private val updateListeners: MutableList<SettingsUpdateListener> = ArrayList()

    init {
        log.info("Loading settings") //$NON-NLS-1$
        loadOrInitSettings(settingsFile)
    }

    fun addSettingsUpdateListener(r: SettingsUpdateListener) {
        if (!updateListeners.contains(r)) {
            updateListeners.add(r)
        }
    }

    fun removeSettingsUpdateListener(r: SettingsUpdateListener): Boolean {
        return updateListeners.remove(r)
    }

    fun fireNotifications() {
        SwingUtilities.invokeLater(Runnable({
            for (r: SettingsUpdateListener in updateListeners) {
                r.onSettingsUpdated()
            }
        }))
    }

    fun saveSettings() {
        try {
            FileUtils.forceMkdirParent(settingsFile.toFile())
            JSON.std.write(settings, settingsFile.toFile())
        } catch (e: IOException) {
            handleException(e)
        }
    }

    private fun loadOrInitSettings(settingsFile: Path) {
        if (Files.exists(settingsFile)) {
            loadSettings(settingsFile)
            migrateIfNecessary()
            saveSettings()
        } else {
            makeNewSettings()
            saveSettings()
        }
    }

    private fun migrateIfNecessary() {
        migrateToV0_2_0()
        migrateToV0_3_0()
    }

    private fun migrateToV0_2_0() {
        if (settings?.mainWindowSettings == null) {
            log.info("Updating settings to v0.2.0") //$NON-NLS-1$
            settings?.mainWindowSettings = MainWindowSettings()
        }
    }

    private fun migrateToV0_3_0() {
        if (settings?.unitSystem == null) {
            log.info("Updating settings to v0.3.0") //$NON-NLS-1$
            settings?.unitSystem = UnitSystem.METRIC
        }
        if (settings?.selectedRouteColor == null) {
            settings?.selectedRouteColor = SettingsColor(255, 0, 0)
        }
        if (settings?.unselectedRouteColor == null) {
            settings?.unselectedRouteColor = SettingsColor(128, 128, 128)
        }
    }

    private fun makeNewSettings() {
        settings = Settings()
        val colors = ColorConverter.convertToSettings(DEFAULT_TRACK_COLORS)
        settings?.routeColors = colors
        migrateIfNecessary()
    }

    private fun loadSettings(settingsFile: Path) {
        try {
            val jsonDoc: String =
                FileUtils.readFileToString(settingsFile.toFile(), Charset.forName("UTF-8")) //$NON-NLS-1$
            settings = JSON.std
                .with(JSON.Feature.PRETTY_PRINT_OUTPUT).beanFrom(Settings::class.java, jsonDoc)
        } catch (e: IOException) {
            handleException(e)
        }
    }

    private fun handleException(e: IOException) {
        log.error("Error while trying to load settings:", e) //$NON-NLS-1$
    }

    //$NON-NLS-1$ //$NON-NLS-2$
    private val settingsFile: Path
        private get() {
            val userDir: String = FileUtils.getUserDirectoryPath()
            return Paths.get(userDir, ".bulkgpxviewer", "settings.json") //$NON-NLS-1$ //$NON-NLS-2$
        }

    companion object {
        private val log = LoggerFactory.getLogger(SettingsManager::class.java)
        private val DEFAULT_TRACK_COLORS = listOf(
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.GRAY,
            Color.WHITE, Color.PINK
        )
    }
}