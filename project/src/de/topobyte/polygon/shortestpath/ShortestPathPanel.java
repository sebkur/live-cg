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
package de.topobyte.polygon.shortestpath;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import de.topobyte.livecg.geometry.geom.AwtHelper;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.geom.PolygonHelper;
import de.topobyte.polygon.monotonepieces.Diagonal;
import de.topobyte.util.MouseOver;
import de.topobyte.util.ShapeUtil;
import de.topobyte.util.SwingUtil;
import de.topobyte.util.graph.Edge;

public class ShortestPathPanel extends JPanel
{

	private static final long serialVersionUID = 7441840910845794124L;

	private Color COLOR_BG = Color.WHITE;

	private Color COLOR_POLYGON_BG = new Color(0xDDDDDD);
	private Color COLOR_TRIANGLE_SLEEVE = new Color(0xBBBBBB);
	private Color COLOR_TRIANGLE_SLEEVE_DONE = new Color(0x3300ff00, true);

	private Color COLOR_POLYGON_EDGES = Color.BLACK;
	private Color COLOR_DIAGONALS_NONSLEEVE = new Color(0x666666);
	private Color COLOR_DIAGONALS_SLEEVE = new Color(0x666666);
	private Color COLOR_DUAL_GRAPH = new Color(0xEEEE00);

	private Color COLOR_NODE_START = Color.RED;
	private Color COLOR_NODE_TARGET = Color.GREEN;
	private Color COLOR_NODE_START_OUTLINE = Color.BLACK;
	private Color COLOR_NODE_TARGET_OUTLINE = Color.BLACK;

	private Color COLOR_APEX = Color.WHITE;
	private Color COLOR_LEFT_TOP = Color.BLUE;
	private Color COLOR_RIGHT_TOP = Color.RED;
	private Color COLOR_COMMON_PATH = Color.MAGENTA.darker();
	private Color COLOR_LEFT_PATH = Color.BLUE;
	private Color COLOR_RIGHT_PATH = Color.RED;

	private Color COLOR_NODE_IDS = Color.BLACK;

	private ShortestPathAlgorithm algorithm;
	private Config config;

	public ShortestPathPanel(ShortestPathAlgorithm algorithm, Config config)
	{
		this.algorithm = algorithm;
		this.config = config;
	}

