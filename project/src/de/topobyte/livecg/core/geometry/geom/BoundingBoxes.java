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

public class BoundingBoxes
{

	public static Rectangle get(Rectangle a, Rectangle b)
	{
		double xmin = Math.min(a.getX1(), b.getX1());
		double xmax = Math.min(a.getX2(), b.getX2());
		double ymin = Math.min(a.getY1(), b.getY1());
		double ymax = Math.min(a.getY2(), b.getY2());
		return new Rectangle(xmin, ymin, xmax, ymax);
	}

	public static Rectangle get(Chain chain)
	{
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			if (c.getX() < xmin) {
				xmin = c.getX();
			}
			if (c.getX() > xmax) {
				xmax = c.getX();
			}
			if (c.getY() < ymin) {
				ymin = c.getY();
			}
			if (c.getY() > ymax) {
				ymax = c.getY();
			}
		}
		return new Rectangle(xmin, ymin, xmax, ymax);
	}

	public static Rectangle get(Polygon polygon)
	{
		return get(polygon.getShell());
	}

}
