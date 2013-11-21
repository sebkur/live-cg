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

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.util.SwingUtil;

public class DistanceTerrain extends JPanel implements SizeProvider
{

	private static final long serialVersionUID = -336337844015240678L;

	private AwtPainter painter;
	private DistanceTerrainPainterChains terrainPainter;

	public DistanceTerrain(Config config, Chain chain1, Chain chain2)
	{
		painter = new AwtPainter(null);
		terrainPainter = new DistanceTerrainPainterChains(config, chain1, chain2,
				painter);
	}

	public void update()
	{
		repaint();
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		painter.setGraphics(g);
		terrainPainter.setWidth(getWidth());
		terrainPainter.setHeight(getHeight());
		terrainPainter.paint();
	}
}
