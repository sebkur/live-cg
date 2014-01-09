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

import de.topobyte.livecg.algorithms.frechet.distanceterrain.ConfigChangedListener;

public class BufferConfig
{

	private boolean drawOriginal = true;
	private int distance = 10;

	private List<ConfigChangedListener> listeners = new ArrayList<ConfigChangedListener>();

	public boolean isDrawOriginal()
	{
		return drawOriginal;
	}

	public void setDrawOriginal(boolean drawOriginal)
	{
		this.drawOriginal = drawOriginal;
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

}
