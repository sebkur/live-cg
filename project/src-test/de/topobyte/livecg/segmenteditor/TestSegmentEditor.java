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
package de.topobyte.livecg.segmenteditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.ui.segmenteditor.SegmentEditor;

public class TestSegmentEditor
{

	public static void main(String[] args)
	{
		runProgrammatically(true);
	}

	public static void runProgrammatically(boolean exitOnClose)
	{
		BasicConfigurator.configure();

		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Segment Editor");

		int size = 200;

		Chain segment = new Chain();

		segment.appendPoint(new Coordinate(20, 50));
		segment.appendPoint(new Coordinate(180, 150));

		SegmentEditor segmentEditor = new SegmentEditor(size, size, segment);

		// toolbar.setFloatable(false);

		GridBagConstraints c = new GridBagConstraints();

		JPanel mainPanel = new JPanel(new GridBagLayout());
		frame.setContentPane(mainPanel);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		// c.gridy = 0;
		// c.weighty = 0.0;
		// mainPanel.add(toolbar, c);

		c.gridy = 1;
		c.weighty = 1.0;
		mainPanel.add(segmentEditor, c);

		frame.setVisible(true);
	}

}
