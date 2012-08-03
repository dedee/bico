package de.dedee.bico;

import java.util.Locale;

import android.util.Log;

public class Units {
	private static final String countryCode = Locale.getDefault().getISO3Country().toUpperCase();

	static {
		Log.i(C.TAG, "Using " + (isMetric() ? "metric" : "imperial") + " units, countryCode is " + countryCode);
	}

	public static double convertElevationGain(double elevationGainInMeters) {
		if (isMetric()) {
			return elevationGainInMeters;
		} else {
			// Return in feet
			return elevationGainInMeters * 3.2808399;
		}
	}

	public static double convertSpeed(double speedMeterPerSeconds) {
		if (isMetric()) {
			return speedMeterPerSeconds * 3.6;
		} else {
			return speedMeterPerSeconds * 2.23693629;
		}
	}

	public final static boolean isMetric() {
		// http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
		boolean metric = true;
		if (countryCode != null) {
			if (countryCode.equals("USA") || countryCode.equals("GBR"))
				metric = false;
		}
		return metric;
	}
}
