/* This file is part of LiveCG.
 *
 * Copyright (C) 2013  Sebastian Kuerten
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
package de.topobyte.livecg.algorithms.jts.buffer;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.operation.buffer.BufferParameters;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.ConfigChangedListener;

public class BufferConfig
{

	private boolean drawInput = true;
	private int distance = 10;
	private int capStyle = BufferParameters.CAP_ROUND;
	private int joinStyle = BufferParameters.JOIN_ROUND;

	private List<ConfigChangedListener> listeners = new ArrayList<>();

	public boolean isDrawInput()
	{
		return drawInput;
	}

	public void setDrawInput(boolean drawInput)
	{
		this.drawInput = drawInput;
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
	}

	public void addConfigChangedListener(ConfigChangedListener listener)
	{
		listeners.add(listener);
	}

	public void removeConfigChangedListener(ConfigChangedListener listener)
	{
		listeners.remove(listener);
	}

	public void fireConfigChanged()
	{
		for (ConfigChangedListener listener : listeners) {
			listener.configChanged();
		}
	}

	public int getCapStyle()
	{
		return capStyle;
	}

	public void setCapStyle(int capStyle)
	{
		this.capStyle = capStyle;
	}

	public int getJoinStyle()
	{
		return joinStyle;
	}

	public void setJoinStyle(int joinStyle)
	{
		this.joinStyle = joinStyle;
	}

}
