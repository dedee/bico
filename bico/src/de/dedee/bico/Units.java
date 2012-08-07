/*  
 * bico - (C)opyright 2012 - dedee
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

	public final static String durationToString(long durationInMillis) {
		// hh:mm
		long minutes = durationInMillis / 1000 / 60;
		long h = minutes / 60;
		long m = minutes % 60;
		StringBuffer sb = new StringBuffer();

		String s_h = Long.toString(h);
		if (s_h.length() == 1)
			sb.append('0');
		sb.append(s_h);

		sb.append(':');

		String s_m = Long.toString(m);
		if (s_m.length() == 1)
			sb.append('0');
		sb.append(s_m);

		return sb.toString();
	}
}
