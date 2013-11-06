/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
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
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingAlgorithmPainter;

public class MonotonePiecesPainter extends TransformingAlgorithmPainter
{

	private Polygon polygon;
	private MonotonePiecesOperation monotonePiecesOperation;
	private List<Polygon> monotonePieces;
	private Config polygonConfig;
	private Map<Polygon, java.awt.Color> colorMap;

	public MonotonePiecesPainter(Rectangle scene,
			MonotonePiecesAlgorithm algorithm, Config polygonConfig,
			Map<Polygon, java.awt.Color> colorMap, Painter painter)
	{
		super(scene, painter);
		this.polygon = algorithm.getPolygon();
		this.monotonePiecesOperation = algorithm.getMonotonePiecesOperation();
		this.monotonePieces = algorithm.getMonotonePieces();
		this.polygonConfig = polygonConfig;
		this.colorMap = colorMap;
	}

	@Override
	public void paint()
	{
		preparePaint();

		fillBackground(new Color(0xffffff));

		fillPolygon();

		fillMonotonePieces();

		drawDiagonals();

		drawPolygon();

		drawNodes();

		drawLabels();
	}

	protected void fillPolygon()
	{
		painter.setColor(new Color(0x66ff0000, true));
		Polygon tpolygon = transformer.transform(polygon);
		painter.fillPolygon(tpolygon);
	}

	protected void fillMonotonePieces()
	{
		for (int i = 0; i < monotonePieces.size(); i++) {
			Polygon piece = monotonePieces.get(i);
			java.awt.Color color = colorMap.get(piece);
			painter.setColor(new Color(color.getRGB()));
			Polygon tpiece = transformer.transform(piece);
			painter.fillPolygon(tpiece);
		}
	}

	protected void drawDiagonals()
	{
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		List<Diagonal> diagonals = monotonePiecesOperation.getDiagonals();

		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			Coordinate t1 = transformer.transform(c1);
			Coordinate t2 = transformer.transform(c2);
			painter.drawLine(t1.getX(), t1.getY(), t2.getX(), t2.getY());
		}
	}

	protected void drawPolygon()
	{
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		Polygon tpolygon = transformer.transform(polygon);
		painter.drawPolygon(tpolygon);
	}

	protected void drawNodes()
	{
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));

		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			VertexType type = monotonePiecesOperation.getType(node);
			Coordinate c = node.getCoordinate();
			Coordinate t = transformer.transform(c);

			double arcSize = 6;
			double rectSize = 6;
			double triangleSize = 8;

			Arc2D arc = new Arc2D.Double(t.getX() - arcSize / 2, t.getY()
					- arcSize / 2, arcSize, arcSize, 0, 360, Arc2D.CHORD);
			Rectangle2D rect = new Rectangle2D.Double(t.getX() - rectSize / 2,
					t.getY() - rectSize / 2, rectSize, rectSize);
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
				triangle.moveTo(t.getX(), t.getY() - triangleSize / 2);
				triangle.lineTo(t.getX() - triangleSize / 2, t.getY()
						+ triangleSize / 2);
				triangle.lineTo(t.getX() + triangleSize / 2, t.getY()
						+ triangleSize / 2);
				triangle.closePath();
				painter.fill(triangle);
				break;
			case MERGE:
				triangle.moveTo(t.getX(), t.getY() + arcSize / 2);
				triangle.lineTo(t.getX() - triangleSize / 2, t.getY()
						- triangleSize / 2);
				triangle.lineTo(t.getX() + triangleSize / 2, t.getY()
						- triangleSize / 2);
				triangle.closePath();
				painter.fill(triangle);
				break;
			default:
				break;
			}

		}
	}

	protected void drawLabels()
	{
		Chain shell = polygon.getShell();
		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		if (polygonConfig.isDrawNodeNumbers()) {
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Node node = shell.getNode(i);
				Coordinate c = node.getCoordinate();
				Coordinate t = transformer.transform(c);
				painter.drawString(String.format("%d", i + 1), t.getX() + 10,
						t.getY());
			}
		}
	}
}
