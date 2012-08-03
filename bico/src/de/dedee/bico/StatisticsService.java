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
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.apps.mytracks.services.ITrackRecordingService;

/**
 * Main background service which itself connects to the MyTracks service and captures all the statistics data whenever a
 * track is recorded in MyTracks. The MyTracks app itself is opensource and its API is documented basically.
 * http://code.google.com/p/mytracks/wiki/MyTracksApi
 * 
 * @author dedee
 */
public class StatisticsService extends Service {

	private static final int WIDGET_HEIGHT = 32;
	private static final int WIDGET_WIDTH = 96;
	private static final int FONT_SIZE = 8;
	private static final String FONT_NAME = "metawatch_8pt_5pxl_CAPS.ttf";
	private static final String ORG_METAWATCH_MANAGER_REFRESH_WIDGET_REQUEST = "org.metawatch.manager.REFRESH_WIDGET_REQUEST";
	private static final String COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STOPPED = "com.google.android.apps.mytracks.TRACK_STOPPED";
	private static final String COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STARTED = "com.google.android.apps.mytracks.TRACK_STARTED";
	private static final String ORG_METAWATCH_MANAGER_BUTTON_PRESS = "org.metawatch.manager.BUTTON_PRESS";
	private static final String ORG_METAWATCH_MANAGER_APPLICATION_DISCOVERY = "org.metawatch.manager.APPLICATION_DISCOVERY";
	private static final String ORG_METAWATCH_MANAGER_WIDGETS_DESIRED = "org.metawatch.manager.widgets_desired";
	private static final String ORG_METAWATCH_MANAGER_GET_PREVIEWS = "org.metawatch.manager.get_previews";
	private final static String WIDGET_ID = "bico_widget_96_32";
	private final static String WIDGET_DESCRIPTION = "bico Widget (96x32)";

	private final static int TIME_MILLISECONDS = 1000;
	private static final int TIME_PERIOD = 10 * TIME_MILLISECONDS;
	private static final int TIME_INITIAL_DELAY = 2 * TIME_MILLISECONDS;

	private final Messenger messenger = new Messenger(new IncomingHandler());
	private final MyTracksServiceStatusReceiver serviceStatusReceiver = new MyTracksServiceStatusReceiver();

	private boolean widgetEnabled;
	private Timer timer;
	private Intent mytracksIntent;
	private ITrackRecordingService myTracksService;
	private List<StatisticsInfo> lastStatistics;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(C.TAG, "Service created");

		// Register to MyTracks service to receiver notifications.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STARTED);
		intentFilter.addAction(COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STOPPED);
		intentFilter.addAction(ORG_METAWATCH_MANAGER_REFRESH_WIDGET_REQUEST);
		intentFilter.addAction(ORG_METAWATCH_MANAGER_APPLICATION_DISCOVERY);
		intentFilter.addAction(ORG_METAWATCH_MANAGER_BUTTON_PRESS);
		registerReceiver(serviceStatusReceiver, intentFilter);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		clearScreen();
		Log.d(C.TAG, "Service started");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(serviceStatusReceiver);
		clearScreen();
		stopTimer();
		Log.d(C.TAG, "Service destroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	/**
	 * Binds to the MyTracks service
	 */
	private boolean bindToMyTracks() {
		Log.i(C.TAG, "Binding to MyTracks service");
		mytracksIntent = new Intent();
		ComponentName componentName = new ComponentName(getString(R.string.mytracks_service_package),
				getString(R.string.mytracks_service_class));
		mytracksIntent.setComponent(componentName);
		// The startService is req
		startService(mytracksIntent);
		boolean status = bindService(mytracksIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		Log.d(C.TAG, "Started service via intent... Status: " + status);
		return status;
	}

	/**
	 * Unbinds from MyTracks service
	 */
	private void unbindFromMyTracks() {
		Log.i(C.TAG, "Unbinding from MyTracks service");
		if (myTracksService != null) {
			unbindService(serviceConnection);
			stopService(mytracksIntent);
		}
	}

	private boolean isMyTracksActive() {
		boolean status = false;
		try {
			if (myTracksService != null) {
				if (myTracksService.isRecording()) {
					status = true;
				} else {
					Log.d(C.TAG, "MyTracks does not record a track currently");
				}
			} else {
				Log.d(C.TAG, "MyTracks service is null");
			}
		} catch (RemoteException e) {
			Log.e(C.TAG, "Could not check service recording state", e);
		}
		Log.v(C.TAG, "MyTrack status: " + status);
		return status;
	}

	class UpdateTask extends TimerTask {
		@Override
		public void run() {
			if (isMyTracksActive()) {
				List<StatisticsInfo> lsi = StatisticsInfoConverter.convert(MyTracksTripStatistics
						.getTripStatistics(StatisticsService.this));

				Log.d(C.TAG, "Updating statistics view");
				sendStatistics(lsi);
			} else {
				Log.d(C.TAG, "MyTracks is not active right now...");
				stopTimer();
			}
		}
	}

	private void vibrate() {
		Intent broadcast = new Intent("org.metawatch.manager.VIBRATE");
		Bundle b = new Bundle();
		b.putInt("vibrate_on", 200);
		b.putInt("vibrate_off", 200);
		b.putInt("vibrate_cycles", 2);
		broadcast.putExtras(b);
		sendBroadcast(broadcast);
	}

	private void startTimer() {
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		timer.schedule(new UpdateTask(), TIME_INITIAL_DELAY, TIME_PERIOD);
		vibrate();
	}

	private void stopTimer() {
		if (timer != null)
			timer.cancel();
		vibrate();
	}

	private void clearScreen() {
		// Clear widget screen.
		sendStatistics(StatisticsInfoConverter.getClearScreenStatistics());
	}

	private void sendStatistics(List<StatisticsInfo> lsi) {
		lastStatistics = lsi;
		Bitmap bitmap = createTextBitmap(this, lsi);
		Intent intent = Utils.createWidgetUpdateIntent(bitmap, WIDGET_ID, WIDGET_DESCRIPTION, 1);
		sendBroadcast(intent);
		Log.d(C.TAG, "Broadcast sent to MetaWatch: " + lsi);
	}

	private void repaint() {
		if (lastStatistics != null)
			sendStatistics(lastStatistics);
		else
			clearScreen();
	}

	public static Bitmap createTextBitmap(Context context, List<StatisticsInfo> lsi) {
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

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			default:
				super.handleMessage(msg);
			}
		}
	}

