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
package de.topobyte.livecg.util.coloring;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.topobyte.livecg.util.graph.Edge;
import de.topobyte.livecg.util.graph.Graph;

public class AlternatingColorMapBuilder
{

	public static <N, E> Map<N, Color> buildColorMap(Graph<N, E> graph,
			Color a, Color b)
	{
		Map<N, Color> map = new HashMap<>();

		N n0 = graph.getNodes().iterator().next();
		traverse(graph, n0, map, a, b);

		return map;
	}

	private static <N, E> void traverse(Graph<N, E> graph, N n,
			Map<N, Color> map, Color a, Color b)
	{
		// Assign color to current node
		map.put(n, a);
		// Recurse to neighbors
		Set<Edge<N, E>> edges = graph.getEdgesOut(n);
		for (Edge<N, E> edge : edges) {
			N neighbor = edge.getTarget();
			if (!map.containsKey(neighbor)) {
				// Invert colors
				traverse(graph, neighbor, map, b, a);
			}
		}
	}

}
