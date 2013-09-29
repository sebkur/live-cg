/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

package de.topobyte.frechet.ui.lineeditor;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.ui.geometryeditor.ScaleMouseListener;
import de.topobyte.livecg.ui.geometryeditor.scale.Scale;
import de.topobyte.livecg.ui.geometryeditor.scale.ScaleX;
import de.topobyte.livecg.ui.geometryeditor.scale.ScaleY;

public class LineEditor extends JPanel
{

	private static final long serialVersionUID = 6938632987085713657L;

	private LineEditPane editPane;

	public LineEditor(int width, int height, Chain line)
	{
		editPane = new LineEditPane(width, height, line);
		Scale scaleX = new ScaleX();
		Scale scaleY = new ScaleY();

		ScaleMouseListener scaleMouseListener = new ScaleMouseListener(
				scaleX, scaleY);
		editPane.addMouseMotionListener(scaleMouseListener);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(editPane, c);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.0;
		add(scaleX, c);

		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0;
		add(scaleY, c);
	}

	public LineEditPane getEditPane()
	{
		return editPane;
	}
}
