package de.taschi.bulkgpsviewer.settings;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import de.taschi.bulkgpsviewer.settings.dto.SettingsColor;

public class ColorConverter {
	
	public static Color convert(SettingsColor in) {
		return new Color(in.getR(), in.getG(), in.getB());
	}
	
	public static SettingsColor convert(Color in) {
		return new SettingsColor(in.getRed(), in.getGreen(), in.getBlue());
	}
	
	public static List<Color> convertToAwt(List<SettingsColor> in) {
		return in.stream().map(ColorConverter::convert).collect(Collectors.toList());
	}

	public static List<SettingsColor> convertToSettings(List<Color> in) {
		return in.stream().map(ColorConverter::convert).collect(Collectors.toList());
	}
}
