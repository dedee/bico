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

package de.dedee.bico.csm;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.google.android.apps.mytracks.services.ITrackRecordingService;

import de.dedee.bico.C;
import de.dedee.bico.csm.states.Event;

public class ContextData {

	private StateContext ctx;

	private ITrackRecordingService myTracksService;

	public ContextData(StateContext ctx) {
		this.ctx = ctx;
	}

	public ServiceConnection getServiceConnection() {
		return serviceConnection;
	}

	public ITrackRecordingService getMyTracksService() {
		return myTracksService;
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(C.TAG, "onServiceConnected");
			myTracksService = ITrackRecordingService.Stub.asInterface(service);
			Log.d(C.TAG, "Service connected " + className);
			ctx.sendEvent(Event.Connected);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.i(C.TAG, "onServiceDisconnected " + className);
			myTracksService = null;
			ctx.sendEvent(Event.Disconnected);
		}
	};

	private boolean previousTrackStatisticsUpdatedOnce;

	public boolean isPreviousTrackStatisticsUpdatedOnce() {
		return previousTrackStatisticsUpdatedOnce;
	}

	public void setPreviousTrackStatisticsUpdatedOnce(boolean previousTrackStatisticsUpdatedOnce) {
		this.previousTrackStatisticsUpdatedOnce = previousTrackStatisticsUpdatedOnce;
	}
}
