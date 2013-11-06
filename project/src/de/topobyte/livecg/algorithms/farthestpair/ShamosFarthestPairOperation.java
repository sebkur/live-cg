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
package de.topobyte.livecg.algorithms.farthestpair;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;

public class ShamosFarthestPairOperation
{
	private Chain polygon;
	private int maxP = -1;
	private int maxQ = -1;
	private double max;

	public static FarthestPairResult compute(Chain polygon)
	{
		ShamosFarthestPairOperation operation = new ShamosFarthestPairOperation(
				polygon);
		operation.execute();
		return new FarthestPairResult(operation.maxP, operation.maxQ,
				Math.sqrt(operation.max));
	}

	private ShamosFarthestPairOperation(Chain polygon)
	{
		this.polygon = polygon;
		if (!PolygonHelper.isCounterClockwiseOriented(polygon)) {
			try {
				this.polygon = ChainHelper.invert(polygon);
			} catch (CloseabilityException e) {
				// should not happen
			}
		}
	}

	private void execute()
	{
		max = 0.0;
		int pn = polygon.getNumberOfNodes() - 1;
		int p = pn; // p = n
		int q = next(next(p)); // q = 1
		// Locate first antipodal pair
		while (area(p, next(p), next(q)) > area(p, next(p), q)) {
			q = next(q);
		}
		int q0 = q;
		// Walk in CCW order along polygon
		while (q != 0) {
			p = next(p);
			check(p, q);
			while (area(p, next(p), next(q)) > area(p, next(p), q)) {
				q = next(q);
				if (p == q0 && q == 0) {
					return;
				} else {
					check(p, q);
				}
			}
			// Handle parallel edges
			if (area(p, next(p), next(q)) == area(p, next(p), q)) {
				if (p == q0 && q == pn) {
					return;
				} else {
					check(p, next(q));
				}
			}
		}
	}

	private int next(int i)
	{
		return (i + 1) % polygon.getNumberOfNodes();
	}

	private void check(int i, int j)
	{
		double d = distance2(polygon.getCoordinate(i), polygon.getCoordinate(j));

		if (d > max) {
			max = d;
			maxP = i;
			maxQ = j;
		}
	}

	private double area(int i, int j, int k)
	{
		Coordinate ci = polygon.getCoordinate(i);
		Coordinate cj = polygon.getCoordinate(j);
		Coordinate ck = polygon.getCoordinate(k);
		return area(ci, cj, ck);
	}

	public double area(Coordinate c1, Coordinate c2, Coordinate c3)
	{
		return -(c2.getX() - c1.getX()) * (c3.getY() - c1.getY())
				+ (c3.getX() - c1.getX()) * (c2.getY() - c1.getY());
	}

	public double distance2(Coordinate c1, Coordinate c2)
	{
		double d1 = c1.getX() - c2.getX();
		double d2 = c1.getY() - c2.getY();
		return d1 * d1 + d2 * d2;
	}
}