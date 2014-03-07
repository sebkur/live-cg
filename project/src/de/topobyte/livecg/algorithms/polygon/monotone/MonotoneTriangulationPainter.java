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

		fillBackground(new Color(0xffffff));

		fillPolygon();

		drawPolygon();

		drawVarious();
	}

	protected void fillPolygon()
	{
		painter.setColor(new Color(0x66ff0000, true));
		Polygon tpolygon = transformer.transform(algorithm.getPolygon());
		painter.fillPolygon(tpolygon);
	}

	protected void drawPolygon()
	{
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		Polygon tpolygon = transformer.transform(algorithm.getPolygon());
		painter.drawPolygon(tpolygon);
	}

	private void drawDiagonals()
	{
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
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
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
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
		// Node info
		Color cDue = new Color(0xff0000);
		Color cDone = new Color(0x00ff00);
		Color cStack = new Color(0xffff00);
		Color cCurrent = new Color(0x0000ff);
		Color cOutline = new Color(0x000000);
		double radius = 3;
		// Other
		Color cLast = new Color(0xffffff);
		Color cReady = new Color(0x33ffffff, true);
		double widLast = 4;

		boolean unfinished = algorithm.getStatus() < algorithm
				.getNumberOfSteps();

		/*
		 * Ready part highlight
		 */

		List<Diagonal> diagonals = algorithm.getDiagonals();
		if (!unfinished) {
			painter.setColor(cReady);
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
				painter.setColor(cReady);
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
			painter.setColor(cLast);
			painter.setStrokeWidth(widLast);
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
				painter.setColor(cDone);
				painter.fillCircle(c.getX(), c.getY(), radius);
				painter.setColor(cOutline);
				painter.drawCircle(c.getX(), c.getY(), radius);
			}
			for (int i = algorithm.getStatus(); i < nodes.size(); i++) {
				Node node = nodes.get(i);
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(cDue);
				painter.fillCircle(c.getX(), c.getY(), radius);
				painter.setColor(cOutline);
				painter.drawCircle(c.getX(), c.getY(), radius);
			}
			for (Node node : algorithm.getStack()) {
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(cStack);
				painter.fillCircle(c.getX(), c.getY(), radius);
				painter.setColor(cOutline);
				painter.drawCircle(c.getX(), c.getY(), radius);
			}
			if (algorithm.getStatus() > 0) {
				Coordinate c = transformer.transform(algorithm.getNodes()
						.get(algorithm.getStatus() + 1).getCoordinate());
				painter.setColor(cCurrent);
				painter.fillCircle(c.getX(), c.getY(), radius);
				painter.setColor(cOutline);
				painter.drawCircle(c.getX(), c.getY(), radius);
			}
		} else {
			for (Node node : algorithm.getNodes()) {
				Coordinate c = transformer.transform(node.getCoordinate());
				painter.setColor(cDone);
				painter.fillCircle(c.getX(), c.getY(), radius);
				painter.setColor(cOutline);
				painter.drawCircle(c.getX(), c.getY(), radius);
			}
		}
	}
}
