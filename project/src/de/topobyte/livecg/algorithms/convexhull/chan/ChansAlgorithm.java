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
import java.util.Map.Entry;

import de.topobyte.livecg.core.AlgorithmWatcher;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class ChansAlgorithm
{

	/*
	 * Watchers that need to be notified once the algorithm moved to a new
	 * state.
	 */
	private List<AlgorithmWatcher> watchers = new ArrayList<AlgorithmWatcher>();

	private List<Polygon> polygons;
	private Map<Polygon, Node> leftMostNodes = new HashMap<Polygon, Node>();
	private Map<Polygon, Integer> leftMostNodesIndices = new HashMap<Polygon, Integer>();

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
			int index = computeLeftMostPoint(polygon);
			leftMostNodesIndices.put(polygon, index);
			Node node = polygon.getShell().getNode(index);
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

	public void addWatcher(AlgorithmWatcher watcher)
	{
		watchers.add(watcher);
	}

	public void removeWatcher(AlgorithmWatcher watcher)
	{
		watchers.remove(watcher);
	}

	/*
	 * Algorithm status / steps
	 */

	private enum Phase {
		LOOK_FOR_TANGENTS, TANGENT_FOUND
	}

	private Phase phase = Phase.LOOK_FOR_TANGENTS;
	private int polygonId = 0;

	public void nextStep()
	{
		if (phase == Phase.LOOK_FOR_TANGENTS) {
			Polygon polygon = polygons.get(polygonId);
			int index = leftMostNodesIndices.get(polygon);
			System.out.println("leftmost: " + index);
		} else if (phase == Phase.TANGENT_FOUND) {

		}
		notifyWatchers();
	}

	public void previousStep()
	{
		// TODO: implement
		notifyWatchers();
	}

	/*
	 * Internal
	 */

	private void notifyWatchers()
	{
		for (AlgorithmWatcher watcher : watchers) {
			watcher.updateAlgorithmStatus();
		}
	}

	private int computeLeftMostPoint(Polygon polygon)
	{
		int leftMostNodeIndex = -1;
		double x = Double.MAX_VALUE;
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			if (node.getCoordinate().getX() < x) {
				leftMostNodeIndex = i;
				x = node.getCoordinate().getX();
			}
		}
		return leftMostNodeIndex;
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
