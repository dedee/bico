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

public class UpdateData {

	private String title;
	private UpdateValue averageSpeed;
	private UpdateValue movingTime;
	private UpdateValue elevationGain;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UpdateValue getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(UpdateValue averageSpeed) {
		this.averageSpeed = averageSpeed;
	}

	public UpdateValue getMovingTime() {
		return movingTime;
	}

	public void setMovingTime(UpdateValue movingTime) {
		this.movingTime = movingTime;
	}

	public UpdateValue getElevationGain() {
		return elevationGain;
	}

	public void setElevationGain(UpdateValue elevationGain) {
		this.elevationGain = elevationGain;
	}

	@Override
	public String toString() {
		return "UpdateData [title=" + title + ", averageSpeed=" + averageSpeed + ", movingTime=" + movingTime
				+ ", elevationGain=" + elevationGain + "]";
	}

}