/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.util.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

import de.topobyte.swing.util.ImageLoader;

public abstract class ControlManager
{

	private Map<String, JButton> buttons = new HashMap<>();

	public JButton add(final String key, Icons icon, String tooltip)
	{
		return add(key, Icons.getPath(icon), tooltip);
	}

	public JButton add(final String key, String iconPath, String tooltip)
	{
		Icon icon = ImageLoader.load(iconPath);
		JButton button = new JButton(icon);
		button.setToolTipText(tooltip);
		buttons.put(key, button);

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				control(key);
			}
		});

		return button;
	}

	public JButton get(String id)
	{
		return buttons.get(id);
	}

	protected abstract void control(String key);
}
