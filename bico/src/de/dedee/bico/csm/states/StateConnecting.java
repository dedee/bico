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

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.R;
import de.dedee.bico.csm.AbstractState;
import de.dedee.bico.csm.StateContext;

public class StateConnecting extends AbstractState {

	public StateConnecting(StateContext ctx) {
		super(ctx);
	}

	@Override
	public void work() {
		Intent mytracksIntent = new Intent();
		ComponentName componentName = new ComponentName(ctx.getAppContext()
				.getString(R.string.mytracks_service_package), ctx.getAppContext().getString(
				R.string.mytracks_service_class));
		mytracksIntent.setComponent(componentName);
		// // The startService is req
		// ctx.getAppContext().startService(mytracksIntent);
		// boolean status = ctx.getAppContext().bindService(mytracksIntent, ctx.getData().getServiceConnection(), 0);
		boolean status = ctx.getAppContext().bindService(mytracksIntent, ctx.getData().getServiceConnection(),
				Service.BIND_AUTO_CREATE);
		Log.d(C.TAG, "Started service via intent... Status: " + status);
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt) {
		case Connected: {
			ctx.changeTo(ctx.getStates().getStateConnected());
			return true;
		}
		case Disconnected: {
			ctx.changeTo(ctx.getStates().getStateDisconnected());
			return true;
		}
		default:
			return super.handleEvent(evt);
		}
	}

}
