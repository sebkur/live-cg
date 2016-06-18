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
package de.topobyte.livecg.algorithms.polygon.triangulation.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.topobyte.livecg.algorithms.polygon.monotone.MonotoneTriangulationOperation;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesOperation;
import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class TriangulationOperation
{
	private List<Diagonal> allDiagonals = new ArrayList<>();

	public TriangulationOperation(Polygon polygon)
	{
		MonotonePiecesOperation monotonePiecesOperation = new MonotonePiecesOperation(
				polygon);
		List<Polygon> monotonePieces = monotonePiecesOperation
				.getMonotonePieces();

		allDiagonals.addAll(monotonePiecesOperation.getDiagonals());
		for (Polygon monotonePolygon : monotonePieces) {
			MonotoneTriangulationOperation monotoneTriangulationOperation = new MonotoneTriangulationOperation(
					monotonePolygon);
			List<Diagonal> diagonals = monotoneTriangulationOperation
					.getDiagonals();
			allDiagonals.addAll(diagonals);
		}
	}

	public List<Diagonal> getDiagonals()
	{
		return Collections.unmodifiableList(allDiagonals);
	}
}
