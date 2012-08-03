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

public class DefaultUserInterface implements UserInterface {

	private static final int WIDGET_HEIGHT = 32;
	private static final int WIDGET_WIDTH = 96;
	private static final int FONT_SIZE = 8;
	private static final String FONT_NAME = "metawatch_8pt_5pxl_CAPS.ttf";
	private static final String STATUS = "STATUS";
	private static final String ELEVATION = "ELEVATION";
	private static final String TIME = "TIME";
	private static final String AVG_SPEED = "AVG SPEED";

	private Context context;

	private TripStatistics lastStatistics;

	public DefaultUserInterface(Context context) {
		this.context = context;
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

		lastStatistics = tripStatistics;
		Bitmap bitmap = createTextBitmap(context, l);
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, C.WIDGET_ID, C.WIDGET_DESCRIPTION, 1);
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
		// l.add(new StatisticsInfo(AVG_SPEED, ""));
		// l.add(new StatisticsInfo(TIME, ""));
		// l.add(new StatisticsInfo(ELEVATION, ""));
		Bitmap bitmap = createTextBitmap(context, l);
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, C.WIDGET_ID, C.WIDGET_DESCRIPTION, 1);
		context.sendBroadcast(intent);
		Log.d(C.TAG, "Broadcast sent to MetaWatch: " + l);
	}

	public void repaint() {
		if (lastStatistics != null)
			sendTripStatistics(lastStatistics);
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

	private static Bitmap createTextBitmap(Context context, List<StatisticsInfo> lsi) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), FONT_NAME);
		Bitmap bitmap = Bitmap.createBitmap(WIDGET_WIDTH, WIDGET_HEIGHT, Bitmap.Config.RGB_565);
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

}
