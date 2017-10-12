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

import de.topobyte.viewports.geometry.Rectangle;

public class Rectangles
{
	public static Rectangle extend(Rectangle r, double extent)
	{
		double xmin = r.getX1();
		double xmax = r.getX2();
		double ymin = r.getY1();
		double ymax = r.getY2();
		xmin -= extent;
		xmax += extent;
		ymin -= extent;
		ymax += extent;
		return new Rectangle(xmin, ymin, xmax, ymax);
	}

	public static Rectangle union(Rectangle a, Rectangle b)
	{
		if (a == null && b == null) {
			return null;
		}
		if (a == null) {
			return new Rectangle(b.getX1(), b.getY1(), b.getX2(), b.getY2());
		}
		if (b == null) {
			return new Rectangle(a.getX1(), a.getY1(), a.getX2(), a.getY2());
		}
		double xmin = Math.min(a.getX1(), b.getX1());
		double xmax = Math.max(a.getX2(), b.getX2());
		double ymin = Math.min(a.getY1(), b.getY1());
		double ymax = Math.max(a.getY2(), b.getY2());
		return new Rectangle(xmin, ymin, xmax, ymax);
	}
}
