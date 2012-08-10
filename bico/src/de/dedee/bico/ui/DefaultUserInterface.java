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

package de.dedee.bico.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.apps.mytracks.stats.TripStatistics;

import de.dedee.bico.C;
import de.dedee.bico.theme.Theme;
import de.dedee.bico.theme.ThemeFactory;
import de.dedee.bico.theme.UpdateData;
import de.dedee.bico.theme.UpdateMode;

public class DefaultUserInterface implements UserInterface {

	private Theme theme;
	private Context context;
	private WidgetVariant variant;
	private int widgetPrio = 1;

	public DefaultUserInterface(Context context, WidgetVariant variant, String themeId) {
		this.context = context;
		this.variant = variant;
		this.theme = ThemeFactory.createTheme(context, variant.getResolution(), themeId);
		sendDemoStatistics();
	}

	@Override
	public void sendTripStatistics(TripStatistics tripStatistics, String title) {
		if (tripStatistics != null) {
			UpdateData ud = new UpdateData();
			ud.setTitle(title);
			ud.setAverageSpeed(Units.convertSpeed(tripStatistics.getAverageMovingSpeed()));
			ud.setMovingTime(Units.durationToString(tripStatistics.getMovingTime()));
			ud.setElevationGain(Units.convertElevationGain(tripStatistics.getTotalElevationGain()));

			Bitmap bitmap = theme.createTextBitmap(UpdateMode.Recording, ud);
			send(bitmap);
		}
	}

	@Override
	public void sendDemoStatistics() {
		TripStatistics tripStatistics = new TripStatistics();
		tripStatistics.setTotalElevationGain(1234);
		tripStatistics.setTotalDistance(25.6 * 1000);
		tripStatistics.setMovingTime(1000 * 60 * 45);

		UpdateData ud = new UpdateData();
		ud.setTitle("Demo");
		ud.setAverageSpeed(Units.convertSpeed(tripStatistics.getAverageMovingSpeed()));
		ud.setMovingTime(Units.durationToString(tripStatistics.getMovingTime()));
		ud.setElevationGain(Units.convertElevationGain(tripStatistics.getTotalElevationGain()));

		Bitmap bitmap = theme.createTextBitmap(UpdateMode.Demo, ud);
		send(bitmap);
	}

	@Override
	public void clearScreen() {
		Bitmap bitmap = theme.createTextBitmap(UpdateMode.Idle, null);
		send(bitmap);
	}

	@Override
	public void repaint() {
		Bitmap bitmap = theme.getLastBitmap();
		send(bitmap);
	}

	private void send(Bitmap bitmap) {
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, variant.getId(), variant.getDescription(), widgetPrio);
		context.sendBroadcast(intent);
		Log.d(C.TAG, "Updating widget id: " + variant.getId());
	}

}
