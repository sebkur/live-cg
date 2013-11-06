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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.scrolling.HasMargin;
import de.topobyte.livecg.core.scrolling.HasScene;
import de.topobyte.livecg.core.scrolling.ScenePanel;
import de.topobyte.livecg.core.scrolling.ViewportWithSignals;
import de.topobyte.livecg.util.SwingUtil;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class MonotonePiecesPanel extends ScenePanel implements PolygonPanel,
		SizeProvider, ViewportWithSignals, HasScene, HasMargin
{

	private static final long serialVersionUID = 2129465700417909129L;

	final static Logger logger = LoggerFactory
			.getLogger(MonotonePiecesPanel.class);

	private Map<Polygon, Color> colorMap;

	private Config polygonConfig = new Config();

	private AwtPainter painter;
	private MonotonePiecesPainter algorithmPainter;

	public MonotonePiecesPanel(MonotonePiecesAlgorithm algorithm)
	{
		super(scene(algorithm, 15));
		colorMap = ColorMapBuilder.buildColorMap(algorithm.getExtendedGraph());

		painter = new AwtPainter(null);
		algorithmPainter = new MonotonePiecesPainter(scene, algorithm,
				polygonConfig, colorMap, painter);
		super.algorithmPainter = algorithmPainter;
	}

	private static Rectangle scene(MonotonePiecesAlgorithm algorithm,
			double margin)
	{
		Rectangle bbox = BoundingBoxes.get(algorithm.getPolygon());
		Rectangle scene = Rectangles.extend(bbox, margin);
		return scene;
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		painter.setGraphics(g);
		algorithmPainter.setWidth(getWidth());
		algorithmPainter.setHeight(getHeight());
		algorithmPainter.paint();
	}

	@Override
	public Config getPolygonConfig()
	{
		return polygonConfig;
	}

	@Override
	public void settingsUpdated()
	{
		repaint();
	}

}
