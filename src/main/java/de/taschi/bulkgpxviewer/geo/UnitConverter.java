package de.taschi.bulkgpxviewer.geo;

public class UnitConverter {
	private UnitConverter() {
		// prevent instantiation
	}
	
	private static final double KILOMETERS_TO_MILES = 0.621371;
	
	public static double kilometersToMiles(double km) {
		return km * KILOMETERS_TO_MILES;
	}
	
	public static double milesToKilometers(double miles) {
		return miles / KILOMETERS_TO_MILES;
	}
}
