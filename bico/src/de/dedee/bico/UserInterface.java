package de.dedee.bico;

import com.google.android.apps.mytracks.stats.TripStatistics;

public interface UserInterface {

	void sendTripStatistics(TripStatistics tripStatistics);

	void sendDemoStatistics();

	void clearScreen();

	void repaint();

	void vibrate();

}
