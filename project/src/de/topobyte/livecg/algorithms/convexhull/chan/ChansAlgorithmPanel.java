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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import de.topobyte.livecg.core.AlgorithmWatcher;
import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.scrolling.ScenePanel;
import de.topobyte.livecg.util.SwingUtil;

public class ChansAlgorithmPanel extends ScenePanel implements SizeProvider,
		AlgorithmWatcher
{
	private static final long serialVersionUID = -788826737883369137L;

	private AwtPainter painter;
	private ChansAlgorithmPainter algorithmPainter;

	public ChansAlgorithmPanel(ChansAlgorithm algorithm)
	{
		super(scene(algorithm));

		painter = new AwtPainter(null);
		algorithmPainter = new ChansAlgorithmPainter(scene, algorithm, painter);
		super.algorithmPainter = algorithmPainter;

		algorithm.addWatcher(this);
	}

	private static Rectangle scene(ChansAlgorithm algorithm)
	{
		List<Polygon> polygons = algorithm.getPolygons();
		Rectangle scene = BoundingBoxes.get(polygons.get(0));
		for (Polygon p : polygons) {
			scene = BoundingBoxes.get(scene, BoundingBoxes.get(p));
		}
		scene = Rectangles.extend(scene, 15);
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
	public void updateAlgorithmStatus()
	{
		repaint();
	}

}
