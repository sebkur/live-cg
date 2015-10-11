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
package de.topobyte.livecg.algorithms.voronoi.fortune.ui.core;

public class FortuneConfig
{

	private boolean drawCircles, drawBeach, drawVoronoiLines, drawDelaunay,
			drawDcel;

	public FortuneConfig()
	{
		drawCircles = false;
		drawBeach = true;
		drawVoronoiLines = true;
		drawDelaunay = false;
		drawDcel = true;
	}

	public boolean isDrawCircles()
	{
		return drawCircles;
	}

	public void setDrawCircles(boolean drawCircles)
	{
		this.drawCircles = drawCircles;
	}

	public boolean isDrawBeach()
	{
		return drawBeach;
	}

	public void setDrawBeach(boolean drawBeach)
	{
		this.drawBeach = drawBeach;
	}

	public boolean isDrawVoronoiLines()
	{
		return drawVoronoiLines;
	}

	public void setDrawVoronoiLines(boolean drawVoronoiLines)
	{
		this.drawVoronoiLines = drawVoronoiLines;
	}

	public boolean isDrawDelaunay()
	{
		return drawDelaunay;
	}

	public void setDrawDelaunay(boolean drawDelaunay)
	{
		this.drawDelaunay = drawDelaunay;
	}

	public boolean isDrawDcel()
	{
		return drawDcel;
	}

	public void setDrawDcel(boolean drawDcel)
	{
		this.drawDcel = drawDcel;
	}

}
