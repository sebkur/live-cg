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

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;

public class SitePoint extends EventPoint
{

	public SitePoint(Point point)
	{
		super(point);
	}

	public SitePoint(double x, double y)
	{
		super(x, y);
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof SitePoint)) {
			return false;
		}
		SitePoint o = (SitePoint) other;
		return o.getX() == getX() && o.getY() == getY();
	}
}
