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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortuneConfig;

public class Settings extends JToolBar implements ItemListener
{

	private static final long serialVersionUID = -6537449209660520005L;

	private Canvas canvas;
	private FortuneConfig config;

	private JToggleButton[] buttons;

	private static final String TEXT_CIRCLES = "Circles";
	private static final String TEXT_BEACHLINE = "Beachline";
	private static final String TEXT_VORONOI = "Voronoi diagram";
	private static final String TEXT_DELAUNAY = "Delaunay triangulation";
	private static final String TEXT_DCEL = "DCEL";

	public Settings(Canvas canvas, FortuneConfig config)
	{
		this.canvas = canvas;
		this.config = config;

		setFloatable(false);

		String as[] = { TEXT_CIRCLES, TEXT_BEACHLINE, TEXT_VORONOI,
				TEXT_DELAUNAY, TEXT_DCEL };

		buttons = new JToggleButton[as.length];
		for (int i = 0; i < as.length; i++) {
			buttons[i] = new JToggleButton(as[i]);
			buttons[i].addItemListener(this);
			add(buttons[i]);
		}

		buttons[0].setSelected(config.isDrawCircles());
		buttons[1].setSelected(config.isDrawBeach());
		buttons[2].setSelected(config.isDrawVoronoiLines());
		buttons[3].setSelected(config.isDrawDelaunay());
		buttons[4].setSelected(config.isDrawDcel());
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		JToggleButton button = (JToggleButton) e.getItem();
		String s = button.getText();
		boolean flag = button.isSelected();
		if (s.equals(TEXT_CIRCLES)) {
			config.setDrawCircles(flag);
		} else if (s.equals(TEXT_BEACHLINE)) {
			config.setDrawBeach(flag);
		} else if (s.equals(TEXT_VORONOI)) {
			config.setDrawVoronoiLines(flag);
		} else if (s.equals(TEXT_DELAUNAY)) {
			config.setDrawDelaunay(flag);
		} else if (s.equals(TEXT_DCEL)) {
			config.setDrawDcel(flag);
		}
		canvas.repaint();
	}

}
