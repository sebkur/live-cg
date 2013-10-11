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

package de.topobyte.livecg.algorithms.frechet.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.segment.LineEditorSegmentPane;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.ui.lineeditor.LineEditor;

public class DualLineEditorDistanceTerrain extends JPanel
{
	private static final long serialVersionUID = 3583790726202326121L;

	private LineEditor editor1;
	private LineEditor editor2;
	private LineEditorSegmentPane segmentPane;

	public DualLineEditorDistanceTerrain(int width, int height, Chain line1,
			Chain line2)
	{
		editor1 = new LineEditor(width, height, line1);
		editor2 = new LineEditor(width, height, line2);
		segmentPane = new LineEditorSegmentPane(editor1, editor2);

		segmentPane.update();

		editor1.setBorder(new TitledBorder("Segment P"));
		editor2.setBorder(new TitledBorder("Segment Q"));

		JPanel segmentPaneContainer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		segmentPaneContainer.add(segmentPane, c);
		segmentPaneContainer.setBorder(new TitledBorder("Distance terrain"));

		setLayout(new GridBagLayout());

		c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;

		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;

		c.gridx = 0;
		c.gridy = 1;
		add(editor1, c);

		c.gridx = 1;
		add(editor2, c);

		c.gridx = 2;
		add(segmentPaneContainer, c);
	}

}
