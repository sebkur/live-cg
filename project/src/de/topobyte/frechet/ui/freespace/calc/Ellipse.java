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

package de.topobyte.frechet.ui.freespace.calc;

public class Ellipse
{

	private static boolean DEBUG = false;

	private final double theta;
	private final double a;
	private final double b;
	private final double c;
	private final double d;
	private final double e;
	private final double f;

	public Ellipse(double a, double b, double c, double d, double e, double f)
	{
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;

		double arctan2alpha = 2 * b / (a - c);
		theta = Math.atan(arctan2alpha) / 2.0;

		if (!DEBUG) {
			return;
		}

		System.out.println("theta: " + theta);

		double cos = Math.cos(theta);
		double sin = Math.sin(theta);

		double a$ = a * cos * cos + b * cos * sin + c * sin * sin;
		double c$ = a * sin * sin - b * cos * sin + c * cos * cos;
		double d$ = d * cos + e * sin;
		double e$ = -d * sin + e * cos;
		double f$ = f;

		System.out.println(String.format("%f, %f, %f, %f, %f, %f", a, b, c, d,
				e, f));
		System.out.println(String.format("%f, %f, %f, %f, %f", a$, c$, d$, e$,
				f$));

		double a1 = Math.sqrt(Math.abs(a$ / -f$));
		double c1 = Math.sqrt(Math.abs(c$ / -f$));
		System.out.println(String.format("%f, %f", a1, c1));

		// double lambda1 = (a + c) / 2 + Math.sqrt((a - c) * (a - c) / 4 + b *
		// b);
		// double lambda2 = (a + c) / 2 - Math.sqrt((a - c) * (a - c) / 4 + b *
		// b);
		//
		// System.out.println("lambda1: " + lambda1);
		// System.out.println("lambda2: " + lambda2);
		//
		// Matrix A = new Matrix(2, 2);
		// A.setValue(0, 0, a);
		// A.setValue(0, 1, b);
		// A.setValue(1, 0, b);
		// A.setValue(1, 1, c);
		//
		// Matrix E = new Matrix(2, 2);
		// E.setValue(0, 0, 1);
		// E.setValue(1, 1, 1);
		//
		// Matrix L1 = E.multiply(lambda1);
		// Matrix M1 = A.subtract(L1);
		// Matrix L2 = E.multiply(lambda2);
		// Matrix M2 = A.subtract(L2);
		// System.out.println("L1:");
		// System.out.println(L1);
		// System.out.println("M1:");
		// System.out.println(M1);
		// try {
		// Matrix S1 = M1.solve(new Vector(2, VectorType.Column));
		// Matrix S2 = M2.solve(new Vector(2, VectorType.Column));
		// } catch (RuntimeException ex) {
		// System.out.println("unable to solve" + ex.getMessage());
		// }

	}

	public double getA()
	{
		return a;
	}

	public double getB()
	{
		return b;
	}

	public double getC()
	{
		return c;
	}

	public double getD()
	{
		return d;
	}

	public double getE()
	{
		return e;
	}

	public double getF()
	{
		return f;
	}

	public double getTheta()
	{
		return theta;
	}

}
