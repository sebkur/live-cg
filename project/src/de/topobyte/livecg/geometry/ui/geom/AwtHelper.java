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
package de.topobyte.livecg.geometry.ui.geom;

import java.awt.geom.Area;
import java.awt.geom.Path2D;

public class AwtHelper
{
	public static Area toShape(Polygon p)
	{
		if (p.isEmpty()) {
			return new Area();
		}

		Editable shell = p.getShell();
		Area outer = getArea(shell);

//		for (int i = 0; i < p.getNumberOfInteriorRings(); i++) {
//			LinearRing interior = p.getInteriorRing(i);
//			Area inner = getArea(interior);
//			outer.subtract(inner);
//		}

		return outer;
	}

	public static Area getArea(Editable ring)
	{
		Path2D.Double path = new Path2D.Double();
		Coordinate c = ring.getCoordinate(0);
		path.moveTo(c.getX(), c.getY());
		for (int i = 1; i < ring.getNumberOfNodes(); i++) {
			c = ring.getCoordinate(i);
			path.lineTo(c.getX(), c.getY());
		}
		path.closePath();
		return new Area(path);
	}
}
