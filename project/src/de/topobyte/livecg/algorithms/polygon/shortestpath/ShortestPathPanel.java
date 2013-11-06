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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.awt.Graphics;
import java.awt.Graphics2D;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.scrolling.ScenePanel;
import de.topobyte.livecg.util.SwingUtil;

public class ShortestPathPanel extends ScenePanel implements SizeProvider
{

	private static final long serialVersionUID = 7441840910845794124L;

	private ShortestPathAlgorithm algorithm;

	private AwtPainter painter;
	private ShortestPathPainter algorithmPainter;

	public ShortestPathPanel(ShortestPathAlgorithm algorithm, Config config)
	{
		super(scene(algorithm.getPolygon(), 15));
		this.algorithm = algorithm;

		painter = new AwtPainter(null);
		algorithmPainter = new ShortestPathPainter(scene, algorithm, config,
				painter);
		super.algorithmPainter = algorithmPainter;
	}

	private static Rectangle scene(Polygon polygon, double margin)
	{
		Rectangle bbox = BoundingBoxes.get(polygon);
		Rectangle scene = Rectangles.extend(bbox, margin);
		return scene;
	}

	public ShortestPathAlgorithm getAlgorithm()
	{
		return algorithm;
	}

	public ShortestPathPainter getPainter()
	{
		return algorithmPainter;
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

}
