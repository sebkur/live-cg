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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class ChansAlgorithm
{

	private List<Polygon> polygons;
	private Map<Polygon, Node> leftMostNodes = new HashMap<Polygon, Node>();

	private Polygon leftMostPolygon;
	private Node leftMostNode;

	public ChansAlgorithm(List<Polygon> polygons)
	{
		this.polygons = polygons;
		computeLeftMostPoints();
		leftMostPolygon = computeOverallLeftMostPoint();
		leftMostNode = leftMostNodes.get(leftMostPolygon);
	}

	public List<Polygon> getPolygons()
	{
		return polygons;
	}

	public Map<Polygon, Node> getLeftMostNodes()
	{
		return leftMostNodes;
	}

	private void computeLeftMostPoints()
	{
		for (Polygon polygon : polygons) {
			Node node = computeLeftMostPoint(polygon);
			leftMostNodes.put(polygon, node);
		}
	}

	public Polygon getLeftMostPolygon()
	{
		return leftMostPolygon;
	}

	public Node getLeftMostNode()
	{
		return leftMostNode;
	}

	/*
	 * Internal
	 */

	private Node computeLeftMostPoint(Polygon polygon)
	{
		Node leftMostNode = null;
		double x = Double.MAX_VALUE;
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			if (node.getCoordinate().getX() < x) {
				leftMostNode = node;
				x = node.getCoordinate().getX();
			}
		}
		return leftMostNode;
	}

	private Polygon computeOverallLeftMostPoint()
	{
		Polygon leftMostPolygon = null;
		double x = Double.MAX_VALUE;
		for (Entry<Polygon, Node> entry : leftMostNodes.entrySet()) {
			Node node = entry.getValue();
			if (node.getCoordinate().getX() < x) {
				leftMostPolygon = entry.getKey();
				x = node.getCoordinate().getX();
			}
		}
		return leftMostPolygon;
	}
}
