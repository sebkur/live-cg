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
import de.topobyte.livecg.core.SceneAlgorithm;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.util.circular.IntRing;

public class ChansAlgorithm implements SceneAlgorithm
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
	private Polygon currentHeadPolygon;
	private Node leftMostNode;

	private List<Node> hull;

	public ChansAlgorithm(List<Polygon> polygons)
	{
		this.polygons = polygons;
	}

	public List<Polygon> getPolygons()
	{
		return polygons;
	}

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

	public void addWatcher(AlgorithmWatcher watcher)
	{
		watchers.add(watcher);
	}

	public void removeWatcher(AlgorithmWatcher watcher)
	{
		watchers.remove(watcher);
	}

	@Override
	public Rectangle getScene()
	{
		Rectangle scene = BoundingBoxes.get(polygons.get(0));
		for (Polygon p : polygons) {
			scene = BoundingBoxes.get(scene, BoundingBoxes.get(p));
		}
		scene = Rectangles.extend(scene, 15);
		return scene;
	}

	/*
	 * Algorithm status / steps
	 */

	private Phase phase = Phase.FIND_LEFTMOST_NODES;
	private int polygonId = 0;
	private int position = -1;
	private List<Integer> positions = new ArrayList<Integer>();
	private int bestPolygonId = -1;

	public void nextStep()
	{
		executeNextStep();
		notifyWatchers();
	}

	public void executeNextStep()
	{
		if (phase == Phase.FIND_LEFTMOST_NODES) {
			int done = leftMostNodes.size();
			if (done < polygons.size()) {
				Polygon polygon = polygons.get(done);
				int index = computeLeftMostPoint(polygon);
				leftMostNodesIndices.put(polygon, index);
				Node node = polygon.getShell().getNode(index);
				leftMostNodes.put(polygon, node);
				if (done == polygons.size() - 1) {
					phase = Phase.FOUND_LEFTMOST_NODES;
				}
			}
		} else if (phase == Phase.FOUND_LEFTMOST_NODES) {
			phase = Phase.FIND_OVERALL_LEFTMOST_NODE;
		} else if (phase == Phase.FIND_OVERALL_LEFTMOST_NODE) {
			leftMostPolygon = computeOverallLeftMostPoint();
			leftMostNode = leftMostNodes.get(leftMostPolygon);

			phase = Phase.FOUND_OVERALL_LEFTMOST_NODE;
		} else if (phase == Phase.FOUND_OVERALL_LEFTMOST_NODE) {
			phase = Phase.INITIALIZE_DATASTRUCTURES;
		} else if (phase == Phase.INITIALIZE_DATASTRUCTURES) {
			hull = new ArrayList<Node>();
			hull.add(leftMostNode);
			for (int i = 0; i < polygons.size(); i++) {
				positions.add(leftMostNodesIndices.get(polygons.get(i)));
			}
			currentHeadPolygon = leftMostPolygon;

			phase = Phase.INITIALIZED_DATASTRUCTURES;
		} else if (phase == Phase.INITIALIZED_DATASTRUCTURES) {
			phase = Phase.LOOK_FOR_TANGENTS;
		} else if (phase == Phase.LOOK_FOR_TANGENTS) {
			Polygon polygon = polygons.get(polygonId);
			if (position == -1) {
				position = positions.get(polygonId);
				return;
			}

			Chain shell = polygon.getShell();
			IntRing ring = new IntRing(shell.getNumberOfNodes(), position);
			int prev = ring.prevValue();
			Node a = getCurrentNode();
			Node b = shell.getNode(position);
			Node c = shell.getNode(prev);

			if (polygon == currentHeadPolygon && a == b) {
				position = prev;
			} else if (GeomMath.isLeftOf(a.getCoordinate(), b.getCoordinate(),
					c.getCoordinate())) {
				position = prev;
			} else {
				phase = Phase.TANGENT_FOUND;
				positions.set(polygonId, position);
			}
		} else if (phase == Phase.TANGENT_FOUND) {
			if (polygonId < polygons.size() - 1) {
				position = -1;
				polygonId++;
				phase = Phase.LOOK_FOR_TANGENTS;
			} else {
				phase = Phase.ALL_TANGENTS_FOUND;
			}
		} else if (phase == Phase.ALL_TANGENTS_FOUND) {
			Coordinate a;
			if (hull.size() == 1) {
				a = new Coordinate(getCurrentNode().getCoordinate());
				a.setY(a.getY() - 100);
			} else {
				a = hull.get(hull.size() - 2).getCoordinate();
			}
			Coordinate b = getCurrentNode().getCoordinate();
			double bestAngle = Double.MAX_VALUE;
			Node bestNode = null;
			for (int i = 0; i < polygons.size(); i++) {
				int pos = positions.get(i);
				Polygon polygon = polygons.get(i);
				Node node = polygon.getShell().getNode(pos);
				double angle = GeomMath.angle(b, a, node.getCoordinate());
				if (angle < bestAngle) {
					bestAngle = angle;
					bestPolygonId = i;
					bestNode = node;
					currentHeadPolygon = polygon;
				}
			}
			hull.add(bestNode);
			phase = Phase.BEST_TANGENT_FOUND;
		} else if (phase == Phase.BEST_TANGENT_FOUND) {
			if (hull.get(0) != hull.get(hull.size() - 1)) {
				phase = Phase.LOOK_FOR_TANGENTS;
				polygonId = 0;
				position = positions.get(0);
			} else {
				phase = Phase.DONE;
			}
		}
	}

	public void previousStep()
	{
		// TODO: implement
		notifyWatchers();
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
