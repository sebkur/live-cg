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

import java.util.Map;
import java.util.Properties;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesConfig;
import de.topobyte.livecg.core.SetupResult;
import de.topobyte.livecg.core.VisualizationSetup;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;
import de.topobyte.viewports.geometry.Rectangle;

public class MonotonePiecesTriangulationVisualizationSetup implements
		VisualizationSetup
{

	@Override
	public SetupResult setup(Content content, String statusArgument,
			Properties properties, double zoom)
	{
		if (content.getPolygons().size() < 1) {
			System.err.println("This visualization requires a polygon");
			System.exit(1);
		}
		Polygon polygon = content.getPolygons().get(0);
		MonotonePiecesTriangulationAlgorithm algorithm = new MonotonePiecesTriangulationAlgorithm(
				polygon);
		MonotonePiecesConfig config = new MonotonePiecesConfig();
		Map<Polygon, ColorCode> colorMap = ColorMapBuilder
				.buildColorMap(algorithm.getExtendedGraph());
		VisualizationPainter visualizationPainter = new MonotonePiecesTriangulationPainter(
				algorithm, config, colorMap, null);

		Rectangle scene = algorithm.getScene();

		int width = (int) Math.ceil(scene.getWidth() * zoom);
		int height = (int) Math.ceil(scene.getHeight() * zoom);

		return new SetupResult(width, height, visualizationPainter);
	}

}
