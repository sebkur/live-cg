/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.algorithms.voronoi.fortune.geometry;

public class Edge
{

	private Point p1, p2;

	public Edge(Point p1, Point p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point getStart()
	{
		return p1;
	}

	public Point getEnd()
	{
		return p2;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Edge)) {
			return false;
		}
		Edge edge = (Edge) other;
		return edge.p1.equals(p1) && edge.p2.equals(p2);
	}

}
