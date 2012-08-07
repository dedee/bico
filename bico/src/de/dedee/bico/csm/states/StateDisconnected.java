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

import de.dedee.bico.csm.AbstractState;
import de.dedee.bico.csm.StateContext;
import de.dedee.bico.csm.StateExecutionException;

public class StateDisconnected extends AbstractState {

	public StateDisconnected(StateContext ctx) {
		super(ctx);
	}

	@Override
	public void work() throws StateExecutionException {
		// Automatically try to connect
		// ctx.sendEvent(Event.Connect);
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt) {
		case Connect:
			ctx.changeTo(ctx.getStates().getStateConnecting());
			return true;
		default:
			return super.handleEvent(evt);
		}
	}
}
