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

import android.content.Context;
import android.util.Log;

import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.Track;
import com.google.android.apps.mytracks.stats.TripStatistics;

public class MyTracksTripStatistics {

	public static TripStatistics getTripStatistics(Context context) {
		try {
			TripStatistics tripStatistics = null;
			MyTracksProviderUtils myTracksProviderUtils = MyTracksProviderUtils.Factory
					.get(context);
			Track track = myTracksProviderUtils.getLastTrack();
			if (track != null) {
				tripStatistics = track.getTripStatistics();
			} else {
				Log.e(C.TAG, "No last track");
			}
			return tripStatistics;
		} catch (Exception e) {
			Log.e(C.TAG, "Could not get actual trip statistics", e);
			return null;
		}
	}

}
