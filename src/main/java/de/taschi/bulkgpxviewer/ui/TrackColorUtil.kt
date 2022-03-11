package de.taschi.bulkgpxviewer.ui

import com.google.inject.Inject
import com.google.inject.Singleton
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.settings.ColorConverter
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor
import de.taschi.bulkgpxviewer.ui.TrackColorUtil
import org.slf4j.LoggerFactory
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
*/ /**
 * Utility class for assigning colors to tracks
 */
@Singleton
class TrackColorUtil constructor() {
    @Inject
    private lateinit var settingsManager: SettingsManager

    /**
     * Get a color for a track. This should be consistent as long as the list of colors in the settings doesn't change.
     * @param track
     * @return
     */
    fun getColorForTrack(track: GpxFile): Color {

        val settingColors: List<SettingsColor> = settingsManager.settings?.routeColors?.filterNotNull() ?: emptyList()

        val colors = ColorConverter.convertToAwt(settingColors)
        if (colors.isEmpty()) {
            log.error("List of available colors is empty") //$NON-NLS-1$
            return Color.RED
        }
        var hash: Int = track.hashCode()
        if (hash < 0) {
            hash += Int.MAX_VALUE
        }
        val idx: Int = hash % colors.size
        return colors[idx]
    }

    companion object {
        private val log = LoggerFactory.getLogger(TrackColorUtil::class.java)
    }
}