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
package de.topobyte.livecg.algorithms.polygon.monotonepieces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.graph.Edge;
import de.topobyte.livecg.util.graph.Graph;

public class PolygonGraphUtil
{

	public static Graph<Polygon, Object> addNodeEdges(
			Graph<Polygon, Diagonal> graph)
	{
		de.topobyte.livecg.util.graph.Graph<Polygon, Object> g = new de.topobyte.livecg.util.graph.Graph<Polygon, Object>();
		// Add nodes from graph to g
		for (Polygon p : graph.getNodes()) {
			g.addNode(p);
		}
		// Add edges from graph to g
		for (Polygon p : graph.getNodes()) {
			Set<Edge<Polygon, Diagonal>> edges = graph.getEdgesOut(p);
			for (Edge<Polygon, Diagonal> edge : edges) {
				g.addEdge(p, edge.getTarget(), edge.getData());
			}
		}
		// Create new edges for connecting nodes
		Map<Node, List<Polygon>> map = new HashMap<Node, List<Polygon>>();
		for (Polygon p : graph.getNodes()) {
			Chain shell = p.getShell();
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Node node = shell.getNode(i);
				List<Polygon> polygons = map.get(node);
				if (polygons == null) {
					polygons = new ArrayList<Polygon>();
					map.put(node, polygons);
				}
				polygons.add(p);
			}
		}
		for (Entry<Node, List<Polygon>> entry : map.entrySet()) {
			List<Polygon> polygons = entry.getValue();
			if (polygons.size() > 1) {
				for (Polygon p : polygons) {
					for (Polygon q : polygons) {
						if (p == q) {
							continue;
						}
						g.addEdge(p, q, entry.getKey());
					}
				}
			}
		}
		return g;
	}

}
