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
package de.topobyte.livecg.algorithms.frechet.distanceterrain;

import java.util.List;
import java.util.Properties;

import de.topobyte.livecg.core.SetupResult;
import de.topobyte.livecg.core.VisualizationSetup;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class DistanceTerrainVisualizationSetup implements VisualizationSetup
{

	@Override
	public SetupResult setup(Content content, String statusArgument,
			Properties properties, double zoom)
	{
		List<Chain> chains = content.getChains();
		if (chains.size() < 2) {
			System.err.println("Not enough chains");
			System.exit(1);
		}
		Chain chain1 = chains.get(0);
		Chain chain2 = chains.get(1);
		DistanceTerrainConfig config = new DistanceTerrainConfig();

		new DistanceTerrainPropertyParser(config).parse(properties);

		VisualizationPainter visualizationPainter = new DistanceTerrainPainterChains(
				config, chain1, chain2, null);
		int cellSize = 50;
		int width = chain1.getNumberOfNodes() * cellSize;
		int height = chain2.getNumberOfNodes() * cellSize;

		return new SetupResult(width, height, visualizationPainter);
	}

}
