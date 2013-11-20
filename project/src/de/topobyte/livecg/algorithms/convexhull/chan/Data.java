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

public class Data
{
	List<Node> hull;
	Map<Polygon, Node> leftMostNodes = new HashMap<Polygon, Node>();
	Map<Polygon, Integer> leftMostNodesIndices = new HashMap<Polygon, Integer>();

	Polygon leftMostPolygon;
	Polygon currentHeadPolygon;
	Node leftMostNode;

	Phase phase = Phase.FIND_LEFTMOST_NODES;
	int polygonId = 0;
	int position = -1;
	List<Integer> positions = new ArrayList<Integer>();
	int bestPolygonId = -1;

	/*
	 * Getters
	 */

	public Map<Polygon, Node> getLeftMostNodes()
	{
		return leftMostNodes;
	}

	public Polygon getLeftMostPolygon()
	{
		return leftMostPolygon;
	}

	public Node getLeftMostNode()
	{
		return leftMostNode;
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

	public List<Integer> getPositions()
	{
		return positions;
	}

	public int getBestPolygonId()
	{
		return bestPolygonId;
	}

	public List<Node> getHull()
	{
		return hull;
	}
}
