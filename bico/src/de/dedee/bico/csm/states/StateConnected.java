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

import android.os.RemoteException;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.csm.AbstractState;
import de.dedee.bico.csm.StateContext;
import de.dedee.bico.csm.StateExecutionException;

public class StateConnected extends AbstractState {

	public StateConnected(StateContext ctx) {
		super(ctx);
	}

	@Override
	public void work() throws StateExecutionException {
		// Check if MyTracks is recording now
		try {
			if (ctx.getData().getMyTracksService() != null) {
				if (ctx.getData().getMyTracksService().isRecording()) {
					Log.i(C.TAG, "Mytracks is recording currently, so we change status to recording");
					ctx.sendEvent(Event.UpdateStatistics);
				} else {
					Log.i(C.TAG, "Mytracks is not recording currently");
					ctx.sendEvent(Event.Disconnect);
				}
			} else {
				Log.i(C.TAG, "Mytracks is not recording currently");
				ctx.sendEvent(Event.Disconnect);
			}
		} catch (RemoteException e) {
			Log.e(C.TAG, "Could not check if mytracks is recording", e);
			ctx.sendEvent(Event.Disconnect);
		}
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt) {
		case Disconnect: {
			ctx.changeTo(ctx.getStates().getStateDisconnecting());
			return true;
		}
		case Disconnected: {
			ctx.changeTo(ctx.getStates().getStateDisconnected());
			return true;
		}
		case UpdateStatistics: {
			ctx.changeTo(ctx.getStates().getStateUpdatingStatistics());
			return true;
		}
		default:
			return super.handleEvent(evt);
		}
	}

}
