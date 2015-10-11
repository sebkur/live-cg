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

import java.util.Set;

import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.Stack;
import de.topobyte.livecg.util.graph.Edge;
import de.topobyte.livecg.util.graph.Graph;

public class GraphFinder
{

	public static Sleeve find(Graph<Polygon, Diagonal> graph, Polygon start,
			Polygon target)
	{
		GraphFinder finder = new GraphFinder(graph);
		finder.find(start, target);
		return new Sleeve(finder.path, finder.pathDiagonals);
	}

	private Graph<Polygon, Diagonal> graph;

	private Stack<Polygon> path = new Stack<Polygon>();
	private Stack<Diagonal> pathDiagonals = new Stack<Diagonal>();

	private GraphFinder(Graph<Polygon, Diagonal> graph)
	{
		this.graph = graph;
	}

	private void find(Polygon start, Polygon target)
	{
		// Push starting node on the stack
		path.push(start);
		// And call recursive method
		find(target);
	}

	private boolean find(Polygon target)
	{
		// Set top to the topmost element on the stack and top2 to the second
		// element from the top or null if not available
		Polygon top = path.top();
		Polygon top2 = null;
		if (path.size() > 1) {
			path.pop();
			top2 = path.top();
			path.push(top);
		}

		// Recursive Depth First Search
		Set<Edge<Polygon, Diagonal>> out = graph.getEdgesOut(top);
		for (Edge<Polygon, Diagonal> edge : out) {
			// Ignore edge pointing to the direction where we came from
			if (edge.getTarget() == top2) {
				continue;
			}
			path.push(edge.getTarget());
			pathDiagonals.push(edge.getData());
			// If we found our target, exit
			if (target == edge.getTarget()) {
				return true;
			}
			// If we are on a successful path, exit
			if (find(target)) {
				return true;
			}
			path.pop();
			pathDiagonals.pop();
		}
		return false;
	}
}
