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
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.painting.Color;

public class MonotonePiecesTriangulationPainter extends MonotonePiecesPainter
{

	private List<List<Diagonal>> allDiagonals;

	public MonotonePiecesTriangulationPainter(AwtPainter painter,
			Polygon polygon, MonotonePiecesOperation monotonePiecesOperation,
			List<Polygon> monotonePieces, List<List<Diagonal>> allDiagonals,
			Config polygonConfig, Map<Polygon, java.awt.Color> colorMap)
	{
		super(polygon, monotonePiecesOperation, monotonePieces, polygonConfig,
				colorMap, painter);
		this.allDiagonals = allDiagonals;
	}

	@Override
	public void paint()
	{
		super.paint();

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
