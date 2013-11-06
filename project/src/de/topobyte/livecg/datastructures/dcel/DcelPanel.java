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
package de.topobyte.livecg.datastructures.dcel;

import java.awt.Graphics;
import java.awt.Graphics2D;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.scrolling.ScenePanel;
import de.topobyte.livecg.util.SwingUtil;

public class DcelPanel extends ScenePanel implements SizeProvider
{

	private static final long serialVersionUID = 8978186265217218174L;

	private DcelConfig config;
	private AwtPainter painter;
	private DcelPainter algorithmPainter;

	public DcelPanel(DCEL dcel, DcelConfig config)
	{
		super(scene(dcel));
		this.config = config;

		painter = new AwtPainter(null);
		algorithmPainter = new InstanceDcelPainter(scene, dcel, config, painter);
		super.algorithmPainter = algorithmPainter;
	}

	private static Rectangle scene(DCEL dcel)
	{
		Rectangle bbox = DcelUtil.getBoundingBox(dcel);
		Rectangle scene = Rectangles.extend(bbox, 15);
		return scene;
	}

	public DcelConfig getConfig()
	{
		return config;
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

	public void settingsUpdated()
	{
		repaint();
	}

}
