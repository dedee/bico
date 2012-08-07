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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.google.android.apps.mytracks.stats.TripStatistics;

/**
 * First dumb UI. Just text. Will be improved soon.
 * 
 * @author dedee
 * 
 */
public class DefaultUserInterface implements UserInterface {

	private static final int FONT_SIZE = 8;
	private static final String FONT_NAME = "metawatch_8pt_5pxl_CAPS.ttf";
	private static final String STATUS = "STATUS";
	private static final Resolution DEFAULT_RESOLUTION = new Resolution(96, 32);

	private static final int WIDGET_PRIORITY_ACTIVE = 1; // 1 if active
	private static final int WIDGET_PRIORITY_INACTIVE = 0; // 0 if inactive

	private Context context;

	private TripStatistics lastStatistics;
	private TripStatistics demoStatistics;
	private Resolution resolution = DEFAULT_RESOLUTION;
	private boolean active;

	public DefaultUserInterface(Context context) {
		this.context = context;

		demoStatistics = new TripStatistics();
		demoStatistics.setMovingTime(25 * 60 * 1000);
		demoStatistics.setTotalElevationGain(280);
		demoStatistics.setTotalDistance(10 * 1000);
	}

	@Override
	public List<Resolution> getSupportedResolutions() {
		List<Resolution> l = new ArrayList<Resolution>();
		l.add(DEFAULT_RESOLUTION);
		return l;
	}

	@Override
	public void setActiveResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	@Override
	public Resolution getActiveResolution() {
		return resolution;
	}

	@Override
	public void sendTripStatistics(TripStatistics tripStatistics) {
		lastStatistics = tripStatistics;
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		if (tripStatistics != null) {
			double speed = Units.convertSpeed(tripStatistics.getAverageMovingSpeed());
			long elevationGain = (long) Units.convertElevationGain(tripStatistics.getTotalElevationGain());
			l.add(new StatisticsInfo(context.getString(R.string.status), context.getString(R.string.active)));
			l.add(new StatisticsInfo(context.getString(R.string.avgspeed), String.format("%.1f", speed)));
			l.add(new StatisticsInfo(context.getString(R.string.time), Units.durationToString(tripStatistics
					.getMovingTime())));
			l.add(new StatisticsInfo(context.getString(R.string.elevation), Long.toString(elevationGain)));
			active = true;
		} else {
			l.add(new StatisticsInfo(STATUS, context.getString(R.string.notactive)));
			active = false;
		}
		sendStatistics(l, active);
	}

	private void sendStatistics(List<StatisticsInfo> l, boolean active) {
		Bitmap bitmap = createTextBitmap(context, l);
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, resolution.getWidgetIdentifier(),
				resolution.getWidgetDescription(), active ? WIDGET_PRIORITY_ACTIVE : WIDGET_PRIORITY_INACTIVE);
		context.sendBroadcast(intent);
		Log.d(C.TAG, "Broadcast sent to MetaWatch: " + l);
	}

	@Override
	public void sendDemoStatistics() {
		sendTripStatistics(demoStatistics);
	}

	public void clearScreen() {
		// Clear widget screen.
		sendTripStatistics(null);
	}

	public void repaint() {
		sendTripStatistics(lastStatistics);
	}

	public void vibrate() {
		Intent broadcast = new Intent("org.metawatch.manager.VIBRATE");
		Bundle b = new Bundle();
		b.putInt("vibrate_on", 200);
		b.putInt("vibrate_off", 200);
		b.putInt("vibrate_cycles", 2);
		broadcast.putExtras(b);
		context.sendBroadcast(broadcast);
	}

	private Bitmap createTextBitmap(Context context, List<StatisticsInfo> lsi) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), FONT_NAME);
		Bitmap bitmap = Bitmap.createBitmap(resolution.getWidth(), resolution.getHeight(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(FONT_SIZE);
		paint.setTypeface(typeface);
		canvas.drawColor(Color.WHITE);

		int xoffset = 5;
		int yoffset = 5;
		int yincrement = 8;
		for (StatisticsInfo si : lsi) {
			String text = si.toString();
			canvas.drawText(text, xoffset, yoffset, paint);
			yoffset += yincrement;
		}

		return bitmap;
	}

	class StatisticsInfo {

		private String label;
		private String value;

		public StatisticsInfo(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return getLabel() + ": " + getValue();
		}

	}

}
