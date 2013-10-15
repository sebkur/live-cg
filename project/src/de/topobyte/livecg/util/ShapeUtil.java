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
package de.topobyte.livecg.util;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

public class ShapeUtil
{

	public static Arc2D createArc(double x, double y, double r)
	{
		Arc2D arc = new Arc2D.Double(x - r, y - r, r * 2, r * 2, 0, 360,
				Arc2D.CHORD);
		return arc;
	}

	public static Arc2D createArc(double x, double y, double w, double h)
	{
		Arc2D arc = new Arc2D.Double(x - w / 2, y - h / 2, w, h, 0, 360,
				Arc2D.CHORD);
		return arc;
	}

	public static Shape createRing(double x, double y, double r, double w)
	{
		double rpw = r + w / 2;
		double rmw = r - w / 2;
		Arc2D outerArc = new Arc2D.Double(x - rpw, y - rpw, r * 2 + w, r * 2
				+ w, 0, 360, Arc2D.CHORD);
		Arc2D innerArc = new Arc2D.Double(x - rmw, y - rmw, r * 2 - w, r * 2
				- w, 0, 360, Arc2D.CHORD);

		Area outer = new Area(outerArc);
		Area inner = new Area(innerArc);
		outer.subtract(inner);

		return outer;
	}
}
