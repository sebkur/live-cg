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
package de.topobyte.livecg.algorithms.frechet.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import de.topobyte.livecg.algorithms.frechet.freespace.Config;
import de.topobyte.livecg.algorithms.frechet.freespace.segment.SegmentEditorSegmentPane;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.geometryeditor.segmenteditor.SegmentEditor;

public class DualSegmentEditorFreespace extends JPanel
{
	private static final long serialVersionUID = 3583790726202326121L;

	private SegmentEditor editor1;
	private SegmentEditor editor2;
	private SegmentEditorSegmentPane segmentPane;

	private JSlider slider;

	public DualSegmentEditorFreespace(int width, int height, Chain line1,
			Chain line2, int epsilon)
	{
		Config config = new Config();
		editor1 = new SegmentEditor(width, height, line1);
		editor2 = new SegmentEditor(width, height, line2);
		segmentPane = new SegmentEditorSegmentPane(config, editor1, editor2,
				epsilon);

		segmentPane.update();

		editor1.setBorder(new TitledBorder("Segment P"));
		editor2.setBorder(new TitledBorder("Segment Q"));

		JPanel segmentPaneContainer = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		segmentPaneContainer.add(segmentPane, c);
		segmentPaneContainer.setBorder(new TitledBorder("Free space"));

		int maxEpsilon = (int) (width * 1.5);
		slider = new JSlider(0, maxEpsilon);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		slider.setValue(epsilon);
		slider.setBorder(new TitledBorder("epsilon"));

		slider.addChangeListener(new EpsilonChangedListener(segmentPane));

		setLayout(new GridBagLayout());

		c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;

		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		add(slider, c);

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

	public JSlider getSlider()
	{
		return slider;
	}
}
