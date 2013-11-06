/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.algorithms.polygon.monotonepieces;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

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
import de.topobyte.livecg.core.scrolling.Viewport;
import de.topobyte.livecg.core.scrolling.ViewportListener;
import de.topobyte.livecg.util.SwingUtil;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class MonotonePiecesPanel extends JPanel implements PolygonPanel,
		SizeProvider, Viewport, HasScene, HasMargin
{

	private static final long serialVersionUID = 2129465700417909129L;

	final static Logger logger = LoggerFactory
			.getLogger(MonotonePiecesPanel.class);

	private int margin = 15;

	private double positionX = 0;
	private double positionY = 0;
	private double zoom = 1;

	private Map<Polygon, Color> colorMap;

	private Config polygonConfig = new Config();

	private Rectangle scene;
	private AwtPainter painter;
	private MonotonePiecesPainter algorithmPainter;

	public MonotonePiecesPanel(MonotonePiecesAlgorithm algorithm)
	{
		colorMap = ColorMapBuilder.buildColorMap(algorithm.getExtendedGraph());

		Rectangle bbox = BoundingBoxes.get(algorithm.getPolygon());
		scene = Rectangles.extend(bbox, margin);

		painter = new AwtPainter(null);
		algorithmPainter = new MonotonePiecesPainter(scene, algorithm,
				polygonConfig, colorMap, painter);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				checkBounds();
			}

		});
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

	@Override
	public Rectangle getScene()
	{
		return scene;
	}

	@Override
	public double getMargin()
	{
		return margin;
	}

	@Override
	public double getPositionX()
	{
		return positionX;
	}

	@Override
	public double getPositionY()
	{
		return positionY;
	}

	@Override
	public double getZoom()
	{
		return zoom;
	}

	private void internalSetPositionX(double value)
	{
		positionX = value;
		algorithmPainter.setPositionX(value);
	}

	private void internalSetPositionY(double value)
	{
		positionY = value;
		algorithmPainter.setPositionY(value);
	}

	@Override
	public void setPositionX(double value)
	{
		internalSetPositionX(value);
		fireViewportListenersViewportChanged();
	}

	@Override
	public void setPositionY(double value)
	{
		internalSetPositionY(value);
		fireViewportListenersViewportChanged();
	}

	@Override
	public void setZoom(double zoom)
	{
		this.zoom = zoom;
		algorithmPainter.setZoom(zoom);
		checkBounds();
		fireViewportListenersZoomChanged();
		repaint();
	}

	private List<ViewportListener> viewportListeners = new ArrayList<ViewportListener>();

	@Override
	public void addViewportListener(ViewportListener listener)
	{
		viewportListeners.add(listener);
	}

	@Override
	public void removeViewportListener(ViewportListener listener)
	{
		viewportListeners.remove(listener);
	}

	private void fireViewportListenersViewportChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.viewportChanged();
		}
	}

	private void fireViewportListenersZoomChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.zoomChanged();
		}
	}

	private void checkBounds()
	{
		boolean update = false;
		if (-positionX + getWidth() / zoom > getScene().getWidth() + margin) {
			logger.debug("Moved out of viewport at right");
			internalSetPositionX(getWidth() / zoom - getScene().getWidth()
					- margin);
			update = true;
		}
		if (positionX > margin) {
			logger.debug("Scrolled too much to the left");
			internalSetPositionX(margin);
			update = true;
		}
		if (-positionY + getHeight() / zoom > getScene().getHeight() + margin) {
			logger.debug("Moved out of viewport at bottom");
			internalSetPositionY(getHeight() / zoom - getScene().getHeight()
					- margin);
			update = true;
		}
		if (positionY > margin) {
			logger.debug("Scrolled too much to the top");
			internalSetPositionY(margin);
			update = true;
		}
		if (update) {
			repaint();
		}
		fireViewportListenersViewportChanged();
	}
}
