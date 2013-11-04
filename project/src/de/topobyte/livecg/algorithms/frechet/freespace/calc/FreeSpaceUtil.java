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
package de.topobyte.livecg.algorithms.frechet.freespace.calc;

import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.livecg.util.DoubleUtil;

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
		Vector2 a = segP.getStart();
		Vector2 b = segP.getDirection();
		Vector2 c = segQ.getStart();
		Vector2 d = segQ.getDirection();
		return freeSpace(a, b, c, d, f, epsilon);
	}

	private static Interval freeSpace(Vector2 a, Vector2 b, Vector2 c, Vector2 d,
			double f, double epsilon)
	{
		// P(x) = a + x * b, Q(y) = c + y * d
		// || P(x) - Q(y) || <= epsilon
		// Now x is fixed with x = f, such that
		// || P(f) - Q(y) || <= epsilon
		// || a + f * b - c + y * d || <= epsilon
		// || a - c + f * b + y * d || <= epsilon
		// We solve this for y and get y1 and y2
		Vector2 m = a.sub(c).add(b.mult(f));
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
		double y1 = ha - rhb;
		double y2 = ha + rhb;
		return new Interval(y1, y2);
	}

	public static Interval reachableL(Interval LRij, Interval BRij,
			Interval LFi1j, Interval BFij1)
	{
		// If neither LR_i,j nor BR_i,j exists, nothing is reachable
		if (LRij == null && BRij == null) {
			return null;
		}
		// If something at the bottom is reachable, LF_i+1,j is completely
		// reachable
		if (BRij != null) {
			return createInterval(LFi1j.getStart(), LFi1j.getEnd());
		}
		// If only something at the left side is reachable:
		double start = LFi1j.getStart();
		double end = LFi1j.getEnd();
		if (LRij.getStart() > end) {
			return null;
		}
		if (LRij.getStart() > start) {
			start = LRij.getStart();
		}
		return createInterval(start, end);
	}

	public static Interval reachableB(Interval LRij, Interval BRij,
			Interval LFi1j, Interval BFij1)
	{
		// If neither LR_i,j nor BR_i,j exists, nothing is reachable
		if (LRij == null && BRij == null) {
			return null;
		}
		// If something at the left side is reachable, BF_i,j+1 is completely
		// reachable
		if (LRij != null) {
			return createInterval(BFij1.getStart(), BFij1.getEnd());
		}
		// If only something at the bottom is reachable:
		double start = BFij1.getStart();
		double end = BFij1.getEnd();
		if (BRij.getStart() > end) {
			return null;
		}
		if (BRij.getStart() > start) {
			start = BRij.getStart();
		}
		return createInterval(start, end);
	}

	private static Interval createInterval(double start, double end)
	{
		if (!DoubleUtil.isValid(start) || !DoubleUtil.isValid(end)) {
			return null;
		}
		if (start < 0 && end < 0) {
			return null;
		}
		if (start > 1 && end > 1) {
			return null;
		}
		return new Interval(start, end);
	}

}
