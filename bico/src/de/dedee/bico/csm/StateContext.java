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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.content.Context;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.UserInterface;
import de.dedee.bico.csm.states.Event;
import de.dedee.bico.csm.states.States;

/**
 * Event driven Finite State Machine.
 * 
 * @author dedee
 * 
 */
public class StateContext implements Runnable {

	private State state;
	private Thread thread;
	private Context appContext;
	private ContextData data;
	private States states;
	private Object monitorEvents = new Object();
	private volatile Event currentEvent;
	private UserInterface ui;
	private BlockingQueue<Event> events;

	public StateContext(Context appContext, UserInterface ui) {
		events = new ArrayBlockingQueue<Event>(20);
		states = new States(this);
		data = new ContextData(this);
		this.ui = ui;
		this.appContext = appContext;

		state = states.getStateIdle();

		thread = new Thread(this);
	}

	public void changeTo(State newState) {
		Log.i(C.TAG, "Changing state from " + state + " to " + newState);
		this.state = newState;
	}

	public boolean isIn(State state) {
		return state.equals(this.state);
	}

	public void sendEvent(Event evt) {
		try {
			events.put(evt);
		} catch (InterruptedException e) {
			Log.e(C.TAG, "Interrupted sendEvents", e);
		}
	}

	public void start() {
		thread.start();
	}

	public void interrupt() {
		thread.interrupt();
	}

	public Context getAppContext() {
		return appContext;
	}

	public ContextData getData() {
		return data;
	}

	@Override
	public void run() {
		try {
			boolean running = true;

			while (running) {
				if (state.equals(states.getStateEnd())) {
					Log.i(C.TAG, "Terminated");
					running = false;
					continue;
				}

				Log.i(C.TAG, "Working in state " + state);
				state.work();

				Event event = events.take();
				boolean status = state.handleEvent(event);
				Log.i(C.TAG, "State " + state + " handled event: " + event + " status:" + status);
			}

		} catch (StateExecutionException e) {
			e.printStackTrace();// FIXME
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public States getStates() {
		return states;
	}

	public boolean waitFor(Event evt, long timeoutMilliseconds) {
		try {
			synchronized (monitorEvents) {
				monitorEvents.wait(timeoutMilliseconds);
				if (currentEvent != null && currentEvent.equals(evt)) {
					return true;
				}

			}
		} catch (InterruptedException e) {
		}
		return false;
	}

	public UserInterface getUi() {
		return ui;
	}

}
