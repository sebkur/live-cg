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
package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.Config;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortunePainter;
import de.topobyte.livecg.core.AlgorithmWatcher;
import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.painting.AwtPainter;

public class Canvas extends JPanel implements AlgorithmWatcher, SizeProvider
{

	private static final long serialVersionUID = 461591430129084653L;

	private FortunesSweep algorithm;
	private FortunePainter algorithmPainter;
	private AwtPainter painter;

	public Canvas(FortunesSweep algorithm, Config config, int width, int height)
	{
		this.algorithm = algorithm;

		painter = new AwtPainter(null);
		algorithmPainter = new FortunePainter(algorithm, config, painter);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e)
			{
				Point point = new Point(e.getPoint().x, e.getPoint().y);
				if (point.getX() > Canvas.this.algorithm.getSweepX()) {
					Canvas.this.algorithm.addSite(point, true);
					repaint();
				}
			}

		});
	}

	@Override
	public void updateAlgorithmStatus()
	{
		repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		painter.setGraphics(g2d);
		algorithmPainter.setWidth(getWidth());
		algorithmPainter.setHeight(getHeight());
		algorithmPainter.paint();
	}

	public void addRandomPoints()
	{
		int MARGIN = 20;
		int MINSIZE = 20;

		int sx = (int) Math.ceil(algorithm.getSweepX());
		int width = getWidth() - sx;
		int marginX = 0;
		int marginY = 0;
		if (width >= MARGIN * 2 + MINSIZE) {
			marginX = MARGIN;
		}
		if (getHeight() >= MARGIN * 2 + MINSIZE) {
			marginY = MARGIN;
		}
		if (width <= 0) {
			return;
		}
		Random random = new Random();
		for (int i = 0; i < 16; i++) {
			int x = random.nextInt(width - marginX * 2 - 1) + sx + marginX + 1;
			int y = random.nextInt(getHeight() - marginY * 2) + marginY;
			algorithm.addSite(new Point(x, y), true);
		}
		updateAlgorithmStatus();
	}

}
