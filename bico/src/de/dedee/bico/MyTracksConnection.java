package de.dedee.bico;

import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.Track;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.stats.TripStatistics;

public class MyTracksConnection {

	private final static int TIME_MILLISECONDS = 1000;
	private static final int TIME_PERIOD = 10 * TIME_MILLISECONDS;
	private static final int TIME_INITIAL_DELAY = 2 * TIME_MILLISECONDS;

	private ITrackRecordingService myTracksService;
	private Timer timer;
	private Intent mytracksIntent;
	private Context context;
	private UserInterface ui;

	public MyTracksConnection(Context context, UserInterface ui) {
		this.context = context;
		this.ui = ui;
	}

	/**
	 * Binds to the MyTracks service
	 */
	public boolean bindToMyTracks() {
		Log.i(C.TAG, "Binding to MyTracks service");
		mytracksIntent = new Intent();
		ComponentName componentName = new ComponentName(context.getString(R.string.mytracks_service_package),
				context.getString(R.string.mytracks_service_class));
		mytracksIntent.setComponent(componentName);
		// The startService is req
		context.startService(mytracksIntent);
		boolean status = context.bindService(mytracksIntent, serviceConnection, 0);
		Log.d(C.TAG, "Started service via intent... Status: " + status);

		startTimer();
		ui.clearScreen();

		return status;
	}

	/**
	 * Unbinds from MyTracks service
	 */
	public void unbindFromMyTracks() {
		Log.i(C.TAG, "Unbinding from MyTracks service");

		ui.clearScreen();
		stopTimer();

		if (myTracksService != null) {
			context.unbindService(serviceConnection);
			context.stopService(mytracksIntent);
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

	private TripStatistics getLastTrackTripStatistics() {
		try {
			TripStatistics tripStatistics = null;
			MyTracksProviderUtils myTracksProviderUtils = MyTracksProviderUtils.Factory.get(context);
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

	class UpdateTask extends TimerTask {
		@Override
		public void run() {
			if (isMyTracksActive()) {
				TripStatistics tripStatistics = getLastTrackTripStatistics();
				if (tripStatistics != null) {
					ui.sendTripStatistics(tripStatistics);
					Log.d(C.TAG, "Updating statistics view");
				}
			} else {
				Log.d(C.TAG, "MyTracks is not active right now...");
				ui.clearScreen(); // Added to see if this happens.
				stopTimer();
			}
		}
	}

	private void startTimer() {
		if (timer != null)
			timer.cancel();
		timer = new Timer();
		timer.schedule(new UpdateTask(), TIME_INITIAL_DELAY, TIME_PERIOD);
		ui.vibrate();
	}

	private void stopTimer() {
		if (timer != null)
			timer.cancel();
		ui.vibrate();
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(C.TAG, "onServiceConnected");
			myTracksService = ITrackRecordingService.Stub.asInterface(service);
			Log.d(C.TAG, "Service connected " + className);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.i(C.TAG, "onServiceDisconnected " + className);
			if (myTracksService != null) {
				context.unbindService(serviceConnection);
				myTracksService = null;
				Log.i(C.TAG, "MyTracks service unbound successfully");
			}
		}
	};
}
