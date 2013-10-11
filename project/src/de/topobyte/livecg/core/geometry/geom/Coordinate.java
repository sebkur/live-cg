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

package de.topobyte.livecg.core.geometry.geom;

public class Coordinate
{
	final double x;
	final double y;

	public Coordinate(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Coordinate(Coordinate c)
	{
		this.x = c.x;
		this.y = c.y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public com.vividsolutions.jts.geom.Coordinate createCoordinate()
	{
		return new com.vividsolutions.jts.geom.Coordinate(x, y);
	}

	public double distance(Coordinate c)
	{
		double a = x - c.x;
		double b = y - c.y;
		return Math.sqrt(a * a + b * b);
	}
}
