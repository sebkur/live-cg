/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

package de.topobyte.livecg.algorithms.frechet.freespace.calc;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.lina2.Vector;

public class LineSegment
{

	private final Coordinate c1;
	private final Coordinate c2;

	public LineSegment(Coordinate c1, Coordinate c2)
	{
		this.c1 = c1;
		this.c2 = c2;
	}

	public Coordinate getCoordinate1()
	{
		return c1;
	}

	public Coordinate getCoordinate2()
	{
		return c2;
	}

	public Vector getStart()
	{
		return new Vector(c1.getX(), c1.getY());
	}

	public Vector getDirection()
	{
		return new Vector(c2.getX() - c1.getX(), c2.getY() - c1.getY());
	}
}
