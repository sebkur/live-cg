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
package de.topobyte.livecg.algorithms.frechet.distanceterrain;

import java.util.ArrayList;
import java.util.List;

public class DistanceTerrainConfig
{

	private boolean drawGrid = true;
	private int scale = 600;

	private List<ConfigChangedListener> listeners = new ArrayList<>();

	public boolean isDrawGrid()
	{
		return drawGrid;
	}

	public void setDrawGrid(boolean drawGrid)
	{
		this.drawGrid = drawGrid;
	}

	public int getScale()
	{
		return scale;
	}

	public void setScale(int scale)
	{
		this.scale = scale;
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
