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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.scrolling.HasMargin;
import de.topobyte.livecg.core.scrolling.ScenePanel;
import de.topobyte.livecg.core.scrolling.ViewportWithSignals;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class MonotonePiecesPanel extends ScenePanel implements PolygonPanel,
		SizeProvider, ViewportWithSignals, HasMargin
{

	private static final long serialVersionUID = 2129465700417909129L;

	final static Logger logger = LoggerFactory
			.getLogger(MonotonePiecesPanel.class);

	private Map<Polygon, ColorCode> colorMap;

	private MonotonePiecesConfig polygonConfig;

	public MonotonePiecesPanel(MonotonePiecesAlgorithm algorithm,
			MonotonePiecesConfig polygonConfig)
	{
		super(algorithm.getScene());
		this.polygonConfig = polygonConfig;

		colorMap = ColorMapBuilder.buildColorMap(algorithm.getExtendedGraph());

		visualizationPainter = new MonotonePiecesPainter(algorithm,
				polygonConfig, colorMap, painter);
	}

	@Override
	public MonotonePiecesConfig getPolygonConfig()
	{
		return polygonConfig;
	}

	@Override
	public void settingsUpdated()
	{
		repaint();
	}

}
