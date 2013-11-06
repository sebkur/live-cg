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
package de.topobyte.livecg.util.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph<T, E>
{

	private Set<T> nodes = new HashSet<T>();

	private Map<T, Set<Edge<T, E>>> edgesOut = new HashMap<T, Set<Edge<T, E>>>();
	private Map<T, Set<Edge<T, E>>> edgesIn = new HashMap<T, Set<Edge<T, E>>>();

	public void addNode(T node)
	{
		nodes.add(node);
		edgesOut.put(node, new HashSet<Edge<T, E>>());
		edgesIn.put(node, new HashSet<Edge<T, E>>());
	}

	public void removeNode(T node)
	{
		nodes.remove(node);

		Set<Edge<T, E>> in = edgesIn.get(node);
		Set<Edge<T, E>> out = edgesOut.get(node);
		edgesIn.remove(node);
		edgesOut.remove(node);
		for (Edge<T, E> edge : out) {
			edgesIn.get(edge.getTarget()).remove(edge);
		}
		for (Edge<T, E> edge : in) {
			edgesOut.get(edge.getSource()).remove(edge);
		}
	}

	public void addEdge(T from, T to, E data)
	{
		Edge<T, E> edge = new Edge<T, E>(from, to, data);
		edgesOut.get(from).add(edge);
		edgesIn.get(to).add(edge);
	}

	public Collection<T> getNodes()
	{
		return nodes;
	}

	public Set<Edge<T, E>> getEdgesOut(T node)
	{
		return edgesOut.get(node);
	}

	public Set<Edge<T, E>> getEdgesIn(T node)
	{
		return edgesIn.get(node);
	}

	public int degreeIn(T node)
	{
		return getEdgesIn(node).size();
	}

	public int degreeOut(T node)
	{
		return getEdgesOut(node).size();
	}
}
