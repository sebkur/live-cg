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
package de.topobyte.frechet.freespace;

public class Config
{

	private boolean drawGrid = true;
	private boolean drawReachableSpace = false;
	private boolean drawReachableSpaceMarkers = false;
	private boolean drawFreeSpaceMarkers = false;

	public boolean isDrawGrid()
	{
		return drawGrid;
	}

	public void setDrawGrid(boolean drawGrid)
	{
		this.drawGrid = drawGrid;
	}

	public void setDrawReachableSpace(boolean drawReachableSpace)
	{
		this.drawReachableSpace = drawReachableSpace;
	}

	public boolean isDrawReachableSpace()
	{
		return drawReachableSpace;
	}

	public boolean isDrawReachableSpaceMarkers()
	{
		return drawReachableSpaceMarkers;
	}

	public void setDrawReachableSpaceMarkers(boolean drawReachableSpaceMarkers)
	{
		this.drawReachableSpaceMarkers = drawReachableSpaceMarkers;
	}

	public boolean isDrawFreeSpaceMarkers()
	{
		return drawFreeSpaceMarkers;
	}

	public void setDrawFreeSpaceMarkers(boolean drawFreeSpaceMarkers)
	{
		this.drawFreeSpaceMarkers = drawFreeSpaceMarkers;
	}

}
