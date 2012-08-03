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

}
