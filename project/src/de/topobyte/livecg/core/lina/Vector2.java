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
package de.topobyte.livecg.core.lina;

import de.topobyte.livecg.core.geometry.geom.Coordinate;

public class Vector2
{

	private final double x;
	private final double y;

	public Vector2(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Vector2(Coordinate c)
	{
		this(c.getX(), c.getY());
	}

	public Vector2(Coordinate from, Coordinate to)
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

	@Override
	public String toString()
	{
		return String.format("%f,%f", x, y);
	}

	public Vector2 add(Vector2 other)
	{
		return new Vector2(x + other.x, y + other.y);
	}

	public Vector2 sub(Vector2 other)
	{
		return new Vector2(x - other.x, y - other.y);
	}

	public Vector2 mult(double lambda)
	{
		return new Vector2(x * lambda, y * lambda);
	}

	public double dotProduct(Vector2 other)
	{
		return x * other.x + y * other.y;
	}

	public double norm()
	{
		return Math.sqrt(x * x + y * y);
	}

	public Vector2 normalized()
	{
		return mult(1.0 / this.norm());
	}

	public Vector2 perpendicularLeft()
	{
		return new Vector2(-y, x);
	}

	public Vector2 perpendicularRight()
	{
		return new Vector2(y, -x);
	}

}
