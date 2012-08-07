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

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.R;
import de.dedee.bico.csm.AbstractState;
import de.dedee.bico.csm.StateContext;
import de.dedee.bico.csm.StateExecutionException;

public class StateDisconnecting extends AbstractState {

	public StateDisconnecting(StateContext ctx) {
		super(ctx);
	}

	@Override
	public void work() throws StateExecutionException {
		Intent mytracksIntent = new Intent();
		ComponentName componentName = new ComponentName(ctx.getAppContext()
				.getString(R.string.mytracks_service_package), ctx.getAppContext().getString(
				R.string.mytracks_service_class));
		mytracksIntent.setComponent(componentName);

		try {
			ctx.getAppContext().unbindService(ctx.getData().getServiceConnection());
		} catch (Exception e) {
			Log.w(C.TAG, "Could not unbind service", e);
		}
		ctx.sendEvent(Event.Disconnected);
		// try {
		// ctx.getAppContext().stopService(mytracksIntent);
		// } catch (Exception e) {
		// Log.w(C.TAG, "Could not stop service", e);
		// }
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt) {
		case Disconnected: {
			ctx.changeTo(ctx.getStates().getStateDisconnected());
			return true;
		}
		default:
			return super.handleEvent(evt);
		}
	}

}
