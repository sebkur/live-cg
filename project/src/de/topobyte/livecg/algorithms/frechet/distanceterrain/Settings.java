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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class Settings extends JToolBar implements ItemListener
{

	private static final long serialVersionUID = -7316346525749613272L;

	private Config config;

	private JToggleButton[] buttons;

	private static final String TEXT_DRAW_GRID = "Grid";

	public Settings(Config config)
	{
		this.config = config;

		setFloatable(false);

		String as[] = { TEXT_DRAW_GRID };

		buttons = new JToggleButton[as.length];
		for (int i = 0; i < as.length; i++) {
			buttons[i] = new JToggleButton(as[i]);
			buttons[i].addItemListener(this);
			add(buttons[i]);
		}

		buttons[0].setSelected(config.isDrawGrid());
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		JToggleButton button = (JToggleButton) e.getItem();
		String s = button.getText();
		boolean flag = button.isSelected();
		if (s.equals(TEXT_DRAW_GRID)) {
			config.setDrawGrid(flag);
		}
		config.fireConfigChanged();
	}

}
