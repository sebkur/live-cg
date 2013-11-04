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

import de.topobyte.livecg.core.lina.Vector2;

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

	public static boolean contains(Rectangle rectangle, Coordinate c)
	{
		double x1 = Math.min(rectangle.getX1(), rectangle.getX2());
		double x2 = Math.max(rectangle.getX1(), rectangle.getX2());
		double y1 = Math.min(rectangle.getY1(), rectangle.getY2());
		double y2 = Math.max(rectangle.getY1(), rectangle.getY2());
		return c.getX() >= x1 && c.getX() <= x2 && c.getY() >= y1
				&& c.getY() <= y2;
	}

	// return the angle formed by coordinate sequence (cPre, c, cSuc)
	public static double angle(Coordinate c, Coordinate cPre, Coordinate cSuc)
	{
		Vector2 v1 = new Vector2(cPre.getX() - c.getX(), cPre.getY() - c.getY());
		Vector2 v2 = new Vector2(cSuc.getX() - c.getX(), cSuc.getY() - c.getY());
		double dotProduct = v1.dotProduct(v2);
		double cosAngle = dotProduct / (v1.norm() * v2.norm());
		double angle = Math.acos(cosAngle);
		double det = determinant(c, cPre, cSuc);
		if (det > 0) {
			angle = Math.PI * 2 - angle;
		}
		return angle;
	}

	private static double determinant(Coordinate c, Coordinate cPre,
			Coordinate cSuc)
	{
		double det = (c.getX() - cPre.getX()) * (cSuc.getY() - cPre.getY())
				- (c.getY() - cPre.getY()) * (cSuc.getX() - cPre.getX());
		return det;
	}

	// return whether c is right of a -> b
	public static boolean isRightOf(Coordinate a, Coordinate b, Coordinate c)
	{
		double det = (a.getX() - c.getX()) * (b.getY() - c.getY())
				- (b.getX() - c.getX()) * (a.getY() - c.getY());
		return det > 0;
	}

	// return whether c is left of a -> b
	public static boolean isLeftOf(Coordinate a, Coordinate b, Coordinate c)
	{
		double det = (a.getX() - c.getX()) * (b.getY() - c.getY())
				- (b.getX() - c.getX()) * (a.getY() - c.getY());
		return det < 0;
	}

	// return whether c is on a -> b
	public static boolean isOn(Coordinate a, Coordinate b, Coordinate c)
	{
		double det = (a.getX() - c.getX()) * (b.getY() - c.getY())
				- (b.getX() - c.getX()) * (a.getY() - c.getY());
		return det == 0;
	}

}
