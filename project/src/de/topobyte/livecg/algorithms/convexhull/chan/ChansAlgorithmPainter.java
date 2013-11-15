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
import java.util.Map;

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

	@Override
	public void paint()
	{
		preparePaint();

		fillBackground(new Color(0xffffff));

		Phase phase = algorithm.getPhase();

		if (phase == Phase.LOOK_FOR_TANGENTS || phase == Phase.TANGENT_FOUND) {
			// Current polygon
			painter.setColor(new Color(0xffaaaa));
			Polygon p = algorithm.getPolygons().get(algorithm.getPolygonId());
			painter.fillPolygon(transformer.transform(p));
		}
		if (phase == Phase.ALL_TANGENTS_FOUND) {
			// All polygons
			painter.setColor(new Color(0xff6666));
			for (Polygon polygon : algorithm.getPolygons()) {
				Polygon tpolygon = transformer.transform(polygon);
				painter.fillPolygon(tpolygon);
			}
		}
		if (phase == Phase.BEST_TANGENT_FOUND) {
			// Best polygon
			painter.setColor(new Color(0x99ff99));
			Polygon best = algorithm.getPolygons().get(
					algorithm.getBestPolygonId());
			Polygon tbest = transformer.transform(best);
			painter.fillPolygon(tbest);
		}

		// All polygons -> outline
		painter.setColor(new Color(0x000000));
		for (Polygon polygon : algorithm.getPolygons()) {
			Polygon tpolygon = transformer.transform(polygon);
			painter.drawPolygon(tpolygon);
		}

		// Leftmost nodes of all polygons
		painter.setColor(new Color(0x000000));
		Map<Polygon, Node> leftMostNodes = algorithm.getLeftMostNodes();
		for (Polygon polygon : algorithm.getPolygons()) {
			Node node = leftMostNodes.get(polygon);
			Coordinate c = transformer.transform(node.getCoordinate());
			painter.fillCircle(c.getX(), c.getY(), 4);
		}

		// Overall leftmost
		painter.setColor(new Color(0x000000));
		Node leftMostNode = algorithm.getLeftMostNode();
		Coordinate c = transformer.transform(leftMostNode.getCoordinate());
		painter.drawCircle(c.getX(), c.getY(), 7);

		// Already found tangent nodes
		List<Integer> positions = algorithm.getPositions();
		for (int i = 0; i < positions.size(); i++) {
			int pos = positions.get(i);
			Polygon polygon = algorithm.getPolygons().get(i);
			Node node = polygon.getShell().getNode(pos);
			c = transformer.transform(node.getCoordinate());
			painter.drawCircle(c.getX(), c.getY(), 7);
		}

		// Tangent search nodes
		if (algorithm.getPosition() >= 0) {
			Polygon p = algorithm.getPolygons().get(algorithm.getPolygonId());
			Chain shell = p.getShell();
			Node node = shell.getNode(algorithm.getPosition());
			c = transformer.transform(node.getCoordinate());
			painter.drawCircle(c.getX(), c.getY(), 7);
		}

		// Computed hull
		List<Node> hull = algorithm.getHull();
		List<Coordinate> hullCoordinates = new ArrayList<Coordinate>();
		for (int i = 0; i < hull.size(); i++) {
			Node node = hull.get(i);
			hullCoordinates.add(transformer.transform(node.getCoordinate()));
		}
		painter.setStrokeWidth(2.0);
		painter.drawPath(hullCoordinates, phase == Phase.DONE);
		painter.setStrokeWidth(1.0);
		for (Node node : hull) {
			c = transformer.transform(node.getCoordinate());
			painter.drawRect(c.getX() - 5, c.getY() - 5, 10, 10);
		}

		// Ids
		for (int i = 0; i < algorithm.getPolygons().size(); i++) {
			Polygon polygon = algorithm.getPolygons().get(i);
			c = transformer.transform(PolygonHelper.center(polygon));
			painter.drawString(String.format("%d", i), c.getX(), c.getY());
		}
	}
}
