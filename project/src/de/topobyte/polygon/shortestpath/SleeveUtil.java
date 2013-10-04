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
package de.topobyte.polygon.shortestpath;

import java.util.List;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.polygon.monotonepieces.Diagonal;

public class SleeveUtil
{

	public static void optimizePath(Sleeve sleeve, Node nodeStart,
			Node nodeTarget)
	{
		List<Polygon> path = sleeve.getPolygons();
		List<Diagonal> diagonals = sleeve.getDiagonals();

		Polygon triangleStart = path.get(0);
		Polygon triangleTarget = path.get(path.size() - 1);

		if (isOnCorner(nodeStart, triangleStart)) {
			while (true) {
				Polygon second = path.get(1);
				if (isOnCorner(nodeStart, second)) {
					triangleStart = second;
					path.remove(0);
					diagonals.remove(0);
					continue;
				}
				break;
			}
		}
		if (isOnCorner(nodeTarget, triangleTarget)) {
			while (true) {
				Polygon beforeLast = path.get(path.size() - 2);
				if (isOnCorner(nodeTarget, beforeLast)) {
					triangleTarget = beforeLast;
					path.remove(path.size() - 1);
					diagonals.remove(diagonals.size() - 1);
					continue;
				}
				break;
			}
		}
	}

	private static boolean isOnCorner(Node node, Polygon triangle)
	{
		Chain shell = triangle.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node n = shell.getNode(i);
			if (n == node) {
				return true;
			}
		}
		return false;
	}

}
