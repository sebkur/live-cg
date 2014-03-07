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
package de.topobyte.livecg.algorithms.polygon.monotone;

import java.util.List;

import de.topobyte.livecg.algorithms.polygon.monotonepieces.SplitResult;
import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.algorithms.polygon.util.DiagonalUtil;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingVisualizationPainter;

public class MonotoneTriangulationPainter extends
		TransformingVisualizationPainter
{
	private String q(String property)
	{
		return "algorithm.polygon.monotone." + property;
	}

	private String qc(String property)
	{
		return q("colors." + property);
	}

	private Color COLOR_BG = LiveConfig.getColor(qc("background"));

	private Color COLOR_POLYGON_INTERIOR = LiveConfig.getColor(qc("polygon"));
	private Color COLOR_POLYGON_BOUNDARY = LiveConfig.getColor(qc("boundary"));

	// Node info
	private Color COLOR_NODE_OUTLINE = LiveConfig.getColor(qc("node.outline"));
	private Color COLOR_NODE_DUE = LiveConfig.getColor(qc("node.due"));
	private Color COLOR_NODE_STACK = LiveConfig.getColor(qc("node.stack"));
	private Color COLOR_NODE_CURRENT = LiveConfig.getColor(qc("node.current"));
	private Color COLOR_NODE_DONE = LiveConfig.getColor(qc("node.done"));
	private double RADIUS_DUE = LiveConfig.getNumber(q("node.size.due"));
	private double RADIUS_STACK = LiveConfig.getNumber(q("node.size.stack"));
	private double RADIUS_CURRENT = LiveConfig
			.getNumber(q("node.size.current"));
	private double RADIUS_DONE = LiveConfig.getNumber(q("node.size.done"));

	// Other
	private Color COLOR_DIAGONALS = LiveConfig.getColor(qc("diagonals"));
	private Color COLOR_LAST_DIAGONAL = LiveConfig
			.getColor(qc("last_diagonal"));
	private Color COLOR_POLYGON_DONE = LiveConfig.getColor(qc("polygon.done"));
	private double WIDTH_BOUNDARY = LiveConfig.getNumber(q("width.polygon"));
	private double WIDTH_LAST_DIAGONAL = LiveConfig
			.getNumber(q("width.last_diagonal"));
	private double WIDTH_DIAGONALS = LiveConfig.getNumber(q("width.diagonals"));
	private double WIDTH_NODE_OUTLINE = LiveConfig
			.getNumber(q("width.node_outline"));

	private MonotoneTriangulationAlgorithm algorithm;
	private MonotoneTriangulationConfig config;

	public MonotoneTriangulationPainter(
			MonotoneTriangulationAlgorithm algorithm,
			MonotoneTriangulationConfig config, Painter painter)
	{
		super(algorithm.getScene(), painter);
		this.algorithm = algorithm;
		this.config = config;
	}

	@Override
	public void paint()
	{
		preparePaint();

		fillBackground(COLOR_BG);

		fillPolygon();

		drawPolygon();

		drawVarious();
	}

	protected void fillPolygon()
	{
		painter.setColor(COLOR_POLYGON_INTERIOR);
		Polygon tpolygon = transformer.transform(algorithm.getPolygon());
		painter.fillPolygon(tpolygon);
	}

	protected void drawPolygon()
	{
		painter.setColor(COLOR_POLYGON_BOUNDARY);
		painter.setStrokeWidth(WIDTH_BOUNDARY);
		Polygon tpolygon = transformer.transform(algorithm.getPolygon());
		painter.drawPolygon(tpolygon);
	}

	private void drawDiagonals()
	{
		painter.setColor(COLOR_DIAGONALS);
		painter.setStrokeWidth(WIDTH_DIAGONALS);
		List<Diagonal> diagonals = algorithm.getDiagonals();

		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			Coordinate t1 = transformer.transform(c1);
			Coordinate t2 = transformer.transform(c2);
			painter.drawLine(t1.getX(), t1.getY(), t2.getX(), t2.getY());
		}
	}

	private void drawMinorDiagonals()
	{
		painter.setColor(COLOR_DIAGONALS);
		painter.setStrokeWidth(WIDTH_DIAGONALS);
		List<Diagonal> diagonals = algorithm.getMinorDiagonals();

		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			Coordinate t1 = transformer.transform(c1);
			Coordinate t2 = transformer.transform(c2);
			painter.drawLine(t1.getX(), t1.getY(), t2.getX(), t2.getY());
		}
	}

	private void drawVarious()
	{
		List<Node> nodes = algorithm.getNodes();

		boolean unfinished = algorithm.getStatus() < algorithm
				.getNumberOfSteps();

		/*
		 * Ready part highlight
		 */

		List<Diagonal> diagonals = algorithm.getDiagonals();
		if (!unfinished) {
			painter.setColor(COLOR_POLYGON_DONE);
			painter.fillPolygon(transformer.transform(algorithm.getPolygon()));
		} else if (diagonals.size() > 0) {
			Coordinate last = nodes.get(nodes.size() - 1).getCoordinate();
			SplitResult split = DiagonalUtil.split(algorithm.getPolygon(),
					diagonals);
			List<Polygon> parts = split.getPolygons();
			for (Polygon part : parts) {
				Rectangle r = BoundingBoxes.get(part);
				if (r.getY2() >= last.getY()) {
					continue;
				}
				painter.setColor(COLOR_POLYGON_DONE);
				painter.fillPolygon(transformer.transform(part));
			}
		}

		/*
		 * Last diagonal
		 */

		if (unfinished && diagonals.size() > 0) {
			Diagonal diagonal = diagonals.get(diagonals.size() - 1);
			Coordinate a = transformer.transform(diagonal.getA()
					.getCoordinate());
			Coordinate b = transformer.transform(diagonal.getB()
					.getCoordinate());
			painter.setColor(COLOR_LAST_DIAGONAL);
			painter.setStrokeWidth(WIDTH_LAST_DIAGONAL);
			painter.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
			painter.setStrokeWidth(1.0);
		}

		/*
		 * Ready diagonals
		 */

		drawDiagonals();

		drawMinorDiagonals();

		/*
		 * Nodes
		 */

		if (unfinished) {
			for (int i = 0; i < algorithm.getStatus(); i++) {
				Node node = nodes.get(i);
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(COLOR_NODE_DONE);
				painter.fillCircle(c.getX(), c.getY(), RADIUS_DONE);
				painter.setColor(COLOR_NODE_OUTLINE);
				painter.setStrokeWidth(WIDTH_NODE_OUTLINE);
				painter.drawCircle(c.getX(), c.getY(), RADIUS_DONE);
			}
			for (int i = algorithm.getStatus(); i < nodes.size(); i++) {
				Node node = nodes.get(i);
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(COLOR_NODE_DUE);
				painter.fillCircle(c.getX(), c.getY(), RADIUS_DUE);
				painter.setColor(COLOR_NODE_OUTLINE);
				painter.setStrokeWidth(WIDTH_NODE_OUTLINE);
				painter.drawCircle(c.getX(), c.getY(), RADIUS_DUE);
			}
			for (Node node : algorithm.getStack()) {
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(COLOR_NODE_STACK);
				painter.fillCircle(c.getX(), c.getY(), RADIUS_STACK);
				painter.setColor(COLOR_NODE_OUTLINE);
				painter.setStrokeWidth(WIDTH_NODE_OUTLINE);
				painter.drawCircle(c.getX(), c.getY(), RADIUS_STACK);
			}
			if (algorithm.getStatus() > 0 && algorithm.getSubStatus() > 0) {
				Coordinate c = transformer.transform(algorithm.getNodes()
						.get(algorithm.getStatus() + 1).getCoordinate());
				painter.setColor(COLOR_NODE_CURRENT);
				painter.fillCircle(c.getX(), c.getY(), RADIUS_CURRENT);
				painter.setColor(COLOR_NODE_OUTLINE);
				painter.setStrokeWidth(WIDTH_NODE_OUTLINE);
				painter.drawCircle(c.getX(), c.getY(), RADIUS_CURRENT);
			}
		} else {
			for (Node node : algorithm.getNodes()) {
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(COLOR_NODE_DONE);
				painter.fillCircle(c.getX(), c.getY(), RADIUS_DONE);
				painter.setColor(COLOR_NODE_OUTLINE);
				painter.setStrokeWidth(WIDTH_NODE_OUTLINE);
				painter.drawCircle(c.getX(), c.getY(), RADIUS_DONE);
			}
		}
	}
}
