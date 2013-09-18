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

public class GeomMath
{
	// return the squared distance between points (vx, vy) and (wx, wy)
	public static double squaredDistance(double vx, double vy, double wx,
			double wy)
	{
		return (vx - wx) * (vx - wx) + (vy - wy) * (vy - wy);
	}

	// return the squared distance between point c and segment (c1, c2)
	public static double squaredDistance(Coordinate c, Coordinate c1,
			Coordinate c2)
	{
		double x = c.getX();
		double y = c.getY();

		double sx1 = c1.getX();
		double sy1 = c1.getY();
		double sx2 = c2.getX();
		double sy2 = c2.getY();

		double squaredLen = squaredDistance(sx1, sy1, sx2, sy2);
		if (squaredLen == 0) {
			double d = squaredDistance(x, y, sx1, sy1);
			return d;
		}
		double t = ((x - sx1) * (sx2 - sx1) + (y - sy1) * (sy2 - sy1))
				/ squaredLen;
		if (t < 0) {
			double d = squaredDistance(x, y, sx1, sy1);
			return d;
		} else if (t > 1) {
			double d = squaredDistance(x, y, sx2, sy2);
			return d;
		} else {
			double tx = sx1 + t * (sx2 - sx1);
			double ty = sy1 + t * (sy2 - sy1);
			double d = squaredDistance(x, y, tx, ty);
			return d;
		}
	}

}
