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
package de.topobyte.livecg.algorithms.voronoi.fortune;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.status.EventPosition;
import de.topobyte.livecg.algorithms.voronoi.fortune.status.FortuneStatusParser;
import de.topobyte.livecg.algorithms.voronoi.fortune.status.PixelPosition;
import de.topobyte.livecg.algorithms.voronoi.fortune.status.Position;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortuneConfig;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortunePainter;
import de.topobyte.livecg.core.SetupResult;
import de.topobyte.livecg.core.VisualizationSetup;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentHelper;

public class FortunesSweepVisualizationSetup implements VisualizationSetup
{

	@Override
	public SetupResult setup(Content content, String statusArgument,
			Properties properties, double zoom)
	{
		List<Node> nodes = ContentHelper.collectNodes(content);
		FortunesSweep algorithm = new FortunesSweep();

		List<Point> sites = new ArrayList<Point>();
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			sites.add(new Point(c.getX() * zoom, c.getY() * zoom));
		}
		algorithm.setSites(sites);

		if (statusArgument != null) {
			try {
				Position status = FortuneStatusParser.parse(statusArgument);
				if (status instanceof PixelPosition) {
					PixelPosition pp = (PixelPosition) status;
					algorithm.setSweep(pp.getPosition());
				} else if (status instanceof EventPosition) {
					EventPosition ep = (EventPosition) status;
					for (int i = 0; i < ep.getEvent(); i++) {
						if (algorithm.getEventQueue().size() != 0) {
							algorithm.nextEvent();
						} else {
							algorithm.setSweep(algorithm.getSweepX() + 1000);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Invalid format for status");
				System.exit(1);
			}
		}

		FortuneConfig config = new FortuneConfig();
		config.setDrawCircles(true);
		config.setDrawDcel(true);
		config.setDrawDelaunay(false);
		VisualizationPainter visualizationPainter = new FortunePainter(
				algorithm, config, null);

		Rectangle scene = content.getScene();
		int width = (int) Math.ceil(scene.getWidth() * zoom);
		int height = (int) Math.ceil(scene.getHeight() * zoom);

		return new SetupResult(width, height, visualizationPainter);
	}

}
