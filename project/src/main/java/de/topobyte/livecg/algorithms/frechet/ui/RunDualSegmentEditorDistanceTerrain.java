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

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;

public class RunDualSegmentEditorDistanceTerrain
{

	final static int STEP_SIZE = 1;
	final static int STEP_SIZE_BIG = 10;

	public static void runProgrammatically(boolean exitOnClose)
	{
		final JFrame frame = new JFrame();
		frame.setSize(1200, 440);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Segment Editor. red - start, blue - end");

		int size = 200;

		Chain segment1 = new Chain();
		Chain segment2 = new Chain();

		// segment1.addPoint(new Coordinate(20, 50));
		// segment1.addPoint(new Coordinate(170, 150));
		//
		// segment2.addPoint(new Coordinate(30, 150));
		// segment2.addPoint(new Coordinate(140, 50));

		// int epsilon = 50;

		// segment1.addPoint(new Coordinate(0, 100));
		// segment1.addPoint(new Coordinate(100, 0));
		//
		// segment2.addPoint(new Coordinate(0, 50));
		// segment2.addPoint(new Coordinate(100, 50));

		segment1.appendPoint(new Coordinate(0, 200));
		segment1.appendPoint(new Coordinate(200, 0));

		segment2.appendPoint(new Coordinate(0, 100));
		segment2.appendPoint(new Coordinate(200, 100));

		// segment2.addPoint(new Coordinate(0, 200));
		// segment2.addPoint(new Coordinate(200, 0));

		final DualSegmentEditorDistanceTerrain segmentEditor = new DualSegmentEditorDistanceTerrain(
				size, size, segment1, segment2);

		GridBagConstraints c = new GridBagConstraints();

		JPanel mainPanel = new JPanel(new GridBagLayout());
		frame.setContentPane(mainPanel);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		c.gridy = 1;
		c.weighty = 1.0;
		mainPanel.add(segmentEditor, c);

		frame.setVisible(true);
	}
}
