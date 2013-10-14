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

import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.util.coloring.AlternatingColorMapBuilder;
import de.topobyte.livecg.util.graph.Graph;

public class MonotonePiecesTriangulationPainter extends MonotonePiecesPainter
{

	private MonotonePiecesTriangulationAlgorithm algorithm;
	private List<List<Diagonal>> allDiagonals;

	public MonotonePiecesTriangulationPainter(
			MonotonePiecesTriangulationAlgorithm algorithm,
			Config polygonConfig, Map<Polygon, java.awt.Color> colorMap,
			Painter painter)
	{
		super(algorithm, polygonConfig, colorMap, painter);
		this.algorithm = algorithm;
		this.allDiagonals = algorithm.getAllDiagonals();
	}

	@Override
	public void paint()
	{
		super.paint();

		java.awt.Color a = new java.awt.Color(0x00ffffff, true);
		java.awt.Color b = new java.awt.Color(0x77ffffff, true);

		List<Polygon> pieces = algorithm.getMonotonePieces();
		for (Polygon piece : pieces) {
			SplitResult splitResult = algorithm.getSplitResult(piece);
			Graph<Polygon, Diagonal> graph = splitResult.getGraph();
			Map<Polygon, java.awt.Color> colorMap = AlternatingColorMapBuilder
					.buildColorMap(graph, a, b);
			for (Polygon triangle : splitResult.getPolygons()) {
				java.awt.Color color = colorMap.get(triangle);
				if (color != null)
					painter.setColor(new Color(color.getRGB(), true));
				painter.fillPolygon(triangle);
			}
		}

		painter.setColor(new Color(java.awt.Color.BLACK.getRGB()));
		for (List<Diagonal> diagonalsT : allDiagonals) {
			for (Diagonal diagonal : diagonalsT) {
				Coordinate c1 = diagonal.getA().getCoordinate();
				Coordinate c2 = diagonal.getB().getCoordinate();
				painter.drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
			}
		}
	}
}
