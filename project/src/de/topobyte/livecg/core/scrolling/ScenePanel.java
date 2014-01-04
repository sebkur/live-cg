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
package de.topobyte.livecg.core.scrolling;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.painting.backend.awt.AwtPainter;
import de.topobyte.livecg.util.SwingUtil;

public class ScenePanel extends JPanel implements ViewportWithSignals,
		HasScene, HasMargin
{

	private static final long serialVersionUID = 7575070738666558877L;

	final static Logger logger = LoggerFactory.getLogger(ScenePanel.class);

	protected Rectangle scene;

	protected int margin = 15;

	protected double positionX = 0;
	protected double positionY = 0;
	protected double zoom = 1;

	protected AwtPainter painter;

	protected AlgorithmPainter algorithmPainter;

	public ScenePanel(Rectangle scene)
	{
		this.scene = scene;

		painter = new AwtPainter(null);

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

	private void internalSetZoom(double value)
	{
		zoom = value;
		algorithmPainter.setZoom(value);
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
		setZoomCentered(zoom);
	}

	public void setZoomCentered(double zoom)
	{
		double mx = -positionX + getWidth() / this.zoom / 2.0;
		double my = -positionY + getHeight() / this.zoom / 2.0;

		internalSetZoom(zoom);
		internalSetPositionX(getWidth() / zoom / 2.0 - mx);
		internalSetPositionY(getHeight() / zoom / 2.0 - my);

		checkBounds();
		fireViewportListenersZoomChanged();
		repaint();
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
}
