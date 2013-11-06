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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.graph.Edge;

public class Graph extends
		de.topobyte.livecg.util.graph.Graph<Polygon, Diagonal>
{
	final static Logger logger = LoggerFactory.getLogger(Graph.class);

	public Graph(Polygon polygon)
	{
		addNode(polygon);
	}

	public void replace(Polygon polygon, Polygon a, Polygon b, Diagonal diagonal)
	{
		Set<Edge<Polygon, Diagonal>> out = getEdgesOut(polygon);

		addNode(a);
		addNode(b);
		addEdge(a, b, diagonal);
		addEdge(b, a, diagonal);

		for (Edge<Polygon, Diagonal> edge : out) {
			Polygon target = edge.getTarget();
			Diagonal d = edge.getData();
			Polygon source = a;
			if (containsBoth(b.getShell(), d.getA(), d.getB())) {
				source = b;
			}
			addEdge(source, target, d);
			addEdge(target, source, d);
		}

		removeNode(polygon);
	}

	private static boolean containsBoth(Chain chain, Node a, Node b)
	{
		boolean foundA = false, foundB = false;
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			foundA |= chain.getNode(i) == a;
			foundB |= chain.getNode(i) == b;
		}
		return foundA && foundB;
	}
}
