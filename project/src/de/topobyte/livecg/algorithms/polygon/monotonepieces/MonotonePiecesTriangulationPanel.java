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

import javax.swing.JPanel;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.scrolling.Viewport;
import de.topobyte.livecg.core.scrolling.ViewportListener;
import de.topobyte.livecg.util.SwingUtil;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class MonotonePiecesTriangulationPanel extends JPanel implements
		PolygonPanel, SizeProvider, Viewport
{

	private static final long serialVersionUID = 2129465700417909129L;

	private int margin = 15;

	private Map<Polygon, Color> colorMap;

	private Config polygonConfig = new Config();

	private Rectangle scene;
	private AwtPainter painter;
	private MonotonePiecesTriangulationPainter algorithmPainter;

	public MonotonePiecesTriangulationPanel(
			MonotonePiecesTriangulationAlgorithm algorithm)
	{
		colorMap = ColorMapBuilder.buildColorMap(algorithm.getExtendedGraph());

		Rectangle bbox = BoundingBoxes.get(algorithm.getPolygon());
		scene = Rectangles.extend(bbox, margin);

		painter = new AwtPainter(null);
		algorithmPainter = new MonotonePiecesTriangulationPainter(scene,
				algorithm, polygonConfig, colorMap, painter);
	}

	@Override
	public void paint(Graphics graphics)
	{
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

	@Override
	public double getPositionX()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPositionY()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getZoom()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setZoom(double value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addViewportListener(ViewportListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeViewportListener(ViewportListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositionX(double value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositionY(double value)
	{
		// TODO Auto-generated method stub

	}
}
