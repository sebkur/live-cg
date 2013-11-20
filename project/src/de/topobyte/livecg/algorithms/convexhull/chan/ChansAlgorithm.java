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
import java.util.List;
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

	public void addWatcher(AlgorithmWatcher watcher)
	{
		watchers.add(watcher);
	}

	public void removeWatcher(AlgorithmWatcher watcher)
	{
		watchers.remove(watcher);
	}

	private void notifyWatchers()
	{
		for (AlgorithmWatcher watcher : watchers) {
			watcher.updateAlgorithmStatus();
		}
	}

	/*
	 * Input
	 */

	private List<Polygon> polygons;

	public ChansAlgorithm(List<Polygon> polygons)
	{
		this.polygons = polygons;
	}

	public List<Polygon> getPolygons()
	{
		return polygons;
	}

	/*
	 * Scene
	 */

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
	 * Algorithm status / steps / output
	 */

	private Data data = new Data();

	public Data getData()
	{
		return data;
	}

	public void nextStep()
	{
		executeNextStep();
		notifyWatchers();
	}

	public void executeNextStep()
	{
		if (data.phase == Phase.FIND_LEFTMOST_NODES) {
			int done = data.leftMostNodes.size();
			if (done < polygons.size()) {
				Polygon polygon = polygons.get(done);
				int index = computeLeftMostPoint(polygon);
				data.leftMostNodesIndices.put(polygon, index);
				Node node = polygon.getShell().getNode(index);
				data.leftMostNodes.put(polygon, node);
				if (done == polygons.size() - 1) {
					data.phase = Phase.FOUND_LEFTMOST_NODES;
				}
			}
		} else if (data.phase == Phase.FOUND_LEFTMOST_NODES) {
			data.phase = Phase.FIND_OVERALL_LEFTMOST_NODE;
		} else if (data.phase == Phase.FIND_OVERALL_LEFTMOST_NODE) {
			data.leftMostPolygon = computeOverallLeftMostPoint();
			data.leftMostNode = data.leftMostNodes.get(data.leftMostPolygon);

			data.phase = Phase.FOUND_OVERALL_LEFTMOST_NODE;
		} else if (data.phase == Phase.FOUND_OVERALL_LEFTMOST_NODE) {
			data.phase = Phase.INITIALIZE_DATASTRUCTURES;
		} else if (data.phase == Phase.INITIALIZE_DATASTRUCTURES) {
			data.hull = new ArrayList<Node>();
			data.hull.add(data.leftMostNode);
			for (int i = 0; i < polygons.size(); i++) {
				data.positions.add(data.leftMostNodesIndices.get(polygons
						.get(i)));
			}
			data.currentHeadPolygon = data.leftMostPolygon;

			data.phase = Phase.INITIALIZED_DATASTRUCTURES;
		} else if (data.phase == Phase.INITIALIZED_DATASTRUCTURES) {
			data.phase = Phase.LOOK_FOR_TANGENTS;
		} else if (data.phase == Phase.LOOK_FOR_TANGENTS) {
			Polygon polygon = polygons.get(data.polygonId);
			if (data.position == -1) {
				data.position = data.positions.get(data.polygonId);
			} else {
				Chain shell = polygon.getShell();
				IntRing ring = new IntRing(shell.getNumberOfNodes(),
						data.position);
				int prev = ring.prevValue();
				Node a = data.getCurrentNode();
				Node b = shell.getNode(data.position);
				Node c = shell.getNode(prev);

				if (polygon == data.currentHeadPolygon && a == b) {
					data.position = prev;
				} else if (GeomMath.isLeftOf(a.getCoordinate(),
						b.getCoordinate(), c.getCoordinate())) {
					data.position = prev;
				} else {
					data.phase = Phase.TANGENT_FOUND;
					data.positions.set(data.polygonId, data.position);
				}
			}
		} else if (data.phase == Phase.TANGENT_FOUND) {
			if (data.polygonId < polygons.size() - 1) {
				data.position = -1;
				data.polygonId++;
				data.phase = Phase.LOOK_FOR_TANGENTS;
			} else {
				data.phase = Phase.ALL_TANGENTS_FOUND;
			}
		} else if (data.phase == Phase.ALL_TANGENTS_FOUND) {
			Coordinate a;
			if (data.hull.size() == 1) {
				a = new Coordinate(data.getCurrentNode().getCoordinate());
				a.setY(a.getY() - 100);
			} else {
				a = data.hull.get(data.hull.size() - 2).getCoordinate();
			}
			Coordinate b = data.getCurrentNode().getCoordinate();
			double bestAngle = Double.MAX_VALUE;
			Node bestNode = null;
			for (int i = 0; i < polygons.size(); i++) {
				int pos = data.positions.get(i);
				Polygon polygon = polygons.get(i);
				Node node = polygon.getShell().getNode(pos);
				double angle = GeomMath.angle(b, a, node.getCoordinate());
				if (angle < bestAngle) {
					bestAngle = angle;
					data.bestPolygonId = i;
					bestNode = node;
					data.currentHeadPolygon = polygon;
				}
			}
			data.hull.add(bestNode);
			data.phase = Phase.BEST_TANGENT_FOUND;
		} else if (data.phase == Phase.BEST_TANGENT_FOUND) {
			if (data.hull.get(0) != data.hull.get(data.hull.size() - 1)) {
				data.phase = Phase.LOOK_FOR_TANGENTS;
				data.polygonId = 0;
				data.position = data.positions.get(0);
			} else {
				data.phase = Phase.DONE;
			}
		}
	}

	public void previousStep()
	{
		// TODO: implement
		notifyWatchers();
	}

	/*
	 * Internal
	 */

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
		for (Entry<Polygon, Node> entry : data.leftMostNodes.entrySet()) {
			Node node = entry.getValue();
			if (node.getCoordinate().getX() < x) {
				leftMostPolygon = entry.getKey();
				x = node.getCoordinate().getX();
			}
		}
		return leftMostPolygon;
	}
}
