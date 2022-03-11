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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.settings.ColorConverter;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for assigning colors to tracks
 */
@Singleton
public class TrackColorUtil {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(TrackColorUtil.class);
	@Inject
	private SettingsManager settingsManager;
	
	/**
	 * Get a color for a track. This should be consistent as long as the list of colors in the settings doesn't change.
	 * @param track
	 * @return
	 */
	public Color getColorForTrack(GpxFile track) {
		List<SettingsColor> settingColors = Collections.unmodifiableList(settingsManager.getSettings().getRouteColors());
		List<Color> colors = ColorConverter.convertToAwt(settingColors);
		
		if (colors.isEmpty()) {
			log.error("List of available colors is empty"); //$NON-NLS-1$
			return Color.RED;
		}
		
		int hash = track.hashCode();
		if (hash < 0) {
			hash += Integer.MAX_VALUE;
		}
		int idx = hash % colors.size();
		
		return colors.get(idx);
	}
	
}
