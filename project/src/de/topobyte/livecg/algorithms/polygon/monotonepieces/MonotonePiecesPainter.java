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

import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.IntRing;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;

public class MonotonePiecesPainter extends BasicAlgorithmPainter
{

	private Polygon polygon;
	private MonotonePiecesOperation monotonePiecesOperation;
	private List<Polygon> monotonePieces;
	private Config polygonConfig;
	private Map<Polygon, java.awt.Color> colorMap;

	public MonotonePiecesPainter(AwtPainter painter, Polygon polygon,
			MonotonePiecesOperation monotonePiecesOperation,
			List<Polygon> monotonePieces, Config polygonConfig,
			Map<Polygon, java.awt.Color> colorMap)
	{
		super(painter);
		this.polygon = polygon;
		this.monotonePiecesOperation = monotonePiecesOperation;
		this.monotonePieces = monotonePieces;
		this.polygonConfig = polygonConfig;
		this.colorMap = colorMap;
	}

	public void paint()
	{
		painter.setColor(new Color(java.awt.Color.WHITE.getRGB()));
		painter.fillRect(0, 0, width, height);

		painter.setColor(new Color(0x66ff0000, true));
		painter.fillPolygon(polygon);

		for (int i = 0; i < monotonePieces.size(); i++) {
			Polygon piece = monotonePieces.get(i);
			java.awt.Color color = colorMap.get(piece);
			painter.setColor(new Color(color.getRGB()));
			painter.fillPolygon(piece);
		}

		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));

		Chain shell = polygon.getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			painter.drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
		}

		List<Diagonal> diagonals = monotonePiecesOperation.getDiagonals();

		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			painter.drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
		}

		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
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
				painter.fill(arc);
				break;
			case START:
				painter.draw(rect);
				break;
			case END:
				painter.fill(rect);
				break;
			case SPLIT:
				triangle.moveTo(c.getX(), c.getY() - triangleSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.closePath();
				painter.fill(triangle);
				break;
			case MERGE:
				triangle.moveTo(c.getX(), c.getY() + arcSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.closePath();
				painter.fill(triangle);
				break;
			default:
				break;
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
}
