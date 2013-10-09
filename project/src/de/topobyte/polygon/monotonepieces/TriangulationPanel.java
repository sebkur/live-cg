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
package de.topobyte.polygon.monotonepieces;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import de.topobyte.util.SwingUtil;
import de.topobyte.util.graph.Edge;
import de.topobyte.util.graph.Graph;

public class TriangulationPanel extends JPanel implements PolygonPanel
{

	private static final long serialVersionUID = 1265869392513220699L;

	private Polygon polygon;
	private TriangulationOperation triangulationOperation;
	private List<Diagonal> diagonals;
	private Graph<Polygon, Diagonal> graph;

	private Config polygonConfig = new Config();

	public TriangulationPanel(Polygon polygon)
	{
		this.polygon = polygon;
		triangulationOperation = new TriangulationOperation(polygon);
		diagonals = triangulationOperation.getDiagonals();

		SplitResult splitResult = DiagonalUtil.split(polygon, diagonals);
		graph = splitResult.getGraph();
	}

	@Override
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		Area shape = AwtHelper.toShape(polygon);
		g.setColor(new Color(0x66ff0000, true));
		g.fill(shape);

		g.setColor(Color.BLACK);
		Chain shell = polygon.getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		g.setColor(Color.BLUE);
		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		g.setColor(Color.GREEN);
		Collection<Polygon> nodes = graph.getNodes();
		for (Polygon p : nodes) {
			Coordinate cp = center(p);
			Set<Edge<Polygon, Diagonal>> edges = graph.getEdgesOut(p);
			for (Edge<Polygon, Diagonal> edge : edges) {
				Polygon q = edge.getTarget();
				Coordinate cq = center(q);
				g.drawLine((int) Math.round(cp.getX()),
						(int) Math.round(cp.getY()),
						(int) Math.round(cq.getX()),
						(int) Math.round(cq.getY()));
			}
		}

		if (polygonConfig.isDrawNodeNumbers()) {
			g.setColor(Color.BLACK);
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Node node = shell.getNode(i);
				Coordinate c = node.getCoordinate();
				g.drawString(String.format("%d", i + 1), (float) c.getX() + 10,
						(float) c.getY());
			}
		}
	}

	private Coordinate center(Polygon polygon)
	{
		double x = 0, y = 0;
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Coordinate c = shell.getNode(i).getCoordinate();
			x += c.getX();
			y += c.getY();
		}
		x /= shell.getNumberOfNodes();
		y /= shell.getNumberOfNodes();
		return new Coordinate(x, y);
	}

	@Override
	public Config getPolygonConfig()
	{
		return polygonConfig;
	}

	@Override
	public void settingsUpdated()
	{
		repaint();
	}
}
