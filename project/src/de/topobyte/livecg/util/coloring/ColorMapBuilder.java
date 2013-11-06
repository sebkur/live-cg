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
package de.topobyte.livecg.util.coloring;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.color.util.HSLColor;
import de.topobyte.livecg.util.graph.Edge;
import de.topobyte.livecg.util.graph.Graph;

public class ColorMapBuilder
{

	public static <N, E> Map<N, Color> buildColorMap(Graph<N, E> graph)
	{
		Map<N, Float> hues = new HashMap<N, Float>();
		Map<N, Color> map = new HashMap<N, Color>();

		float s = 90, l = 50;

		N n0 = graph.getNodes().iterator().next();
		traverse(graph, n0, hues);

		for (N n : graph.getNodes()) {
			float h = hues.get(n);
			HSLColor hsl = new HSLColor(h, s, l);
			Color color = hsl.getRGB();
			map.put(n, color);
		}

		return map;
	}

	private static <N, E> void traverse(Graph<N, E> graph, N n,
			Map<N, Float> hues)
	{
		// Build list of neighbors' hue values
		List<Float> neighborHues = new ArrayList<Float>();
		Set<Edge<N, E>> edges = graph.getEdgesOut(n);
		for (Edge<N, E> edge : edges) {
			N neighbor = edge.getTarget();
			if (hues.containsKey(neighbor)) {
				float hue = hues.get(neighbor);
				neighborHues.add(hue);
			}
		}
		// Pick the best color based on the neighbors
		hues.put(n, pickBest(neighborHues));
		// Recurse to neighbors
		for (Edge<N, E> edge : edges) {
			N neighbor = edge.getTarget();
			if (!hues.containsKey(neighbor)) {
				traverse(graph, neighbor, hues);
			}
		}
	}

	private static float pickBest(List<Float> hues)
	{
		// Handle the anchor case with no neighbors with already assigned value
		if (hues.size() == 0) {
			return 30;
		}
		// Otherwise find the best fit
		Collections.sort(hues);

		// We always compute the gaps before the i'th element.
		// Start with gap before first element
		float first = hues.get(0);
		float last = hues.get(hues.size() - 1);
		float longestGap = first + (359 - last);
		int best = 0;
		// Continue with the other gaps
		for (int i = 1; i < hues.size(); i++) {
			float prev = hues.get(i - 1);
			float ith = hues.get(i);
			float gap = ith - prev;
			if (gap > longestGap) {
				longestGap = gap;
				best = i;
			}
		}
		// Compute actual value
		float h = hues.get(best) - longestGap / 2;
		if (h < 0) {
			h += 360;
		}
		return h;
	}
}
