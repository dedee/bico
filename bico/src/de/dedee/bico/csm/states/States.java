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

import de.dedee.bico.csm.StateContext;

public class States {

	private StateDisconnecting stateDisconnecting;
	private StateDisconnected stateDisconnected;
	private StateConnecting stateConnecting;
	private StateConnected stateConnected;
	private StateEnd stateEnd;
	private StateIdle stateIdle;
	private StateUpdatingStatistics stateRecording;

	public States(StateContext ctx) {
		stateIdle = new StateIdle(ctx);
		stateDisconnecting = new StateDisconnecting(ctx);
		stateDisconnected = new StateDisconnected(ctx);
		stateConnecting = new StateConnecting(ctx);
		stateConnected = new StateConnected(ctx);
		stateEnd = new StateEnd(ctx);
		stateRecording = new StateUpdatingStatistics(ctx);
	}

	public StateDisconnecting getStateDisconnecting() {
		return stateDisconnecting;
	}

	public StateConnecting getStateConnecting() {
		return stateConnecting;
	}

	public StateDisconnected getStateDisconnected() {
		return stateDisconnected;
	}

	public StateConnected getStateConnected() {
		return stateConnected;
	}

	public StateEnd getStateEnd() {
		return stateEnd;
	}

	public StateIdle getStateIdle() {
		return stateIdle;
	}

	public StateUpdatingStatistics getStateUpdatingStatistics() {
		return stateRecording;
	}
}
