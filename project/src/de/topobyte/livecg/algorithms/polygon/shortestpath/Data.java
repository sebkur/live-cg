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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Node;

public class Data
{

	private List<Node> commonPath = new ArrayList<Node>();
	private List<Node> leftPath = new ArrayList<Node>();
	private List<Node> rightPath = new ArrayList<Node>();
	private Map<Side, List<Node>> funnelChains = new HashMap<Side, List<Node>>();

	private Data()
	{
		// intentionally empty
	}

	public Data(Node start, Node left, Node right)
	{
		commonPath.add(start);
		leftPath.add(left);
		rightPath.add(right);
		funnelChains.put(Side.LEFT, leftPath);
		funnelChains.put(Side.RIGHT, rightPath);
	}

	@Override
	public Data clone()
	{
		Data copy = new Data();
		copy.commonPath = clone(commonPath);
		copy.leftPath = clone(leftPath);
		copy.rightPath = clone(rightPath);
		copy.funnelChains.put(Side.LEFT, copy.leftPath);
		copy.funnelChains.put(Side.RIGHT, copy.rightPath);
		return copy;
	}

	private List<Node> clone(List<Node> list)
	{
		List<Node> copy = new ArrayList<Node>();
		for (Node n : list) {
			copy.add(n);
		}
		return copy;
	}

	public Node getApex()
	{
		return commonPath.get(commonPath.size() - 1);
	}

	public int getCommonLength()
	{
		return commonPath.size();
	}

	public int getFunnelLength(Side side)
	{
		return funnelChains.get(side).size();
	}

	public void appendCommon(Node node)
	{
		commonPath.add(node);
	}

	public void append(Side side, Node node)
	{
		funnelChains.get(side).add(node);
	}

	public Node getCommon(int i)
	{
		return commonPath.get(i);
	}

	public Node getSafe(Side side, int i)
	{
		if (i == -1) {
			return getApex();
		}
		return funnelChains.get(side).get(i);
	}

	public Node get(Side side, int i)
	{
		return funnelChains.get(side).get(i);
	}

	public Node removeFirst(Side side)
	{
		List<Node> path = funnelChains.get(side);
		return path.remove(0);
	}

	public Node removeLast(Side side)
	{
		List<Node> path = funnelChains.get(side);
		return path.remove(path.size() - 1);
	}

	public void clear(Side side)
	{
		List<Node> path = funnelChains.get(side);
		path.clear();
	}

	public Node getLast(Side side)
	{
		List<Node> path = funnelChains.get(side);
		if (path.isEmpty()) {
			return getApex();
		}
		return path.get(path.size() - 1);
	}

}
