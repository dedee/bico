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

import de.dedee.bico.csm.states.Event;

public abstract class AbstractState implements State {

	protected StateContext ctx;

	public AbstractState(StateContext stateContext) {
		this.ctx = stateContext;
	}

	@Override
	public void init() {
	}

	@Override
	public void work() throws StateExecutionException {
	}

	@Override
	public boolean handleEvent(Event evt) {
		switch (evt) {
		case Terminate:
			ctx.changeTo(ctx.getStates().getStateEnd());
			return true;
		default:
			return false;
		}
	}

	@Override
	public void enter() {
	}

	@Override
	public void leave() {
	}
}
