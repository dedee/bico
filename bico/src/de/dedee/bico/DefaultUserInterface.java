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
	private static final int WIDGET_PRIORITY_ACTIVE = 1; // 1 if active
	private static final int WIDGET_PRIORITY_INACTIVE = 0; // 0 if inactive

	private Context context;
	private List<StatisticsInfo> lastStatistics;
	private int lastWidgetPrio;
	private WidgetVariant widgetVariant;

	public DefaultUserInterface(Context context) {
		this.context = context;
		this.widgetVariant = new WidgetVariants().getDefault();
	}

	@Override
	public void sendTripStatistics(TripStatistics tripStatistics) {
		sendStatistics(convertTripStatistics(tripStatistics), WIDGET_PRIORITY_ACTIVE);
	}

	private void sendStatistics(List<StatisticsInfo> l, int widgetPrio) {
		if (widgetVariant == null) {
			Log.e(C.TAG, "Bico seems not be configured");
			return;
		}

		lastWidgetPrio = widgetPrio;
		lastStatistics = l;

		Bitmap bitmap = createTextBitmap(context, l);
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, widgetVariant.getId(), widgetVariant.getDescription(),
				widgetPrio);
		context.sendBroadcast(intent);
		Log.d(C.TAG, "Updating widget id: " + widgetVariant.getId() + " data: " + l);
	}

	@Override
	public void sendDemoStatistics() {
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		l.add(new StatisticsInfo(context.getString(R.string.status), "DEMO"));
		l.add(new StatisticsInfo(context.getString(R.string.avgspeed), "24"));
		l.add(new StatisticsInfo(context.getString(R.string.time), "1234"));
		l.add(new StatisticsInfo(context.getString(R.string.elevation), "1234"));
		sendStatistics(l, WIDGET_PRIORITY_ACTIVE);
	}

	public void clearScreen() {
		// Clear widget screen.
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		l.add(new StatisticsInfo(STATUS, context.getString(R.string.notactive)));
		sendStatistics(l, WIDGET_PRIORITY_INACTIVE);
	}

	public void repaint() {
		sendStatistics(lastStatistics, lastWidgetPrio);
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

	private List<StatisticsInfo> convertTripStatistics(TripStatistics ts) {
		List<StatisticsInfo> l = new ArrayList<StatisticsInfo>();
		if (ts != null) {
			double speed = Units.convertSpeed(ts.getAverageMovingSpeed());
			long elevationGain = (long) Units.convertElevationGain(ts.getTotalElevationGain());
			l.add(new StatisticsInfo(context.getString(R.string.status), context.getString(R.string.active)));
			l.add(new StatisticsInfo(context.getString(R.string.avgspeed), String.format("%.1f", speed)));
			l.add(new StatisticsInfo(context.getString(R.string.time), Units.durationToString(ts.getMovingTime())));
			l.add(new StatisticsInfo(context.getString(R.string.elevation), Long.toString(elevationGain)));

		} else {
			l.add(new StatisticsInfo(STATUS, context.getString(R.string.notactive)));
		}
		return l;
	}

	private Bitmap createTextBitmap(Context context, List<StatisticsInfo> lsi) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), FONT_NAME);
		Bitmap bitmap = Bitmap.createBitmap(widgetVariant.getResolution().getWidth(), widgetVariant.getResolution()
				.getHeight(), Bitmap.Config.RGB_565);
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

	@Override
	public void setActiveWidgetVariant(WidgetVariant wv) {
		widgetVariant = wv;
	}

	@Override
	public WidgetVariant getActiveWidgetVariant() {
		return widgetVariant;
	}

}
