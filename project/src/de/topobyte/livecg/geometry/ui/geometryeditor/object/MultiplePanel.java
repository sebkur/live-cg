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
package de.topobyte.livecg.geometry.ui.geometryeditor.object;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.topobyte.swing.layout.GridBagHelper;

public class MultiplePanel extends JPanel
{

	private static final long serialVersionUID = 1448044034372567014L;

	public MultiplePanel()
	{
		setLayout(new GridBagLayout());

		JLabel label = new JLabel("multiple active objects");

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;

		GridBagHelper.setGxGy(c, 0, 0);
		add(label, c);

		GridBagHelper.setGxGy(c, 0, 1);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(new JPanel(), c);
	}

}
