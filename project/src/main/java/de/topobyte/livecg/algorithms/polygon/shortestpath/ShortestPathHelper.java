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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.algorithms.convexhull.ConvexHullOperation;
import de.topobyte.livecg.algorithms.farthestpair.FarthestPairResult;
import de.topobyte.livecg.algorithms.farthestpair.ShamosFarthestPairOperation;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class ShortestPathHelper
{

	public static PairOfNodes determineGoodNodes(Polygon polygon)
	{
		List<Polygon> list = new ArrayList<>();
		list.add(polygon);
		Polygon convexHull = ConvexHullOperation.compute(null, null, list);
		FarthestPairResult farthestPair = ShamosFarthestPairOperation
				.compute(convexHull.getShell());
		Coordinate c0 = convexHull.getShell()
				.getCoordinate(farthestPair.getI());
		Coordinate c1 = convexHull.getShell()
				.getCoordinate(farthestPair.getJ());

		Chain shell = polygon.getShell();
		Node start = findNearest(shell, c0);
		Node target = findNearest(shell, c1);
		return new PairOfNodes(start, target);
	}

	private static Node findNearest(Chain shell, Coordinate c)
	{
		Node nearest = null;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			double d = node.getCoordinate().distance(c);
			if (d < min) {
				min = d;
				nearest = node;
			}
		}
		return nearest;
	}
}
