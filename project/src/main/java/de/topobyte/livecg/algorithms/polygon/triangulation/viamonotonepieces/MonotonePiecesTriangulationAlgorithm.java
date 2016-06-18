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
package de.topobyte.livecg.algorithms.polygon.triangulation.viamonotonepieces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.algorithms.polygon.monotone.MonotoneTriangulationOperation;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.SplitResult;
import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.algorithms.polygon.util.DiagonalUtil;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class MonotonePiecesTriangulationAlgorithm extends
		MonotonePiecesAlgorithm
{

	private List<List<Diagonal>> allDiagonals;
	private Map<Polygon, SplitResult> splitResults;

	public MonotonePiecesTriangulationAlgorithm(Polygon polygon)
	{
		super(polygon);
	}

	@Override
	public void execute()
	{
		super.execute();
		System.out.println("execute: " + this.getClass().getSimpleName());

		allDiagonals = new ArrayList<>();
		splitResults = new HashMap<>();

		List<Polygon> monotonePieces = getMonotonePieces();
		for (Polygon monotonePolygon : monotonePieces) {
			MonotoneTriangulationOperation monotoneTriangulationOperation = new MonotoneTriangulationOperation(
					monotonePolygon);
			List<Diagonal> diagonals = monotoneTriangulationOperation
					.getDiagonals();
			allDiagonals.add(diagonals);

			SplitResult splitResult = DiagonalUtil.split(monotonePolygon,
					diagonals);
			splitResults.put(monotonePolygon, splitResult);
		}

	}

	public List<List<Diagonal>> getAllDiagonals()
	{
		return allDiagonals;
	}

	public SplitResult getSplitResult(Polygon piece)
	{
		return splitResults.get(piece);
	}
}
