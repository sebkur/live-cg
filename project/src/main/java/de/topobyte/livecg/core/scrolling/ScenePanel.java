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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.core.painting.backend.awt.AwtPainter;
import de.topobyte.viewports.BaseScenePanel;
import de.topobyte.viewports.geometry.Rectangle;

public class ScenePanel extends BaseScenePanel
{

	private static final long serialVersionUID = 7575070738666558877L;

	final static Logger logger = LoggerFactory.getLogger(ScenePanel.class);

	protected AwtPainter painter;

	protected VisualizationPainter visualizationPainter;

	public ScenePanel(Rectangle scene)
	{
		super(scene);

		painter = new AwtPainter(null);
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		GraphicsUtil.useAntialiasing(g, true);

		painter.setGraphics(g);
		visualizationPainter.setWidth(getWidth());
		visualizationPainter.setHeight(getHeight());
		visualizationPainter.paint();
	}

	@Override
	protected void internalSetZoom(double value)
	{
		super.internalSetZoom(value);
		visualizationPainter.setZoom(zoom);
	}

	@Override
	protected void internalSetPositionX(double value)
	{
		super.internalSetPositionX(value);
		visualizationPainter.setPositionX(value);
	}

	@Override
	protected void internalSetPositionY(double value)
	{
		super.internalSetPositionY(value);
		visualizationPainter.setPositionY(value);
	}

	@Override
	protected void checkBounds()
	{
		if (visualizationPainter == null) {
			logger.error(
					"visualizationPainter is null. Have you forgotten to set this field in your subclass?");
		}
		super.checkBounds();
	}

}
