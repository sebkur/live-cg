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
package de.topobyte.livecg.geometryeditor.geometryeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusBar extends JPanel
{

	private static final long serialVersionUID = -6453279448326406961L;

	private JLabel label;

	public StatusBar()
	{
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		label = new JLabel();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridx = 0;
		c.gridy = 0;

		add(label, c);
	}

	public void setText(String text)
	{
		label.setText(text);
	}
}
