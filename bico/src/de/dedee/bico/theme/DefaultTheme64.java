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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import de.dedee.bico.C;
import de.dedee.bico.R;
import de.dedee.bico.ui.Resolution;

public class DefaultTheme64 implements Theme {

	private Typeface typefaceLarge;
	private Typeface typefaceSmall;
	private Bitmap bitmap;
	private Canvas canvas;
	private Context context;
	private Paint paintSmall;
	private Paint paintLarge;

	public DefaultTheme64(Context context, Resolution resolution) {
		this.context = context;

		typefaceLarge = Typeface.createFromAsset(context.getAssets(), "metawatch_16pt_11pxl.ttf");
		typefaceSmall = Typeface.createFromAsset(context.getAssets(), "metawatch_8pt_5pxl_CAPS.ttf");

		bitmap = Bitmap.createBitmap(resolution.getWidth(), resolution.getHeight(), Bitmap.Config.RGB_565);
		canvas = new Canvas(bitmap);

		// Initialize bitmap
		createTextBitmap(UpdateMode.Idle, null);

		paintSmall = new Paint();
		paintSmall.setColor(Color.BLACK);
		paintSmall.setTextSize(8);
		paintSmall.setTypeface(typefaceSmall);
		paintLarge = new Paint();
		paintLarge.setColor(Color.BLACK);
		paintLarge.setTextSize(16);
		paintLarge.setTypeface(typefaceLarge);
	}

	@Override
	public Bitmap createTextBitmap(UpdateMode mode, UpdateData data) {

		canvas.drawColor(Color.WHITE);

		int yoffset = 0;
		int rowHeight = 0;

		if (data != null) {
			// TITLE
			rowHeight = drawHeader(context.getString(R.string.status) + ": " + data.getTitle(), yoffset);
			yoffset += rowHeight + 5;

			switch (mode) {
			case Recording:
			case Demo: {
				// Log.d(C.TAG, "Updating bitmap data mode: " + mode + " data: " + data);

				// Average speed
				rowHeight = drawRow(data.getAverageSpeed(), yoffset);
				yoffset += rowHeight + 5;

				// Moving time
				rowHeight = drawRow(data.getMovingTime(), yoffset);
				yoffset += rowHeight + 5;

				// Elevation gain
				rowHeight = drawRow(data.getElevationGain(), yoffset);
				yoffset += rowHeight + 5;

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

	private int drawRow(UpdateValue v, int yoffset) {
		int headerHeight = drawHeader(context.getString(R.string.avgspeed), yoffset);
		int valueHeight = drawValue(v.getValue(), yoffset);
		drawUnit(v.getUnit(), yoffset, headerHeight);
		return valueHeight;
	}

	private int drawHeader(String s, int yoffset) {
		Rect bounds = new Rect();
		paintSmall.getTextBounds(s, 0, s.length(), bounds);
		int height = Math.abs(bounds.top);
		int x = 5;
		int y = yoffset + height;
		// Log.d(C.TAG, "drawHeader x=" + x + " y=" + y);
		canvas.drawText(s, x, y, paintSmall);
		return height;
	}

	private int drawValue(String s, int yoffset) {
		Rect bounds = new Rect();
		paintLarge.getTextBounds(s, 0, s.length(), bounds);
		int height = Math.abs(bounds.top);
		int x = 96 - 5 - bounds.right;
		int y = yoffset + height;
		// Log.d(C.TAG, "drawValue x=" + x + " y=" + y);
		canvas.drawText(s, x, y, paintLarge);
		return height;
	}

	private int drawUnit(String s, int yoffset, int heiderHeight) {
		Rect bounds = new Rect();
		paintSmall.getTextBounds(s, 0, s.length(), bounds);
		int height = Math.abs(bounds.top);
		int x = 5;
		int y = yoffset + heiderHeight + height + 2;
		// Log.d(C.TAG, "drawUnit x=" + x + " y=" + y);
		canvas.drawText(s, x, y, paintSmall);
		return height;
	}

	@Override
	public Bitmap getLastBitmap() {
		Log.d(C.TAG, "Get last bitmap");
		return bitmap;
	}

}
