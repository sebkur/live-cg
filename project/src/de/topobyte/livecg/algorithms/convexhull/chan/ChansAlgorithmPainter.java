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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingAlgorithmPainter;

public class ChansAlgorithmPainter extends TransformingAlgorithmPainter
{

	private ChansAlgorithm algorithm;

	public ChansAlgorithmPainter(Rectangle scene, ChansAlgorithm algorithm,
			Painter painter)
	{
		super(scene, painter);
		this.algorithm = algorithm;
	}

	/*
	 * Helpers for filling all leftover polygons that are not marked with some
	 * special color with a default color.
	 */
	private Set<Polygon> filled;

	private void fill(Polygon polygon)
	{
		painter.fillPolygon(transformer.transform(polygon));
		filled.add(polygon);
	}

	@Override
	public void paint()
	{
		preparePaint();

		fillBackground(new Color(0xffffff));

		Phase phase = algorithm.getPhase();

		painter.setColor(new Color(0x000000));

		Coordinate text = transformer.transform(new Coordinate(
				scene.getX1() + 10, scene.getY1() + 20));
		painter.drawString(phase.toString(), text.getX(), text.getY());

		filled = new HashSet<Polygon>();

		/*
		 * Polygon interior
		 */

		Color colorOthers = new Color(0xDDDDDD);
		Color colorCurrent = new Color(0xffaaaa);
		Color colorFound = new Color(0x6666ff);
		Color colorFoundNow = new Color(0x6666ff);
		Color colorFoundAll = new Color(0x6666ff);
		Color colorBest = new Color(0x99ff99);

		Color colorOulines = new Color(0x000000);
		Color colorNodes = new Color(0x000000);
		Color colorHull = new Color(0x00ff00);
		Color colorIds = new Color(0x000000);
		Color colorTangents = new Color(0xff0000);
		Color colorSearchTangent = new Color(0xff00ff);

		double widthTangents = 2.0;
		double widthSearchTangent = 2.0;

		if (phase.ordinal() <= Phase.INITIALIZED_DATASTRUCTURES.ordinal()
				|| phase == Phase.DONE) {
			painter.setColor(colorOthers);
			for (int i = 0; i < algorithm.getPolygons().size(); i++) {
				fill(algorithm.getPolygons().get(i));
			}
		}

		if (phase == Phase.LOOK_FOR_TANGENTS) {
			// Polygons we did not find a tangent for yet
			painter.setColor(colorOthers);
			for (int i = algorithm.getPolygonId(); i < algorithm.getPolygons()
					.size(); i++) {
				fill(algorithm.getPolygons().get(i));
			}
			// Polygons we already found the tangent for
			painter.setColor(colorFound);
			for (int i = 0; i < algorithm.getPolygonId(); i++) {
				fill(algorithm.getPolygons().get(i));
			}
			// Current polygon
			painter.setColor(colorCurrent);
			fill(algorithm.getPolygons().get(algorithm.getPolygonId()));
		}
		if (phase == Phase.TANGENT_FOUND) {
			// Other polygons
			painter.setColor(colorOthers);
			for (int i = 0; i < algorithm.getPolygons().size(); i++) {
				if (i != algorithm.getPolygonId()) {
					fill(algorithm.getPolygons().get(i));
				}
			}
			// Polygon we just now found the tangent for
			painter.setColor(colorFoundNow);
			fill(algorithm.getPolygons().get(algorithm.getPolygonId()));
		}
		// All polygons after finding the tangents
		if (phase == Phase.ALL_TANGENTS_FOUND) {
			painter.setColor(colorFoundAll);
			for (Polygon polygon : algorithm.getPolygons()) {
				fill(polygon);
			}
		}
		if (phase == Phase.BEST_TANGENT_FOUND) {
			// Other polygons
			painter.setColor(colorOthers);
			for (int i = 0; i < algorithm.getPolygons().size(); i++) {
				if (i != algorithm.getBestPolygonId()) {
					fill(algorithm.getPolygons().get(i));
				}
			}
			// Best polygon
			painter.setColor(colorBest);
			Polygon best = algorithm.getPolygons().get(
					algorithm.getBestPolygonId());
			fill(best);
		}

		/*
		 * Polygon outline
		 */

		// Outline for all polygons
		painter.setColor(colorOulines);
		for (Polygon polygon : algorithm.getPolygons()) {
			Polygon tpolygon = transformer.transform(polygon);
			painter.drawPolygon(tpolygon);
		}

		/*
		 * Nodes / tangents
		 */

		// Leftmost nodes that have already been found
		if (phase.ordinal() >= Phase.FIND_LEFTMOST_NODES.ordinal()
				&& phase.ordinal() < Phase.INITIALIZED_DATASTRUCTURES.ordinal()) {
			painter.setColor(colorNodes);
			Map<Polygon, Node> leftMostNodes = algorithm.getLeftMostNodes();
			for (Polygon polygon : algorithm.getPolygons()) {
				Node node = leftMostNodes.get(polygon);
				if (node == null) {
					continue;
				}
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.fillRect(c.getX() - 4, c.getY() - 4, 8, 8);
			}
		}

		// Tangents
		if (phase.ordinal() >= Phase.INITIALIZED_DATASTRUCTURES.ordinal()
				&& phase != Phase.BEST_TANGENT_FOUND && phase != Phase.DONE) {
			painter.setColor(colorTangents);
			painter.setStrokeWidth(widthTangents);
			Coordinate current = transformer.transform(algorithm
					.getCurrentNode().getCoordinate());

			List<Integer> positions = algorithm.getPositions();
			for (int i = 0; i < positions.size(); i++) {
				int pos = positions.get(i);
				Polygon polygon = algorithm.getPolygons().get(i);
				Node node = polygon.getShell().getNode(pos);
				Coordinate c = transformer.transform(node.getCoordinate());

				painter.drawLine(current.getX(), current.getY(), c.getX(),
						c.getY());
			}
			painter.setStrokeWidth(1.0);
		}

		// Already found tangent nodes
		if (phase.ordinal() >= Phase.INITIALIZED_DATASTRUCTURES.ordinal()) {
			painter.setColor(colorNodes);
			List<Integer> positions = algorithm.getPositions();
			for (int i = 0; i < positions.size(); i++) {
				int pos = positions.get(i);
				Polygon polygon = algorithm.getPolygons().get(i);
				Node node = polygon.getShell().getNode(pos);
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.fillCircle(c.getX(), c.getY(), 4);
			}
		}

		// Search tangent
		if (phase == Phase.LOOK_FOR_TANGENTS) {
			painter.setColor(colorSearchTangent);
			painter.setStrokeWidth(widthSearchTangent);
			Coordinate current = transformer.transform(algorithm
					.getCurrentNode().getCoordinate());

			if (algorithm.getPosition() >= 0) {
				Polygon p = algorithm.getPolygons().get(
						algorithm.getPolygonId());
				Chain shell = p.getShell();
				Node node = shell.getNode(algorithm.getPosition());
				Coordinate c = transformer.transform(node.getCoordinate());

				painter.drawLine(current.getX(), current.getY(), c.getX(),
						c.getY());
			}
			painter.setStrokeWidth(1.0);
		}

		// Tangent search nodes
		if (phase == Phase.LOOK_FOR_TANGENTS) {
			painter.setColor(colorNodes);
			if (algorithm.getPosition() >= 0) {
				Polygon p = algorithm.getPolygons().get(
						algorithm.getPolygonId());
				Chain shell = p.getShell();
				Node node = shell.getNode(algorithm.getPosition());
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.drawCircle(c.getX(), c.getY(), 7);
			}
		}

		if (phase == Phase.FOUND_OVERALL_LEFTMOST_NODE
				|| phase == Phase.INITIALIZE_DATASTRUCTURES) {
			painter.setColor(colorNodes);
			Node node = algorithm.getLeftMostNode();
			Coordinate c = transformer.transform(node.getCoordinate());
			painter.drawCircle(c.getX(), c.getY(), 7);
		}

		// Computed hull
		if (phase.ordinal() >= Phase.INITIALIZED_DATASTRUCTURES.ordinal()) {
			painter.setColor(colorHull);
			List<Node> hull = algorithm.getHull();
			List<Coordinate> hullCoordinates = new ArrayList<Coordinate>();
			for (int i = 0; i < hull.size(); i++) {
				Node node = hull.get(i);
				hullCoordinates
						.add(transformer.transform(node.getCoordinate()));
			}
			painter.setStrokeWidth(2.0);
			painter.drawPath(hullCoordinates, phase == Phase.DONE);
			painter.setStrokeWidth(1.0);

			painter.setColor(colorNodes);
			for (Node node : hull) {
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.drawCircle(c.getX(), c.getY(), 4);
			}
		}

		// Ids
		for (int i = 0; i < algorithm.getPolygons().size(); i++) {
			painter.setColor(colorIds);
			Polygon polygon = algorithm.getPolygons().get(i);
			Coordinate c = transformer.transform(PolygonHelper.center(polygon));
			painter.drawString(String.format("%d", i + 1), c.getX(), c.getY());
		}
	}
}
