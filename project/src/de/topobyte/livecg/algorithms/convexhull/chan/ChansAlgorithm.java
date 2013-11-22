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

import de.topobyte.livecg.core.DefaultSceneAlgorithm;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.util.circular.IntRing;

public class ChansAlgorithm extends DefaultSceneAlgorithm
{

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

	private List<Data> history = new ArrayList<Data>();

	public Data getData()
	{
		return data;
	}

	public void nextStep()
	{
		if (data.getPhase() != Phase.DONE) {
			history.add(data.clone());
			executeNextStep();
			fireAlgorithmStatusChanged();
		}
	}

	public void previousStep()
	{
		if (history.size() > 0) {
			data = history.remove(history.size() - 1);
			fireAlgorithmStatusChanged();
		}
	}

	public void executeNextStep()
	{
		if (data.getPhase() == Phase.FIND_LEFTMOST_NODES) {
			int done = data.getNumberOfLeftmostNodesFound();
			if (done < polygons.size()) {
				Polygon polygon = polygons.get(done);
				int index = computeLeftMostPoint(polygon);
				data.setLeftMostNodesIndex(polygon, index);
				if (done == polygons.size() - 1) {
					data.setPhase(Phase.FOUND_LEFTMOST_NODES);
				}
			}
		} else if (data.getPhase() == Phase.FOUND_LEFTMOST_NODES) {
			data.setPhase(Phase.FIND_OVERALL_LEFTMOST_NODE);
		} else if (data.getPhase() == Phase.FIND_OVERALL_LEFTMOST_NODE) {
			data.setLeftMostPolygon(computeOverallLeftMostPoint());
			data.setOverallLeftMostNode(data.getLeftMostNode(data
					.getLeftMostPolygon()));

			data.setPhase(Phase.FOUND_OVERALL_LEFTMOST_NODE);
		} else if (data.getPhase() == Phase.FOUND_OVERALL_LEFTMOST_NODE) {
			data.setPhase(Phase.INITIALIZE_DATASTRUCTURES);
		} else if (data.getPhase() == Phase.INITIALIZE_DATASTRUCTURES) {
			data.initializeHull();
			data.appendToHull(data.getOverallLeftMostNode());
			for (int i = 0; i < polygons.size(); i++) {
				data.setPosition(i, data.getLeftMostIndex(polygons.get(i)));
			}
			data.setCurrentHeadPolygon(data.getLeftMostPolygon());

			data.setPhase(Phase.INITIALIZED_DATASTRUCTURES);
		} else if (data.getPhase() == Phase.INITIALIZED_DATASTRUCTURES) {
			data.setPhase(Phase.LOOK_FOR_TANGENTS);
		} else if (data.getPhase() == Phase.LOOK_FOR_TANGENTS) {
			Polygon polygon = polygons.get(data.getPolygonId());
			if (data.getPosition() == -1) {
				data.setPosition(data.getPosition(data.getPolygonId()));
			} else {
				Chain shell = polygon.getShell();
				IntRing ring = new IntRing(shell.getNumberOfNodes(),
						data.getPosition());
				int prev = ring.prevValue();
				Node a = data.getCurrentNode();
				Node b = shell.getNode(data.getPosition());
				Node c = shell.getNode(prev);

				if (polygon == data.getCurrentHeadPolygon() && a == b) {
					data.setPosition(prev);
				} else if (GeomMath.isLeftOf(a.getCoordinate(),
						b.getCoordinate(), c.getCoordinate())) {
					data.setPosition(prev);
				} else {
					data.setPhase(Phase.TANGENT_FOUND);
					data.setPosition(data.getPolygonId(), data.getPosition());
				}
			}
		} else if (data.getPhase() == Phase.TANGENT_FOUND) {
			if (data.getPolygonId() < polygons.size() - 1) {
				data.setPosition(-1);
				data.setPolygonId(data.getPolygonId() + 1);
				data.setPhase(Phase.LOOK_FOR_TANGENTS);
			} else {
				data.setPhase(Phase.ALL_TANGENTS_FOUND);
			}
		} else if (data.getPhase() == Phase.ALL_TANGENTS_FOUND) {
			Coordinate a;
			if (data.getHull().size() == 1) {
				a = new Coordinate(data.getCurrentNode().getCoordinate());
				a.setY(a.getY() - 100);
			} else {
				a = data.getHull().get(data.getHull().size() - 2)
						.getCoordinate();
			}
			Coordinate b = data.getCurrentNode().getCoordinate();
			double bestAngle = Double.MAX_VALUE;
			Node bestNode = null;
			for (int i = 0; i < polygons.size(); i++) {
				int pos = data.getPosition(i);
				Polygon polygon = polygons.get(i);
				Node node = polygon.getShell().getNode(pos);
				double angle = GeomMath.angle(b, a, node.getCoordinate());
				if (angle < bestAngle) {
					bestAngle = angle;
					data.setBestPolygonId(i);
					bestNode = node;
					data.setCurrentHeadPolygon(polygon);
				}
			}
			data.appendToHull(bestNode);
			data.setPhase(Phase.BEST_TANGENT_FOUND);
		} else if (data.getPhase() == Phase.BEST_TANGENT_FOUND) {
			if (data.getHull().get(0) != data.getHull().get(
					data.getHull().size() - 1)) {
				data.setPhase(Phase.LOOK_FOR_TANGENTS);
				data.setPolygonId(0);
				data.setPosition(data.getPosition(0));
			} else {
				data.setPhase(Phase.DONE);
			}
		}
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
		for (Polygon polygon : polygons) {
			Node node = data.getLeftMostNode(polygon);
			if (node.getCoordinate().getX() < x) {
				leftMostPolygon = polygon;
				x = node.getCoordinate().getX();
			}
		}
		return leftMostPolygon;
	}
}
