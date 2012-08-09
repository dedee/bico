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

package de.dedee.bico.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.R;
import de.dedee.bico.ui.Resolution;

public class DefaultTheme implements Theme {

	private static final int FONT_SIZE = 8;
	private static final String FONT_NAME = "metawatch_8pt_5pxl_CAPS.ttf";
	private Typeface typeface;
	private Bitmap bitmap;
	private Canvas canvas;
	private Context context;

	public DefaultTheme(Context context, Resolution resolution) {
		this.context = context;
		typeface = Typeface.createFromAsset(context.getAssets(), FONT_NAME);
		bitmap = Bitmap.createBitmap(resolution.getWidth(), resolution.getHeight(), Bitmap.Config.RGB_565);
		canvas = new Canvas(bitmap);

		// Initialize bitmap
		createTextBitmap(UpdateMode.Idle, null);
	}

	@Override
	public Bitmap createTextBitmap(UpdateMode mode, UpdateData data) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(FONT_SIZE);
		paint.setTypeface(typeface);
		canvas.drawColor(Color.WHITE);

		int xoffset = 5;
		int yoffset = 5;
		int yincrement = 8;

		if (data != null) {
			// TITLE
			String text = context.getString(R.string.status) + ": " + data.getTitle();
			canvas.drawText(text, xoffset, yoffset, paint);
			yoffset += yincrement;

			switch (mode) {
			case Recording:
			case Demo: {
				Log.d(C.TAG, "Updating bitmap data mode: " + mode + " data: " + data);
				// Average speed
				text = context.getString(R.string.avgspeed) + " : " + data.getAverageSpeed();
				canvas.drawText(text, xoffset, yoffset, paint);
				yoffset += yincrement;

				// Moving time
				text = context.getString(R.string.time) + " : " + data.getMovingTime();
				canvas.drawText(text, xoffset, yoffset, paint);
				yoffset += yincrement;

				// Elevation gain
				text = context.getString(R.string.elevation) + " : " + data.getElevationGain();
				canvas.drawText(text, xoffset, yoffset, paint);
				yoffset += yincrement;
				break;
			}
			case Idle: {
				Log.d(C.TAG, "Updating idle mode: " + mode);
				break;
			}
			}
		} else {
			Log.d(C.TAG, "Data is null. This is the case when the screen is cleared");
		}

		return bitmap;
	}

	@Override
	public Bitmap getLastBitmap() {
		Log.d(C.TAG, "Get last bitmap");
		return bitmap;
	}

}
