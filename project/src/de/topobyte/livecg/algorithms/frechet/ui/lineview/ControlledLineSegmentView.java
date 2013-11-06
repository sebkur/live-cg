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
package de.topobyte.livecg.algorithms.frechet.ui.lineview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class ControlledLineSegmentView extends JPanel
{

	private static final long serialVersionUID = 6013079669477474258L;

	public ControlledLineSegmentView(LineSegmentView lineView)
	{
		super(new GridBagLayout());

		LineSegmentViewControl controls = new LineSegmentViewControl(lineView);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 0.0;

		c.gridy = 0;
		c.weighty = 0.0;
		add(controls, c);

		c.gridy = 1;
		c.weighty = 1.0;
		add(lineView, c);
	}
}
