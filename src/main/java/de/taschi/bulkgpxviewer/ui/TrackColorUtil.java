package de.taschi.bulkgpxviewer.ui;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import de.taschi.bulkgpxviewer.geo.GpxFile;
import de.taschi.bulkgpxviewer.settings.ColorConverter;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.settings.dto.SettingsColor;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class for assigning colors to tracks
 */
@Log4j2
public class TrackColorUtil {

	private static TrackColorUtil INSTANCE;
		
	public static TrackColorUtil getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TrackColorUtil();
		}
		return INSTANCE;
	}
	
	private TrackColorUtil() {
		// prevent instantiation
	}
	
	/**
	 * Get a color for a track. This should be consistent as long as the list of colors in the settings doesn't change.
	 * @param track
	 * @return
	 */
	public Color getColorForTrack(GpxFile track) {
		List<SettingsColor> settingColors = Collections.unmodifiableList(SettingsManager.getInstance().getSettings().getRouteColors());
		List<Color> colors = ColorConverter.convertToAwt(settingColors);
		
		if (colors.isEmpty()) {
			log.error("List of available colors is empty");
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
