/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

package de.topobyte.frechet.ui.frechet.calc;

public class Calculator
{

	public static Ellipse calc(LineSegment seg1, LineSegment seg2,
			double epsilon)
	{
		Vector a = seg1.getDirection();
		Vector b = seg1.getStart();

		Vector c = seg2.getDirection();
		Vector d = seg2.getStart();

		double ax = a.getX();
		double bx = b.getX();
		double cx = c.getX();
		double dx = d.getX();
		double ay = a.getY();
		double by = b.getY();
		double cy = c.getY();
		double dy = d.getY();

		double bx_dx = bx - dx;
		double by_dy = by - dy;

		double A = ax * ax + ay * ay;
		double C = cx * cx + cy * cy;
		double B = -(ax * cx + ay * cy);
		double D = (ax * bx_dx + ay * by_dy);
		double E = -(cx * bx_dx + cy * by_dy);
		double F = bx_dx * bx_dx + by_dy * by_dy - epsilon * epsilon;

		return new Ellipse(A, B, C, D, E, F);
	}
}
