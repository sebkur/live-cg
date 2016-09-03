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
package de.topobyte.livecg.algorithms.frechet.freespace;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.painting.backend.awt.AwtPainter;

public class FreeSpaceDiagram extends JPanel implements EpsilonSettable,
		SizeProvider
{
	private static final long serialVersionUID = 5024820193840910054L;

	private AwtPainter painter;
	private FreeSpacePainterChains visualizationPainter;

	private int epsilon;

	public FreeSpaceDiagram(FreeSpaceConfig config, int epsilon, Chain chain1,
			Chain chain2)
	{
		this.epsilon = epsilon;
		painter = new AwtPainter(null);
		visualizationPainter = new FreeSpacePainterChains(config, epsilon,
				chain1, chain2, painter);
	}

	@Override
	public void setEpsilon(int epsilon)
	{
		this.epsilon = epsilon;
		repaint();
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		GraphicsUtil.useAntialiasing(g, true);

		painter.setGraphics(g);

		visualizationPainter.setEpsilon(epsilon);
		visualizationPainter.setWidth(getWidth());
		visualizationPainter.setHeight(getHeight());
		visualizationPainter.paint();
	}
}
