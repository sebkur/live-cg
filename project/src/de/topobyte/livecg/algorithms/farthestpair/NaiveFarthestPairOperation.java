/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.algorithms.farthestpair;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;

public class NaiveFarthestPairOperation
{
	private Chain polygon;
	private int maxP = -1;
	private int maxQ = -1;
	private double max;

	public static FarthestPairResult compute(Chain polygon)
	{
		NaiveFarthestPairOperation operation = new NaiveFarthestPairOperation(
				polygon);
		operation.execute();
		return new FarthestPairResult(operation.maxP, operation.maxQ,
				Math.sqrt(operation.max));
	}

	private NaiveFarthestPairOperation(Chain polygon)
	{
		this.polygon = polygon;
	}

	private void execute()
	{
		max = 0.0;
		for (int i = 0; i < polygon.getNumberOfNodes(); i++) {
			for (int j = 0; j < polygon.getNumberOfNodes(); j++) {
				if (i == j) {
					continue;
				}
				Coordinate ci = polygon.getCoordinate(i);
				Coordinate cj = polygon.getCoordinate(j);
				double d = ci.distance(cj);
				if (d > max) {
					max = d;
					maxP = i;
					maxQ = j;
				}
			}
		}
	}

}