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

import android.content.Context;

public class WidgetVariant {

	private String id;
	private Resolution resolution;
	private String name;
	private String description;
	private boolean active;
	private UserInterface ui;

	/**
	 * @param id
	 * @param resolution
	 */
	public WidgetVariant(String name, Resolution resolution, String description, Context context) {
		this.name = name;
		this.resolution = resolution;
		this.description = name + " (" + resolution.getWidth() + "x" + resolution.getHeight() + ")";
		this.id = this.description.replace(' ', '_');
		this.ui = new DefaultUserInterface(context, this, "FIXME");
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public String getDescription() {
		return description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return id;
	}

	public UserInterface getUi() {
		return ui;
	}

}
