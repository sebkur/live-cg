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
package de.topobyte.livecg.algorithms.convexhull.chan;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.topobyte.livecg.core.SetupResult;
import de.topobyte.livecg.core.VisualizationSetup;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.core.status.ExplicitPosition;
import de.topobyte.livecg.core.status.FinishedPosition;
import de.topobyte.livecg.core.status.Position;
import de.topobyte.livecg.core.status.TwoLevelStatusParser;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class ChanVisualizationSetup implements VisualizationSetup
{

	@Override
	public SetupResult setup(Content content, String statusArgument,
			Properties properties, double zoom)
	{
		List<Polygon> viable = new ArrayList<>();
		for (Polygon polygon : content.getPolygons()) {
			if (polygon.getHoles().size() == 0) {
				viable.add(polygon);
			}
			// TODO: if polygon is convex
		}
		if (viable.size() < 2) {
			System.err.println("Not enough viable polygons");
			System.exit(1);
		}

		List<Polygon> polygons = new ArrayList<>();

		for (Polygon polygon : viable) {
			if (PolygonHelper.isCounterClockwiseOriented(polygon)) {
				polygons.add(CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));
			} else {
				Chain shell = polygon.getShell();
				try {
					polygon = new Polygon(ChainHelper.invert(shell), null);
					polygons.add(CopyUtil.copy(polygon,
							PolygonMode.REUSE_NOTHING));
				} catch (CloseabilityException e) {
					// Should not happen
				}
			}
		}
		ChansAlgorithm algorithm = new ChansAlgorithm(polygons);

		if (statusArgument != null) {
			try {
				Position status = TwoLevelStatusParser.parse(statusArgument);
				if (status instanceof FinishedPosition) {
					while (!algorithm.isFinished()) {
						algorithm.nextStep();
					}
				} else if (status instanceof ExplicitPosition) {
					ExplicitPosition pos = (ExplicitPosition) status;
					while (algorithm.getNumberOfNodesOnHull() < pos.getMajor()) {
						algorithm.nextStep();
					}
					for (int i = 0; i < pos.getMinor(); i++) {
						algorithm.nextStep();
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid format for status");
				System.exit(1);
			}
		}

		ChanConfig config = new ChanConfig();

		ChanPropertyParser propertyParser = new ChanPropertyParser(config);
		propertyParser.parse(properties);

		VisualizationPainter visualizationPainter = new ChansAlgorithmPainter(
				algorithm, config, null);

		Rectangle scene = algorithm.getScene();

		int width = (int) Math.ceil(scene.getWidth() * zoom);
		int height = (int) Math.ceil(scene.getHeight() * zoom);

		return new SetupResult(width, height, visualizationPainter);
	}

}
