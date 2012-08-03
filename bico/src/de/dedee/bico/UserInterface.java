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

package de.dedee.bico;

import java.util.List;

import com.google.android.apps.mytracks.stats.TripStatistics;

public interface UserInterface {

	/**
	 * Returns a list of supported resolutions (e.g. 96x32, etc.).
	 * 
	 * @return List of supported resolutions
	 */
	List<Resolution> getSupportedResolutions();

	/**
	 * Configures which resolution shall be used.
	 * 
	 * @param resolution
	 *            Active resolution
	 */
	void setActiveResolution(Resolution resolution);

	/**
	 * Returns active resolution.
	 * 
	 * @return Active resolution
	 */
	Resolution getActiveResolution();

	void sendTripStatistics(TripStatistics tripStatistics);

	void sendDemoStatistics();

	void clearScreen();

	void repaint();

	void vibrate();

	void setActive(boolean b);

}
