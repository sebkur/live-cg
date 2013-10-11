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
package de.topobyte.livecg.algorithms.polygon.monotonepieces;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.IntRing;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.util.graph.Edge;
import de.topobyte.livecg.util.graph.Graph;

public class TriangulationPainter extends BasicAlgorithmPainter
{

	private Polygon polygon;
	private List<Diagonal> diagonals;
	private Graph<Polygon, Diagonal> graph;
	private Config polygonConfig;

	public TriangulationPainter(AwtPainter painter, Polygon polygon,
			List<Diagonal> diagonals, Graph<Polygon, Diagonal> graph,
			Config polygonConfig)
	{
		super(painter);
		this.polygon = polygon;
		this.diagonals = diagonals;
		this.graph = graph;
		this.polygonConfig = polygonConfig;
	}

	public void paint()
	{

		painter.setColor(new Color(java.awt.Color.WHITE.getRGB()));
		painter.fillRect(0, 0, width, height);

		painter.setColor(new Color(0x66ff0000));
		painter.fillPolygon(polygon);

		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		Chain shell = polygon.getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			painter.drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
		}

		painter.setColor(new Color(java.awt.Color.BLUE.getRGB()));
		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			painter.drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
		}

		painter.setColor(new Color(java.awt.Color.GREEN.getRGB()));
		Collection<Polygon> nodes = graph.getNodes();
		for (Polygon p : nodes) {
			Coordinate cp = center(p);
			Set<Edge<Polygon, Diagonal>> edges = graph.getEdgesOut(p);
			for (Edge<Polygon, Diagonal> edge : edges) {
				Polygon q = edge.getTarget();
				Coordinate cq = center(q);
				painter.drawLine(cp.getX(), cp.getY(), cq.getX(), cq.getY());
			}
		}

		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		if (polygonConfig.isDrawNodeNumbers()) {
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Node node = shell.getNode(i);
				Coordinate c = node.getCoordinate();
				painter.drawString(String.format("%d", i + 1), c.getX() + 10,
						c.getY());
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
}
