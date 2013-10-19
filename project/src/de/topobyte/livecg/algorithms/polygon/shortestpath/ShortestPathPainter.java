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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.awt.Shape;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.topobyte.livecg.algorithms.polygon.monotonepieces.Diagonal;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.util.MouseOver;
import de.topobyte.livecg.util.ShapeUtil;
import de.topobyte.livecg.util.circular.IntRing;
import de.topobyte.livecg.util.graph.Edge;

public class ShortestPathPainter extends BasicAlgorithmPainter
{

	private String q(String property)
	{
		return "algorithm.polygon.shortestpath.colors." + property;
	}

	private Color COLOR_BG = LiveConfig.getColor(q("background"));

	private Color COLOR_POLYGON_BG = LiveConfig.getColor(q("polygon"));
	private Color COLOR_TRIANGLE_SLEEVE = LiveConfig.getColor(q("sleeve"));
	private Color COLOR_TRIANGLE_SLEEVE_DONE = LiveConfig
			.getColor(q("sleeve.done"));

	private Color COLOR_POLYGON_EDGES = LiveConfig.getColor(q("boundary"));
	private Color COLOR_DIAGONALS_NONSLEEVE = LiveConfig
			.getColor(q("diagonals"));
	private Color COLOR_DIAGONALS_SLEEVE = LiveConfig.getColor(q("diagonals"));
	private Color COLOR_DUAL_GRAPH = LiveConfig.getColor(q("dualgraph"));

	private Color COLOR_NODE_START = LiveConfig.getColor(q("node.start"));
	private Color COLOR_NODE_TARGET = LiveConfig.getColor(q("node.target"));
	private Color COLOR_NODE_START_OUTLINE = LiveConfig
			.getColor(q("node.start.outline"));
	private Color COLOR_NODE_TARGET_OUTLINE = LiveConfig
			.getColor(q("node.target.outline"));

	private Color COLOR_APEX = LiveConfig.getColor(q("path.apex"));
	private Color COLOR_LEFT_TOP = LiveConfig.getColor(q("path.left.top"));
	private Color COLOR_RIGHT_TOP = LiveConfig.getColor(q("path.right.top"));
	private Color COLOR_COMMON_PATH = LiveConfig.getColor(q("path.common"));
	private Color COLOR_LEFT_PATH = LiveConfig.getColor(q("path.left"));
	private Color COLOR_RIGHT_PATH = LiveConfig.getColor(q("path.right"));

	private Color COLOR_NODE_IDS = LiveConfig.getColor(q("node.ids"));

	private ShortestPathAlgorithm algorithm;
	private Config config;

	// Variables for handling start / target dragging

	private Coordinate dragStart = null;
	private Coordinate dragTarget = null;

	private MouseOver mouseOverStart = MouseOver.NONE;
	private MouseOver mouseOverTarget = MouseOver.NONE;

	public ShortestPathPainter(ShortestPathAlgorithm algorithm, Config config,
			Painter painter)
	{
		super(painter);
		this.algorithm = algorithm;
		this.config = config;
	}

	public void setDragStart(Coordinate dragStart)
	{
		this.dragStart = dragStart;
	}

	public void setDragTarget(Coordinate dragTarget)
	{
		this.dragTarget = dragTarget;
	}

	public boolean setStartMouseOver(MouseOver over)
	{
		if (mouseOverStart == over) {
			return false;
		}
		mouseOverStart = over;
		return true;
	}

	public boolean setTargetMouseOver(MouseOver over)
	{
		if (mouseOverTarget == over) {
			return false;
		}
		mouseOverTarget = over;
		return true;
	}

