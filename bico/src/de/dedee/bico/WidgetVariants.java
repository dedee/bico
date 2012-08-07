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

import java.util.ArrayList;
import java.util.List;

public class WidgetVariants {

	public List<WidgetVariant> getVariants() {
		List<WidgetVariant> l = new ArrayList<WidgetVariant>();

		// FIXME: Multiple variants don't work currently. There is no real guarantee that we always know which variant
		// is activated.

		l.add(new WidgetVariant("bico text small", new Resolution(96, 32), "bico text small"));
		// l.add(new WidgetVariant("bico text large", new Resolution(96, 64), "bico text large"));

		return l;
	}

	public WidgetVariant getDefault() {
		return getVariants().get(0);
	}
}
