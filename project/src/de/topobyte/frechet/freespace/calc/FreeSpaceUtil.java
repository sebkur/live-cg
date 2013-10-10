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
package de.topobyte.frechet.freespace.calc;

public class FreeSpaceUtil
{

	/**
	 * Compute the parameter interval of a segment q specifying the free space
	 * for a given, fixed parameter of a segment p, specified by parameter 'f'
	 * and a fixed epsilon.
	 */
	public static Interval freeSpace(LineSegment segP, LineSegment segQ,
			double f, double epsilon)
	{
		Vector a = segP.getStart();
		Vector b = segP.getDirection();
		Vector c = segQ.getStart();
		Vector d = segQ.getDirection();
		return freeSpace(a, b, c, d, f, epsilon);
	}

	private static Interval freeSpace(Vector a, Vector b, Vector c, Vector d,
			double f, double epsilon)
	{
		// P(x) = a + x * b, Q(y) = c + y * d
		// || P(x) - Q(y) || <= epsilon
		// Now x is fixed with x = f, such that
		// || P(f) - Q(y) || <= epsilon
		// || a + f * b - c + y * d || <= epsilon
		// || a - c + f * b + y * d || <= epsilon
		// We solve this for y and get y1 and y2
		Vector m = a.sub(c).add(b.mult(f));
		double mxdx = m.getX() * d.getX();
		double mydy = m.getY() * d.getY();
		double dx2 = d.getX() * d.getX();
		double dy2 = d.getY() * d.getY();
		double mx2 = m.getX() * m.getX();
		double my2 = m.getY() * m.getY();
		double eps2 = epsilon * epsilon;
		double ha = (mxdx + mydy) / (dx2 + dy2);
		double hb = (ha * ha) - ((mx2 + my2 - eps2) / (dx2 + dy2));
		double rhb = Math.sqrt(hb);
		double y1 = ha + rhb;
		double y2 = ha - rhb;
		return new Interval(y1, y2);
	}
}