	public void paint()
	{
		painter.setColor(COLOR_BG);
		painter.fillRect(0, 0, getWidth(), getHeight());

		painter.setColor(COLOR_POLYGON_BG);
		painter.fillPolygon(algorithm.getPolygon());

		List<Polygon> triangles = algorithm.getSleeve().getPolygons();
		for (int i = 0; i < triangles.size(); i++) {
			if (i < algorithm.getStatus()) {
				painter.setColor(COLOR_TRIANGLE_SLEEVE_DONE);
			} else {
				painter.setColor(COLOR_TRIANGLE_SLEEVE);
			}
			Polygon triangle = triangles.get(i);
			painter.fillPolygon(triangle);
		}

		painter.setStrokeWidth(1.0);

		painter.setColor(COLOR_POLYGON_EDGES);
		Chain shell = algorithm.getPolygon().getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			painter.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		for (Diagonal diagonal : algorithm.getTriangulationDiagonals()) {
			painter.setColor(COLOR_DIAGONALS_NONSLEEVE);
			if (algorithm.getSleeve().getDiagonals().contains(diagonal)) {
				painter.setColor(COLOR_DIAGONALS_SLEEVE);
			}
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			painter.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		if (config.isDrawDualGraph()) {
			painter.setColor(COLOR_DUAL_GRAPH);
			Collection<Polygon> nodes = algorithm.getGraph().getNodes();
			for (Polygon p : nodes) {
				Coordinate cp = PolygonHelper.center(p);
				Set<Edge<Polygon, Diagonal>> edges = algorithm.getGraph()
						.getEdgesOut(p);
				for (Edge<Polygon, Diagonal> edge : edges) {
					Polygon q = edge.getTarget();
					Coordinate cq = PolygonHelper.center(q);
					painter.drawLine((int) Math.round(cp.getX()),
							(int) Math.round(cp.getY()),
							(int) Math.round(cq.getX()),
							(int) Math.round(cq.getY()));
				}
			}
		}

		painter.setStrokeWidth(2.0);

		Data data = algorithm.getData();
		if (data != null) {
			paintCommonPath();

			paintPath(Side.LEFT);
			paintPath(Side.RIGHT);
			painter.setColor(COLOR_APEX);

			Coordinate c = data.getApex().getCoordinate();
			painter.fill(ShapeUtil.createArc(c.getX(), c.getY(), 4));
		}

		Coordinate cStart = algorithm.getNodeStart().getCoordinate();
		Coordinate cTarget = algorithm.getNodeTarget().getCoordinate();

		double r = 6;
		double w = 4;

		if (dragStart != null) {
			cStart = dragStart;
		}
		if (dragTarget != null) {
			cTarget = dragTarget;
		}

		Shape arcStart = ShapeUtil.createArc(cStart.getX(), cStart.getY(), r);
		Shape arcTarget = ShapeUtil
				.createArc(cTarget.getX(), cTarget.getY(), r);
		Shape arcStartIn = ShapeUtil.createArc(cStart.getX(), cStart.getY(), r
				- w / 2);
		Shape arcTargetIn = ShapeUtil.createArc(cTarget.getX(), cTarget.getY(),
				r - w / 2);
		Shape arcStartOut = ShapeUtil.createArc(cStart.getX(), cStart.getY(), r
				+ w / 2);
		Shape arcTargetOut = ShapeUtil.createArc(cTarget.getX(),
				cTarget.getY(), r + w / 2);

		painter.setStrokeWidth(w);
		painter.setColor(COLOR_NODE_START);
		painter.draw(arcStart);
		painter.setColor(COLOR_NODE_TARGET);
		painter.draw(arcTarget);

		painter.setColor(new Color(0xaaffffff, true));
		if (mouseOverStart == MouseOver.OVER) {
			painter.draw(arcStart);
		}
		if (mouseOverTarget == MouseOver.OVER) {
			painter.draw(arcTarget);
		}

		painter.setStrokeWidth(1.0);
		painter.setColor(COLOR_NODE_START_OUTLINE);
		painter.draw(arcStartOut);
		painter.draw(arcStartIn);
		painter.setColor(COLOR_NODE_TARGET_OUTLINE);
		painter.draw(arcTargetOut);
		painter.draw(arcTargetIn);

		painter.setStrokeWidth(2.0);
		painter.setColor(new Color(0xaaffffff, true));
		if (mouseOverStart == MouseOver.OVER
				|| mouseOverStart == MouseOver.ACTIVE) {
			painter.draw(arcStartOut);
			painter.draw(arcStartIn);
		}
		if (mouseOverTarget == MouseOver.OVER
				|| mouseOverTarget == MouseOver.ACTIVE) {
			painter.draw(arcTargetOut);
			painter.draw(arcTargetIn);
		}

		if (config.isDrawNodeNumbers()) {
			painter.setColor(COLOR_NODE_IDS);
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Coordinate c = shell.getNode(i).getCoordinate();
				painter.drawString(String.format("%d", i + 1),
						(float) c.getX() + 10, (float) c.getY());
			}
		}
	}

	private void paintCommonPath()
	{
		Data data = algorithm.getData();
		painter.setColor(COLOR_COMMON_PATH);
		for (int i = 0; i < data.getCommonLength() - 1; i++) {
			Node m = data.getCommon(i);
			Node n = data.getCommon(i + 1);
			Coordinate cm = m.getCoordinate();
			Coordinate cn = n.getCoordinate();
			painter.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
		}
	}

	private void paintPath(Side side)
	{
		Data data = algorithm.getData();
		if (side == Side.LEFT) {
			painter.setColor(COLOR_LEFT_PATH);
		} else {
			painter.setColor(COLOR_RIGHT_PATH);
		}
		Node m = data.getApex();
		for (int i = 0; i < data.getFunnelLength(side); i++) {
			Node n = data.get(side, i);
			Coordinate cm = m.getCoordinate();
			Coordinate cn = n.getCoordinate();
			painter.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
			m = n;
		}
		Node last = data.getLast(side);
		Coordinate c = last.getCoordinate();
		if (side == Side.LEFT) {
			painter.setColor(COLOR_LEFT_TOP);
		} else {
			painter.setColor(COLOR_RIGHT_TOP);
		}
		painter.fill(ShapeUtil.createArc(c.getX(), c.getY(), 4));
	}

}
