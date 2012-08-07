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

package de.dedee.bico.csm.states;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.Track;
import com.google.android.apps.mytracks.stats.TripStatistics;

import de.dedee.bico.C;
import de.dedee.bico.csm.AbstractState;
import de.dedee.bico.csm.StateContext;
import de.dedee.bico.csm.StateExecutionException;

public class StateUpdatingStatistics extends AbstractState {

	public StateUpdatingStatistics(StateContext ctx) {
		super(ctx);
	}

	@Override
	public void work() throws StateExecutionException {
		try {
			boolean updated = false;

			// Check if MyTracks is recording now. If not, change state to connected.
			if (ctx.getData().getMyTracksService() != null) {
				if (ctx.getData().getMyTracksService().isRecording()) {
					TripStatistics tripStatistics = null;
					MyTracksProviderUtils myTracksProviderUtils = MyTracksProviderUtils.Factory
							.get(ctx.getAppContext());

					// Read latest statistics
					Track track = myTracksProviderUtils.getLastTrack();
					if (track != null) {
						tripStatistics = track.getTripStatistics();
						if (tripStatistics != null) {
							ctx.getUi().sendTripStatistics(tripStatistics);
							updated = true;
							Log.d(C.TAG, "Updating statistics view");
						}
					} else {
						Log.e(C.TAG, "No last track");
					}

				} else {
					Log.e(C.TAG, "Mytracks service is not recording");
				}
			} else {
				Log.e(C.TAG, "No mytracks service");
			}

			if (updated) {
				// Sleep 10s and read again
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						Log.i(C.TAG, "Pinging Recording...");
						ctx.sendEvent(Event.UpdateStatistics);
					}
				}, 10 * 1000);
			} else {
				ctx.sendEvent(Event.Back);
			}

		} catch (Exception e) {
			throw new StateExecutionException("Could not check mytracks servcice recording state", e);
		}
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt) {

		case Back: {
			ctx.changeTo(ctx.getStates().getStateConnected());
			return true;
		}

		case UpdateStatistics: {
			return true;
		}

		case Disconnect: {
			ctx.changeTo(ctx.getStates().getStateDisconnecting());
			return true;
		}

		case Disconnected: {
			ctx.changeTo(ctx.getStates().getStateDisconnected());
			return true;
		}

		default: {
			return super.handleEvent(evt);
		}

		}
	}

	@Override
	public void enter() {
		ctx.getUi().vibrate();
	}

	@Override
	public void leave() {
		ctx.getUi().vibrate();
	}
}
