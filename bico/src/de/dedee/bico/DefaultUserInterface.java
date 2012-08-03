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
	private static final String ELEVATION = "ELEVATION";
	private static final String TIME = "TIME";
	private static final String AVG_SPEED = "AVG SPEED";
	private static final Resolution DEFAULT_RESOLUTION = new Resolution(96, 32);

	private Context context;

	private List<StatisticsInfo> lastStatistics;
	private Resolution resolution = DEFAULT_RESOLUTION;

	public DefaultUserInterface(Context context) {
		this.context = context;
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
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		if (tripStatistics != null) {
			double speed = Units.convertSpeed(tripStatistics.getAverageMovingSpeed());
			long elevationGain = (long) Units.convertElevationGain(tripStatistics.getTotalElevationGain());
			l.add(new StatisticsInfo(STATUS, "ACTIVE"));
			l.add(new StatisticsInfo(AVG_SPEED, String.format("%.1f", speed)));
			l.add(new StatisticsInfo(TIME, Long.toString(tripStatistics.getMovingTime() / 1000 / 60)));
			l.add(new StatisticsInfo(ELEVATION, Long.toString(elevationGain)));
		}
		sendStatistics(l);
	}

	private void sendStatistics(List<StatisticsInfo> l) {
		lastStatistics = l;
		Bitmap bitmap = createTextBitmap(context, l);
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, resolution.getWidgetIdentifier(),
				resolution.getWidgetDescription(), 1);
		context.sendBroadcast(intent);
		Log.d(C.TAG, "Broadcast sent to MetaWatch: " + l);
	}

	@Override
	public void sendDemoStatistics() {
		TripStatistics ts = new TripStatistics();
		ts.setMovingTime(25 * 60 * 1000);
		ts.setTotalElevationGain(280);
		ts.setTotalDistance(10 * 1000);
		sendTripStatistics(ts);
	}

	public void clearScreen() {
		// Clear widget screen.
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		l.add(new StatisticsInfo(STATUS, "NOT ACTIVE"));
		sendStatistics(l);
	}

	public void repaint() {
		if (lastStatistics != null)
			sendStatistics(lastStatistics);
		else
			clearScreen();
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
