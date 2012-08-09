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

package de.dedee.bico.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.android.apps.mytracks.stats.TripStatistics;

import de.dedee.bico.C;

public class WidgetVariants implements UserInterface {

	List<WidgetVariant> variants;

	public WidgetVariants(Context context) {
		variants = new ArrayList<WidgetVariant>();
		// FIXME: Multiple variants don't work currently. There is no real guarantee that we always know which variant
		// is activated.
		variants.add(new WidgetVariant("bico text small", new Resolution(96, 32), "bico text small", context));
		// variants.add(new WidgetVariant("bico text large", new Resolution(96, 64), "bico text large"));

		// Hack: We dont get the activate always from MWM.
		variants.get(0).setActive(true);
	}

	public List<WidgetVariant> getVariants() {
		return variants;
	}

	/**
	 * Activates widgets if their ID is in the given String array.
	 * 
	 * @param activatedWidgetIds
	 *            List of widget IDs
	 * @return True if at least one widget variant is activated
	 */
	public boolean activatePerIdList(ArrayList<String> activatedWidgetIds) {
		// This boolean signals that at least one widget is activated. If false bico is inactive at all.
		boolean bicoActive = false;
		// Check if widgets_desired contains each widget ID you're responsible for
		// and send an update
		Log.i(C.TAG, "MWM has configured these widgets: " + activatedWidgetIds);
		if (activatedWidgetIds != null && activatedWidgetIds.size() > 0) {
			for (WidgetVariant wv : getVariants()) {
				boolean active = activatedWidgetIds.contains(wv.getId());
				Log.d(C.TAG, "Checking widget id " + wv.getId() + " active=" + active);
				wv.setActive(active);
				if (active) {
					Log.i(C.TAG, wv.getId() + " recognized as activated in MWM widget screen, so activating this one");
					bicoActive = true;
				}
			}
		}
		return bicoActive;
	}

	/**
	 * Returns true if at least one widget is active
	 * 
	 * @return
	 */
	public boolean isActive() {
		boolean active = false;
		for (WidgetVariant wv : getVariants()) {
			if (wv.isActive()) {
				active = true;
				break;
			}
		}
		Log.d(C.TAG, "WidgetVariants isActive:" + active);
		return active;
	}

	@Override
	public void sendTripStatistics(TripStatistics tripStatistics, String title) {
		if (isActive()) {
			for (WidgetVariant wv : getVariants()) {
				if (wv.isActive()) {
					Log.d(C.TAG, "Sending sendTripStatistics with title " + title + " to widget " + wv.getId());
					wv.getUi().sendTripStatistics(tripStatistics, title);
				}
			}
		} else {
			Log.d(C.TAG, "No widget active. Skipping sendTripStatistics");
		}
	}

	@Override
	public void sendDemoStatistics() {
		if (isActive()) {
			for (WidgetVariant wv : getVariants()) {
				if (wv.isActive()) {
					wv.getUi().sendDemoStatistics();
				}
			}
		} else {
			Log.d(C.TAG, "No widget active. Skipping sendDemoStatistics");
		}
	}

	@Override
	public void clearScreen() {
		if (isActive()) {
			for (WidgetVariant wv : getVariants()) {
				if (wv.isActive()) {
					wv.getUi().clearScreen();
				}
			}
		} else {
			Log.d(C.TAG, "No widget active. Skipping clearScreen");
		}
	}

	@Override
	public void repaint() {
		if (isActive()) {
			for (WidgetVariant wv : getVariants()) {
				if (wv.isActive()) {
					wv.getUi().repaint();
				}
			}
		} else {
			Log.d(C.TAG, "No widget active. Skipping repaint");
		}
	}

}
