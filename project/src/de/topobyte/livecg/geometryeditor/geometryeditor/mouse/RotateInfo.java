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
package de.topobyte.livecg.geometryeditor.geometryeditor.mouse;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeomMath;

public class RotateInfo
{
	private Coordinate center;
	private Coordinate start;
	private Coordinate last;
	private Coordinate current;

	public RotateInfo(double x, double y, double cx, double cy)
	{
		start = new Coordinate(x, y);
		center = new Coordinate(cx, cy);
		last = start;
		current = start;
	}

	public void update(double x, double y)
	{
		last = current;
		current = new Coordinate(x, y);
	}

	public double getAngleToLast()
	{
		return getAngleTo(last);
	}

	public double getAngleToStart()
	{
		return getAngleTo(start);
	}

	private double getAngleTo(Coordinate other)
	{
		return GeomMath.angle(center, other, current);
	}

	public Coordinate getCenter()
	{
		return center;
	}
}
