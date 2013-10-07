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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
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
import de.topobyte.util.ShapeUtil;
import de.topobyte.util.SwingUtil;
import de.topobyte.util.graph.Edge;

public class ShortestPathPanel extends JPanel
{

	private static final long serialVersionUID = 7441840910845794124L;

	private ShortestPathAlgorithm algorithm;
	private Config config;

	public ShortestPathPanel(ShortestPathAlgorithm algorithm, Config config)
	{
		this.algorithm = algorithm;
		this.config = config;
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		Area shape = AwtHelper.toShape(algorithm.getPolygon());
		g.setColor(new Color(0x66ff0000, true));
		g.fill(shape);

		List<Polygon> triangles = algorithm.getSleeve().getPolygons();
		Color colorTriangle0 = new Color(0x66ffffff, true);
		Color colorTriangle1 = new Color(0x66ffff00, true);
		for (int i = 0; i < triangles.size(); i++) {
			if (i < algorithm.getStatus()) {
				g.setColor(colorTriangle1);
			} else {
				g.setColor(colorTriangle0);
			}
			Polygon triangle = triangles.get(i);
			g.fill(AwtHelper.toShape(triangle));
		}

		g.setColor(Color.BLACK);
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
			g.setColor(Color.BLUE);
			if (algorithm.getSleeve().getDiagonals().contains(diagonal)) {
				g.setColor(Color.RED.darker());
			}
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		if (config.isDrawDualGraph()) {
			g.setColor(Color.GREEN);
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

		Path leftPath = algorithm.getLeftPath();
		Path rightPath = algorithm.getRightPath();
		paintPath(g, leftPath, true);
		paintPath(g, rightPath, false);

		Coordinate cStart = algorithm.getNodeStart().getCoordinate();
		Coordinate cTarget = algorithm.getNodeTarget().getCoordinate();
		Arc2D arcStart = ShapeUtil.createArc(cStart.getX(), cStart.getY(), 5);
		Arc2D arcTarget = ShapeUtil
				.createArc(cTarget.getX(), cTarget.getY(), 5);
		g.setColor(Color.RED);
		g.draw(arcStart);
		g.setColor(Color.GREEN);
		g.draw(arcTarget);

		g.setColor(Color.BLACK);
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Coordinate c = shell.getNode(i).getCoordinate();
			g.drawString(String.format("%d", i + 1), (float) c.getX() + 10,
					(float) c.getY());
		}
	}

	private void paintPath(Graphics2D g, Path path, boolean left)
	{
		if (path == null) {
			return;
		}
		g.setColor(Color.MAGENTA);
		List<Node> nodes = path.getNodes();
		Node m = nodes.get(0);
		for (int i = 1; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			Coordinate cm = m.getCoordinate();
			Coordinate cn = n.getCoordinate();
			g.drawLine((int) Math.round(cm.getX()),
					(int) Math.round(cm.getY()), (int) Math.round(cn.getX()),
					(int) Math.round(cn.getY()));
			m = n;
		}
		Coordinate c = nodes.get(nodes.size()- 1).getCoordinate();
		if (left) {
			g.setColor(Color.YELLOW);
		}else {
			g.setColor(Color.BLUE);
		}
		g.draw(ShapeUtil.createArc(c.getX(), c.getY(), 5));
	}

}
