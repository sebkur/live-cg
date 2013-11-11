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
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingAlgorithmPainter;
import de.topobyte.livecg.util.MouseOver;
import de.topobyte.livecg.util.ShapeUtil;
import de.topobyte.livecg.util.graph.Edge;

public class ShortestPathPainter extends TransformingAlgorithmPainter
{

	private String q(String property)
	{
		return "algorithm.polygon.shortestpath." + property;
	}

	private String qc(String property)
	{
		return q("colors." + property);
	}

	private Color COLOR_BG = LiveConfig.getColor(qc("background"));

	private Color COLOR_POLYGON_BG = LiveConfig.getColor(qc("polygon"));
	private Color COLOR_TRIANGLE_SLEEVE = LiveConfig.getColor(qc("sleeve"));
	private Color COLOR_TRIANGLE_SLEEVE_DONE = LiveConfig
			.getColor(qc("sleeve.done"));

	private Color COLOR_POLYGON_EDGES = LiveConfig.getColor(qc("boundary"));
	private Color COLOR_DIAGONALS_NONSLEEVE = LiveConfig
			.getColor(qc("diagonals"));
	private Color COLOR_DIAGONALS_SLEEVE = LiveConfig.getColor(qc("diagonals"));
	private Color COLOR_DUAL_GRAPH = LiveConfig.getColor(qc("dualgraph"));

	private Color COLOR_NODE_START = LiveConfig.getColor(qc("node.start"));
	private Color COLOR_NODE_TARGET = LiveConfig.getColor(qc("node.target"));
	private Color COLOR_NODE_START_OUTLINE = LiveConfig
			.getColor(qc("node.start.outline"));
	private Color COLOR_NODE_TARGET_OUTLINE = LiveConfig
			.getColor(qc("node.target.outline"));

	private Color COLOR_APEX = LiveConfig.getColor(qc("path.apex"));
	private Color COLOR_LEFT_TOP = LiveConfig.getColor(qc("path.left.top"));
	private Color COLOR_RIGHT_TOP = LiveConfig.getColor(qc("path.right.top"));
	private Color COLOR_COMMON_PATH = LiveConfig.getColor(qc("path.common"));
	private Color COLOR_LEFT_PATH = LiveConfig.getColor(qc("path.left"));
	private Color COLOR_RIGHT_PATH = LiveConfig.getColor(qc("path.right"));

	private Color COLOR_NODE_IDS = LiveConfig.getColor(qc("node.ids"));

	private double SIZE_FIRST_NODE = LiveConfig.getNumber(q("node.size.first"));
	private double SIZE_APEX = LiveConfig.getNumber(q("node.size.apex"));
	private double SIZE_FINAL_NODES = LiveConfig
			.getNumber(q("node.size.final"));
	private double SIZE_INTERMEDIATE_NODES = LiveConfig
			.getNumber(q("node.size.intermediate"));

	private double SIZE_ST_RADIUS = LiveConfig
			.getNumber(q("size.start_target.radius"));
	private double SIZE_ST_WIDTH = LiveConfig
			.getNumber(q("size.start_target.width"));

	private double LINE_WIDTH_POLYGON = LiveConfig
			.getNumber(q("width.polygon"));
	private double LINE_WIDTH_DIAGONALS = LiveConfig
			.getNumber(q("width.diagonals"));
	private double LINE_WIDTH_DUAL_GRAPH = LiveConfig
			.getNumber(q("width.dual_graph"));
	private double LINE_WIDTH_PATH = LiveConfig.getNumber(q("width.path"));

	private Color COLOR_NODE_HIGHLIGHT = LiveConfig
			.getColor(qc("node_highlight"));
	private double LINE_WIDTH_NODE_HIGHLIGHT = LiveConfig
			.getNumber(q("width.node_highlight"));

	private ShortestPathAlgorithm algorithm;
	private Config config;

	// Variables for handling start / target dragging

	private Coordinate dragStart = null;
	private Coordinate dragTarget = null;

