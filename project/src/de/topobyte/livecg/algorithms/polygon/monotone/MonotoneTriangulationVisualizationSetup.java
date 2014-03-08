/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.algorithms.polygon.monotone;

import java.util.Properties;

import de.topobyte.livecg.core.SetupResult;
import de.topobyte.livecg.core.VisualizationSetup;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.core.status.ExplicitPosition;
import de.topobyte.livecg.core.status.FinishedPosition;
import de.topobyte.livecg.core.status.Position;
import de.topobyte.livecg.core.status.TwoLevelStatusParser;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class MonotoneTriangulationVisualizationSetup implements
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
		MonotoneTriangulationAlgorithm algorithm = new MonotoneTriangulationAlgorithm(
				polygon);
		MonotoneTriangulationConfig config = new MonotoneTriangulationConfig();

		VisualizationPainter visualizationPainter = new MonotoneTriangulationPainter(
				algorithm, config, null);

		Rectangle scene = algorithm.getScene();

		int width = (int) Math.ceil(scene.getWidth() * zoom);
		int height = (int) Math.ceil(scene.getHeight() * zoom);

		if (statusArgument != null) {
			try {
				Position status = TwoLevelStatusParser.parse(statusArgument);
				if (status instanceof FinishedPosition) {
					int numberOfSteps = algorithm.getNumberOfSteps();
					algorithm.setStatus(numberOfSteps, 0);
				} else if (status instanceof ExplicitPosition) {
					ExplicitPosition pos = (ExplicitPosition) status;
					algorithm.setStatus(pos.getMajor(), pos.getMinor());
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid format for status");
				System.exit(1);
			}
		}

		return new SetupResult(width, height, visualizationPainter);
	}

}