	public ShortestPathAlgorithm getAlgorithm()
	{
		return algorithm;
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		g.setColor(COLOR_BG);
		g.fillRect(0, 0, getWidth(), getHeight());

		Area shape = AwtHelper.toShape(algorithm.getPolygon());
		g.setColor(COLOR_POLYGON_BG);
		g.fill(shape);

		List<Polygon> triangles = algorithm.getSleeve().getPolygons();
		for (int i = 0; i < triangles.size(); i++) {
			if (i < algorithm.getStatus()) {
				g.setColor(COLOR_TRIANGLE_SLEEVE_DONE);
			} else {
				g.setColor(COLOR_TRIANGLE_SLEEVE);
			}
			Polygon triangle = triangles.get(i);
			g.fill(AwtHelper.toShape(triangle));
		}

		g.setStroke(new BasicStroke(1.0f));

		g.setColor(COLOR_POLYGON_EDGES);
		Chain shell = algorithm.getPolygon().getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		for (Diagonal diagonal : algorithm.getTriangulationDiagonals()) {
			g.setColor(COLOR_DIAGONALS_NONSLEEVE);
			if (algorithm.getSleeve().getDiagonals().contains(diagonal)) {
				g.setColor(COLOR_DIAGONALS_SLEEVE);
			}
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		if (config.isDrawDualGraph()) {
			g.setColor(COLOR_DUAL_GRAPH);
			Collection<Polygon> nodes = algorithm.getGraph().getNodes();
			for (Polygon p : nodes) {
				Coordinate cp = PolygonHelper.center(p);
				Set<Edge<Polygon, Diagonal>> edges = algorithm.getGraph()
						.getEdgesOut(p);
				for (Edge<Polygon, Diagonal> edge : edges) {
					Polygon q = edge.getTarget();
					Coordinate cq = PolygonHelper.center(q);
					g.drawLine((int) Math.round(cp.getX()),
							(int) Math.round(cp.getY()),
							(int) Math.round(cq.getX()),
							(int) Math.round(cq.getY()));
				}
			}
		}

		g.setStroke(new BasicStroke(2.0f));

		Data data = algorithm.getData();
		if (data != null) {
			paintCommonPath(g);

			paintPath(g, Side.LEFT);
			paintPath(g, Side.RIGHT);
			g.setColor(COLOR_APEX);

			Coordinate c = data.getApex().getCoordinate();
			g.fill(ShapeUtil.createArc(c.getX(), c.getY(), 3));
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

		g.setStroke(new BasicStroke((float) w));
		g.setColor(COLOR_NODE_START);
		g.draw(arcStart);
		g.setColor(COLOR_NODE_TARGET);
		g.draw(arcTarget);

		g.setColor(new Color(0xaaffffff, true));
		if (mouseOverStart == MouseOver.OVER) {
			g.draw(arcStart);
		}
		if (mouseOverTarget == MouseOver.OVER) {
			g.draw(arcTarget);
		}

		g.setStroke(new BasicStroke(1.0f));
		g.setColor(COLOR_NODE_START_OUTLINE);
		g.draw(arcStartOut);
		g.draw(arcStartIn);
		g.setColor(COLOR_NODE_TARGET_OUTLINE);
		g.draw(arcTargetOut);
		g.draw(arcTargetIn);

		g.setStroke(new BasicStroke(2.0f));
		g.setColor(new Color(0xaaffffff, true));
		if (mouseOverStart == MouseOver.OVER
				|| mouseOverStart == MouseOver.ACTIVE) {
			g.draw(arcStartOut);
			g.draw(arcStartIn);
		}
		if (mouseOverTarget == MouseOver.OVER
				|| mouseOverTarget == MouseOver.ACTIVE) {
			g.draw(arcTargetOut);
			g.draw(arcTargetIn);
		}

		if (config.isDrawNodeNumbers()) {
			g.setColor(COLOR_NODE_IDS);
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Coordinate c = shell.getNode(i).getCoordinate();
				g.drawString(String.format("%d", i + 1), (float) c.getX() + 10,
						(float) c.getY());
			}
		}
	}

	private void paintCommonPath(Graphics2D g)
	{
		Data data = algorithm.getData();
		g.setColor(COLOR_COMMON_PATH);
		for (int i = 0; i < data.getCommonLength() - 1; i++) {
			Node m = data.getCommon(i);
			Node n = data.getCommon(i + 1);
			Coordinate cm = m.getCoordinate();
			Coordinate cn = n.getCoordinate();
			g.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
		}
	}

	private void paintPath(Graphics2D g, Side side)
	{
		Data data = algorithm.getData();
		if (side == Side.LEFT) {
			g.setColor(COLOR_LEFT_PATH);
		} else {
			g.setColor(COLOR_RIGHT_PATH);
		}
		Node m = data.getApex();
		for (int i = 0; i < data.getFunnelLength(side); i++) {
			Node n = data.get(side, i);
			Coordinate cm = m.getCoordinate();
			Coordinate cn = n.getCoordinate();
			g.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
			m = n;
		}
		Node last = data.getLast(side);
		Coordinate c = last.getCoordinate();
		if (side == Side.LEFT) {
			g.setColor(COLOR_LEFT_TOP);
		} else {
			g.setColor(COLOR_RIGHT_TOP);
		}
		g.fill(ShapeUtil.createArc(c.getX(), c.getY(), 3));
	}

	private MouseOver mouseOverStart = MouseOver.NONE;
	private MouseOver mouseOverTarget = MouseOver.NONE;

	private Coordinate dragStart = null;
	private Coordinate dragTarget = null;

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

	public void setDragStart(Coordinate dragStart)
	{
		this.dragStart = dragStart;
	}

	public void setDragTarget(Coordinate dragTarget)
	{
		this.dragTarget = dragTarget;
	}
}
