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
package de.topobyte.livecg.core.lina2;

import de.topobyte.livecg.core.geometry.geom.Coordinate;

public class Vector
{

	private final double x;
	private final double y;

	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector(Coordinate c)
	{
		this(c.getX(), c.getY());
	}

	public Vector(Coordinate from, Coordinate to)
	{
		this(to.getX() - from.getX(), to.getY() - from.getY());
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public String toString()
	{
		return String.format("%f,%f", x, y);
	}

	public Vector add(Vector other)
	{
		return new Vector(x + other.x, y + other.y);
	}

	public Vector sub(Vector other)
	{
		return new Vector(x - other.x, y - other.y);
	}

	public Vector mult(double lambda)
	{
		return new Vector(x * lambda, y * lambda);
	}

	public double dotProduct(Vector other)
	{
		return x * other.x + y * other.y;
	}

	public double norm()
	{
		return Math.sqrt(x * x + y * y);
	}

	public Vector normalized()
	{
		return mult(1.0 / this.norm());
	}

	public Vector perpendicularLeft()
	{
		return new Vector(-y, x);
	}

	public Vector perpendicularRight()
	{
		return new Vector(y, -x);
	}

}
