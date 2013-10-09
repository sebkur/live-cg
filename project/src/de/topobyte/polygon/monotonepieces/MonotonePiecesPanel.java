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
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import de.topobyte.livecg.geometry.geom.AwtHelper;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.util.SwingUtil;
import de.topobyte.util.graph.Graph;

public class MonotonePiecesPanel extends JPanel
{

	private static final long serialVersionUID = 2129465700417909129L;

	private Polygon polygon;
	private MonotonePiecesOperation monotonePiecesOperation;

	private List<Polygon> monotonePieces;
	private Graph<Polygon, Diagonal> graph;

	private Map<Polygon, Color> colorMap;

	public MonotonePiecesPanel(Polygon polygon)
	{
		this.polygon = polygon;
		monotonePiecesOperation = new MonotonePiecesOperation(polygon);
		SplitResult split = monotonePiecesOperation
				.getMonotonePiecesWithGraph();
		monotonePieces = split.getPolygons();
		graph = split.getGraph();

		colorMap = ColorMapBuilder.buildColorMap(graph);
	}

	@Override
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		Area shape = AwtHelper.toShape(polygon);
		g.setColor(new Color(0x66ff0000, true));
		g.fill(shape);

		for (int i = 0; i < monotonePieces.size(); i++) {
			Polygon piece = monotonePieces.get(i);
			shape = AwtHelper.toShape(piece);
			Color color = colorMap.get(piece);
			g.setColor(color);
			g.fill(shape);
		}

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

		List<Diagonal> diagonals = monotonePiecesOperation.getDiagonals();

		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		g.setColor(Color.BLACK);
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			VertexType type = monotonePiecesOperation.getType(node);
			Coordinate c = node.getCoordinate();

			double arcSize = 6;
			double rectSize = 6;
			double triangleSize = 8;

			Arc2D arc = new Arc2D.Double(c.getX() - arcSize / 2, c.getY()
					- arcSize / 2, arcSize, arcSize, 0, 360, Arc2D.CHORD);
			Rectangle2D rect = new Rectangle2D.Double(c.getX() - rectSize / 2,
					c.getY() - rectSize / 2, rectSize, rectSize);
			Path2D triangle = new Path2D.Double();

			switch (type) {
			case REGULAR:
				g.fill(arc);
				break;
			case START:
				g.draw(rect);
				break;
			case END:
				g.fill(rect);
				break;
			case SPLIT:
				triangle.moveTo(c.getX(), c.getY() - triangleSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			case MERGE:
				triangle.moveTo(c.getX(), c.getY() + arcSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			default:
				break;
			}

			g.drawString(String.format("%d", i + 1), (float) c.getX() + 10,
					(float) c.getY());
		}
	}
}
