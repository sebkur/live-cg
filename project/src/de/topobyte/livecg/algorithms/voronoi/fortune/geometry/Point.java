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

public class Point
{

	private double x, y;

	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Point(Point point)
	{
		x = point.x;
		y = point.y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double distance(Point point)
	{
		double dx = point.x - x;
		double dy = point.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Point)) {
			return false;
		}
		Point o = (Point) other;
		return o.getX() == getX() && o.getY() == getY();
	}

	@Override
	public String toString()
	{
		return x + ", " + y;
	}

	@Override
	public int hashCode()
	{
		long bitsX = Double.doubleToLongBits(x);
		long bitsY = Double.doubleToLongBits(y);
		long bits = bitsX + bitsY;
		return (int) (bits ^ (bits >>> 32));
	}

}