	private MouseOver mouseOverStart = MouseOver.NONE;
	private MouseOver mouseOverTarget = MouseOver.NONE;

	public ShortestPathPainter(Rectangle scene,
			ShortestPathAlgorithm algorithm, Config config, Painter painter)
	{
		super(scene, painter);
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

	@Override
	public void paint()
	{
		preparePaint();

		super.fillBackground(COLOR_BG);

		Polygon tpolygon = transformer.transform(algorithm.getPolygon());

		/*
		 * Polygon
		 */

		painter.setColor(COLOR_POLYGON_BG);
		painter.fillPolygon(tpolygon);

		List<Polygon> triangles = algorithm.getSleeve().getPolygons();
		for (int i = 0; i < triangles.size(); i++) {
			if (i < algorithm.getStatus()) {
				painter.setColor(COLOR_TRIANGLE_SLEEVE_DONE);
			} else {
				painter.setColor(COLOR_TRIANGLE_SLEEVE);
			}
			Polygon triangle = triangles.get(i);
			Polygon ttriangle = transformer.transform(triangle);
			painter.fillPolygon(ttriangle);
		}

		painter.setStrokeWidth(LINE_WIDTH_POLYGON);
		painter.setColor(COLOR_POLYGON_EDGES);
		Chain shell = tpolygon.getShell();
		painter.drawChain(shell);

		/*
		 * Diagonals
		 */

		painter.setStrokeWidth(LINE_WIDTH_DIAGONALS);
		for (Diagonal diagonal : algorithm.getTriangulationDiagonals()) {
			painter.setColor(COLOR_DIAGONALS_NONSLEEVE);
			if (algorithm.getSleeve().getDiagonals().contains(diagonal)) {
				painter.setColor(COLOR_DIAGONALS_SLEEVE);
			}
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			Coordinate t1 = transformer.transform(c1);
			Coordinate t2 = transformer.transform(c2);
			painter.drawLine((int) Math.round(t1.getX()),
					(int) Math.round(t1.getY()), (int) Math.round(t2.getX()),
					(int) Math.round(t2.getY()));
		}

		/*
		 * Dual graph
		 */

		painter.setStrokeWidth(LINE_WIDTH_DUAL_GRAPH);
		if (config.isDrawDualGraph()) {
			painter.setColor(COLOR_DUAL_GRAPH);
			Collection<Polygon> nodes = algorithm.getGraph().getNodes();
			for (Polygon p : nodes) {
				Coordinate cp = PolygonHelper.center(p);
				Coordinate tp = transformer.transform(cp);
				Set<Edge<Polygon, Diagonal>> edges = algorithm.getGraph()
						.getEdgesOut(p);
				for (Edge<Polygon, Diagonal> edge : edges) {
					Polygon q = edge.getTarget();
					Coordinate cq = PolygonHelper.center(q);
					Coordinate tq = transformer.transform(cq);
					painter.drawLine((int) Math.round(tp.getX()),
							(int) Math.round(tp.getY()),
							(int) Math.round(tq.getX()),
							(int) Math.round(tq.getY()));
				}
			}
		}

		/*
		 * Funnel
		 */

		painter.setStrokeWidth(LINE_WIDTH_PATH);

		Data data = algorithm.getData();
		if (data != null) {
			paintCommonPath();

			paintPath(Side.LEFT);
			paintPath(Side.RIGHT);

			paintNodes(Side.LEFT);
			paintNodes(Side.RIGHT);

			boolean apexVisible = true;
			if (data.getFunnelLength(Side.LEFT) == 0
					|| data.getFunnelLength(Side.RIGHT) == 0) {
				apexVisible = false;
			}
			if (data.getFunnelLength(Side.LEFT) == 0
					&& data.getFunnelLength(Side.RIGHT) == 0) {
				apexVisible = true;
			}
			Coordinate c = data.getApex().getCoordinate();
			Coordinate t = transformer.transform(c);
			if (apexVisible) {
				painter.setColor(COLOR_APEX);
				painter.fill(ShapeUtil.createArc(t.getX(), t.getY(), SIZE_APEX));
			}
		}

		/*
		 * Substatus
		 */

		painter.setStrokeWidth(LINE_WIDTH_NODE_HIGHLIGHT);
		if (data != null) {
			int status = algorithm.getStatus();
			int steps = algorithm.getNumberOfSteps();
			int subStatus = algorithm.getSubStatus();
			if (status != steps - 1 && subStatus != 0) {
				Node next = algorithm.getNextNode();
				Node candiate = algorithm
						.getNthNodeOfFunnelTraversal(subStatus);
				Coordinate tn = transformer.transform(next.getCoordinate());
				Coordinate tc = transformer.transform(candiate.getCoordinate());
				painter.setColor(COLOR_NODE_HIGHLIGHT);
				painter.drawCircle(tn.getX(), tn.getY(), 8);
				painter.drawCircle(tc.getX(), tc.getY(), 8);
			}
		}

		/*
		 * Start / target nodes
		 */

		Coordinate cStart = algorithm.getNodeStart().getCoordinate();
		Coordinate cTarget = algorithm.getNodeTarget().getCoordinate();

		double r = SIZE_ST_RADIUS;
		double w = SIZE_ST_WIDTH;

		if (dragStart != null) {
			cStart = dragStart;
		}
		if (dragTarget != null) {
			cTarget = dragTarget;
		}

		Coordinate tStart = transformer.transform(cStart);
		Coordinate tTarget = transformer.transform(cTarget);

		Shape arcStart = ShapeUtil.createArc(tStart.getX(), tStart.getY(), r);
		Shape arcTarget = ShapeUtil
				.createArc(tTarget.getX(), tTarget.getY(), r);
		Shape arcStartIn = ShapeUtil.createArc(tStart.getX(), tStart.getY(), r
				- w / 2);
		Shape arcTargetIn = ShapeUtil.createArc(tTarget.getX(), tTarget.getY(),
				r - w / 2);
		Shape arcStartOut = ShapeUtil.createArc(tStart.getX(), tStart.getY(), r
				+ w / 2);
		Shape arcTargetOut = ShapeUtil.createArc(tTarget.getX(),
				tTarget.getY(), r + w / 2);

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

		/*
		 * Node ids
		 */

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
			Coordinate cm = transformer.transform(m.getCoordinate());
			Coordinate cn = transformer.transform(n.getCoordinate());
			painter.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
		}
		for (int i = 0; i < data.getCommonLength() - 1; i++) {
			Coordinate c = data.getCommon(i).getCoordinate();
			Coordinate t = transformer.transform(c);
			painter.fill(ShapeUtil.createArc(t.getX(), t.getY(),
					i == 0 ? SIZE_FIRST_NODE : SIZE_INTERMEDIATE_NODES));
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
			Coordinate cm = transformer.transform(m.getCoordinate());
			Coordinate cn = transformer.transform(n.getCoordinate());
			painter.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
			m = n;
		}
	}

	private void paintNodes(Side side)
	{
		Data data = algorithm.getData();
		if (side == Side.LEFT) {
			painter.setColor(COLOR_LEFT_PATH);
		} else {
			painter.setColor(COLOR_RIGHT_PATH);
		}

		for (int i = 0; i < data.getFunnelLength(side); i++) {
			Coordinate c = transformer.transform(data.get(side, i)
					.getCoordinate());
			painter.fill(ShapeUtil.createArc(c.getX(), c.getY(),
					SIZE_INTERMEDIATE_NODES));
		}

		Node last = data.getLast(side);
		Coordinate c = transformer.transform(last.getCoordinate());
		if (side == Side.LEFT) {
			painter.setColor(COLOR_LEFT_TOP);
		} else {
			painter.setColor(COLOR_RIGHT_TOP);
		}
		painter.fill(ShapeUtil.createArc(c.getX(), c.getY(), SIZE_FINAL_NODES));
	}

}
