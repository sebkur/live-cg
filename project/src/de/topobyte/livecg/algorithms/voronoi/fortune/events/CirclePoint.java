/* This file is part of LiveCG. 
 * 
 * Copyright (C) 1997-1999 Pavel Kouznetsov
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
package de.topobyte.livecg.algorithms.voronoi.fortune.events;

import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ArcNode;

public class CirclePoint extends EventPoint
{

	private double radius;
	private ArcNode arc;

	public CirclePoint(double x, double y, ArcNode arcnode)
	{
		super(x, y);
		arc = arcnode;
		radius = distance(arcnode);
		setX(getX() + radius);
	}

	public double getRadius()
	{
		return radius;
	}

	public ArcNode getArc()
	{
		return arc;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof CirclePoint)) {
			return false;
		}
		CirclePoint o = (CirclePoint) other;
		return o.getX() == getX() && o.getY() == getY();
	}
}
