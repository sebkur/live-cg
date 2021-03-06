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
import java.util.Collections;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Node;

public class Path
{

	private List<Node> nodes = new ArrayList<>();

	public Path(Node start, Node node)
	{
		nodes.add(start);
		nodes.add(node);
	}

	public List<Node> getNodes()
	{
		return Collections.unmodifiableList(nodes);
	}

	public int length()
	{
		return nodes.size();
	}

	public void add(Node node)
	{
		nodes.add(node);
	}

	public Node getNode(int i)
	{
		return nodes.get(i);
	}

	public Node lastNode()
	{
		return nodes.get(nodes.size() - 1);
	}

	public void removeFirst()
	{
		nodes.remove(0);
	}

	public void removeLast()
	{
		nodes.remove(nodes.size() - 1);
	}

	public void clear()
	{
		nodes.clear();
	}

}
