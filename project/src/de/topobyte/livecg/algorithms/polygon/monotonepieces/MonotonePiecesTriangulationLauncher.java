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

import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.ContentLauncher;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class MonotonePiecesTriangulationLauncher implements ContentLauncher
{

	@Override
	public void launch(Content content)
	{
		List<Polygon> polygons = content.getPolygons();
		if (polygons.size() < 1) {
			return;
		}
		Polygon polygon = polygons.get(0);
		polygon = CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING);

		MonotonePiecesTriangulationAlgorithm algorithm = new MonotonePiecesTriangulationAlgorithm(
				polygon);
		new MonotonePiecesTriangulationDialog(algorithm);
	}

}
