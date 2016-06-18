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
package de.topobyte.livecg.algorithms.convexhull.chan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.CloneUtil;

public class Data implements Cloneable
{
	private Phase phase = Phase.FIND_LEFTMOST_NODES;
	private List<Node> hull;

	private Map<Polygon, Integer> leftMostNodesIndices = new HashMap<>();

	private Polygon leftMostPolygon;
	private Node overallLeftMostNode;
	private Polygon currentHeadPolygon;

	private int polygonId = 0;
	private int position = -1;
	private Map<Integer, Integer> positions = new HashMap<>();
	private int bestPolygonId = -1;

	@Override
	public Data clone()
	{
		Data copy = null;
		try {
			copy = (Data) super.clone();
		} catch (CloneNotSupportedException e) {
			// impossible by design
		}
		copy.hull = CloneUtil.clone(hull);
		copy.leftMostNodesIndices = CloneUtil.clone(leftMostNodesIndices);
		copy.positions = CloneUtil.clone(positions);
		return copy;
	}

	/*
	 * Getters
	 */

	public int getNumberOfLeftmostNodesFound()
	{
		return leftMostNodesIndices.size();
	}

	public int getLeftMostIndex(Polygon polygon)
	{
		return leftMostNodesIndices.get(polygon);
	}

	public Node getLeftMostNode(Polygon polygon)
	{
		Integer index = leftMostNodesIndices.get(polygon);
		if (index == null) {
			return null;
		}
		return polygon.getShell().getNode(index);
	}

	public Polygon getCurrentHeadPolygon()
	{
		return currentHeadPolygon;
	}

	public Polygon getLeftMostPolygon()
	{
		return leftMostPolygon;
	}

	public Node getLeftMostNode()
	{
		return overallLeftMostNode;
	}

	public Node getOverallLeftMostNode()
	{
		return overallLeftMostNode;
	}

	public Node getCurrentNode()
	{
		return hull.get(hull.size() - 1);
	}

	public Phase getPhase()
	{
		return phase;
	}

	public int getPolygonId()
	{
		return polygonId;
	}

	public int getPosition()
	{
		return position;
	}

	public int getPosition(int polygonId)
	{
		return positions.get(polygonId);
	}

	public int getBestPolygonId()
	{
		return bestPolygonId;
	}

	public List<Node> getHull()
	{
		return hull;
	}

	/*
	 * Modifiers
	 */

	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}

	public void initializeHull()
	{
		hull = new ArrayList<>();
	}

	public void appendToHull(Node node)
	{
		hull.add(node);
	}

	public void setLeftMostNodesIndex(Polygon polygon, int index)
	{
		leftMostNodesIndices.put(polygon, index);
	}

	public void setCurrentHeadPolygon(Polygon currentHeadPolygon)
	{
		this.currentHeadPolygon = currentHeadPolygon;
	}

	public void setLeftMostPolygon(Polygon leftMostPolygon)
	{
		this.leftMostPolygon = leftMostPolygon;
	}

	public void setOverallLeftMostNode(Node overallLeftMostNode)
	{
		this.overallLeftMostNode = overallLeftMostNode;
	}

	public void setPolygonId(int polygonId)
	{
		this.polygonId = polygonId;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public void setBestPolygonId(int bestPolygonId)
	{
		this.bestPolygonId = bestPolygonId;
	}

	public void setPosition(int polygonId, int position)
	{
		positions.put(polygonId, position);
	}
}
