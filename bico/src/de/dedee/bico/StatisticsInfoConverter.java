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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.google.android.apps.mytracks.stats.TripStatistics;

public class StatisticsInfoConverter {

	private static final String STATUS = "STATUS";
	private static final String ELEVATION = "ELEVATION";
	private static final String TIME = "TIME";
	private static final String AVG_SPEED = "AVG SPEED";

	private static final String countryCode = Locale.getDefault().getISO3Country().toUpperCase();

	static {
		Log.i(C.TAG, "Using " + (isMetric() ? "metric" : "imperial") + " units, countryCode is " + countryCode);
	}

	public static List<StatisticsInfo> convert(TripStatistics ts) {
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		if (ts != null) {
			double speed = convertSpeed(ts.getAverageMovingSpeed());
			long elevationGain = (long) convertElevationGain(ts.getTotalElevationGain());
			l.add(new StatisticsInfo(STATUS, "ACTIVE"));
			l.add(new StatisticsInfo(AVG_SPEED, String.format("%.1f", speed)));
			l.add(new StatisticsInfo(TIME, Long.toString(ts.getMovingTime() / 1000 / 60)));
			l.add(new StatisticsInfo(ELEVATION, Long.toString(elevationGain)));
		} else {
			l.add(new StatisticsInfo(STATUS, "INVALID"));
			l.add(new StatisticsInfo(AVG_SPEED, "---"));
			l.add(new StatisticsInfo(TIME, "---"));
			l.add(new StatisticsInfo(ELEVATION, "---"));
		}
		return l;
	}

	public static List<StatisticsInfo> getDemoStatistics() {
		// TODO Also imperial units should be used here
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		l.add(new StatisticsInfo(STATUS, "DEMO"));
		l.add(new StatisticsInfo(AVG_SPEED, "25"));
		l.add(new StatisticsInfo(TIME, "60"));
		l.add(new StatisticsInfo(ELEVATION, "321"));
		return l;
	}

	public static List<StatisticsInfo> getClearScreenStatistics() {
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		l.add(new StatisticsInfo(STATUS, "NOT ACTIVE"));
		l.add(new StatisticsInfo(AVG_SPEED, ""));
		l.add(new StatisticsInfo(TIME, ""));
		l.add(new StatisticsInfo(ELEVATION, ""));
		return l;
	}

	private static double convertElevationGain(double elevationGainInMeters) {
		if (isMetric()) {
			return elevationGainInMeters;
		} else {
			// Return in feet
			return elevationGainInMeters * 3.2808399;
		}
	}

	private static double convertSpeed(double speedMeterPerSeconds) {
		if (isMetric()) {
			return speedMeterPerSeconds * 3.6;
		} else {
			return speedMeterPerSeconds * 2.23693629;
		}
	}

	private final static boolean isMetric() {
		// http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3
		boolean metric = true;
		if (countryCode != null) {
			if (countryCode.equals("USA") || countryCode.equals("GBR"))
				metric = false;
		}
		return metric;
	}
}
