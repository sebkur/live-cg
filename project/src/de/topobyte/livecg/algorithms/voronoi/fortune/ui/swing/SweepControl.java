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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.core.algorithm.AlgorithmWatcher;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.painting.Color;

public class SweepControl extends JComponent implements AlgorithmWatcher
{

	private static final long serialVersionUID = 2560101765472976128L;

	private Color COLOR_SWEEP_CONTROL_BG = LiveConfig
			.getColor("algorithm.voronoi.fortune.colors.sweep.control.background");
	private Color COLOR_SWEEP_CONTROL_HANDLE = LiveConfig
			.getColor("algorithm.voronoi.fortune.colors.sweep.control.handle");

	private FortunesSweep algorithm;

	private int height = 30;
	private int ch = 20;
	private int cw = 20;

	public SweepControl(FortunesSweep algorithm)
	{
		this.algorithm = algorithm;
		setPreferredSize(new Dimension(getPreferredSize().width, height));
		ControlMouseAdapter controlMouseListener = new ControlMouseAdapter();
		addMouseListener(controlMouseListener);
		addMouseMotionListener(controlMouseListener);
	}

	@Override
	public void paintComponent(Graphics gr)
	{
		Graphics2D g = (Graphics2D) gr;

		g.setColor(new java.awt.Color(COLOR_SWEEP_CONTROL_BG.getARGB(), true));
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(new java.awt.Color(COLOR_SWEEP_CONTROL_HANDLE.getARGB(),
				true));
		g.fillRect((int) Math.round(algorithm.getSweepX()) - cw / 2,
				getHeight() / 2 - ch / 2, cw, ch);
	}

	@Override
	public void updateAlgorithmStatus()
	{
		repaint();
	}

	private class ControlMouseAdapter extends MouseAdapter
	{
		private boolean pressed = false;

		@Override
		public void mousePressed(MouseEvent e)
		{
			pressed = true;
			Point point = e.getPoint();
			algorithm.setSweep(point.x);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			pressed = false;
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			if (pressed) {
				Point point = e.getPoint();
				int x = point.x;
				if (x < 0) {
					x = 0;
				}
				algorithm.setSweep(x);
			}
		}
	}
}