	class MyTracksServiceStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (action.equals(COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STARTED)) {
				Log.d(C.TAG, "The track was started");
				bindToMyTracks();
				clearScreen();
				startTimer();

			} else if (action.equals(COM_GOOGLE_ANDROID_APPS_MYTRACKS_TRACK_STOPPED)) {
				Log.d(C.TAG, "The track was stopped");
				unbindFromMyTracks();
				stopTimer();
				clearScreen();

			} else if (action.equals(ORG_METAWATCH_MANAGER_REFRESH_WIDGET_REQUEST)) {
				Log.d(C.TAG, "Widget update requested we resend the previous stats or clear the screen");
				Bundle bundle = intent.getExtras();
				if (bundle.containsKey(ORG_METAWATCH_MANAGER_WIDGETS_DESIRED)) {
					ArrayList<String> activatedWidgetIds = new ArrayList<String>(Arrays.asList(bundle
							.getStringArray(ORG_METAWATCH_MANAGER_WIDGETS_DESIRED)));
					// Check if widgets_desired contains each widget ID you're responsible for
					// and send an update
					widgetEnabled = (activatedWidgetIds != null && activatedWidgetIds.contains(WIDGET_ID));
					if (widgetEnabled) {
						Log.i(C.TAG, "Got REFRESH_WIDGET_REQUEST and we are activated");
					}
				}
				boolean previewRequested = bundle.containsKey(ORG_METAWATCH_MANAGER_GET_PREVIEWS);
				if (previewRequested) {
					Log.d(C.TAG, "A preview picture is requested, so sending some nice preview");
					sendStatistics(StatisticsInfoConverter.getDemoStatistics());
				} else if (widgetEnabled) {
					repaint();
				}

			} else if (action.equals(ORG_METAWATCH_MANAGER_APPLICATION_DISCOVERY)) {
				Log.d(C.TAG, "Received Discovery request from MWM");
				// When the MWM app sends the discovery we update the screen once to get it included in the widget list.
				// Note: We are no MWM app - just a widget. So nothing to announce.
				repaint();

			} else if (action.equals(ORG_METAWATCH_MANAGER_BUTTON_PRESS)) {
				Log.d(C.TAG, "Received a button event from the MetaWatch. Currently we ignore this");

			} else {
				Log.w(C.TAG, "Ignored action in receiver " + action);
			}
		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			myTracksService = ITrackRecordingService.Stub.asInterface(service);
			Log.d(C.TAG, "Service connected " + className);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.d(C.TAG, "Service disconnected " + className);
			if (myTracksService != null) {
				unbindService(serviceConnection);
				myTracksService = null;
			}
		}
	};

}
