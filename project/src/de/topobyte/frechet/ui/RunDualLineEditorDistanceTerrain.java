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

package de.topobyte.frechet.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;

public class RunDualLineEditorDistanceTerrain
{

	final static int STEP_SIZE = 1;
	final static int STEP_SIZE_BIG = 10;

	public static void runProgrammatically(boolean exitOnClose)
	{
		BasicConfigurator.configure();

		final JFrame frame = new JFrame();
		frame.setSize(1200, 440);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Line Editor. red - start, blue - end");

		int size = 200;

		Chain line1 = new Chain();
		Chain line2 = new Chain();

		// line1.addPoint(new Coordinate(20, 50));
		// line1.addPoint(new Coordinate(170, 150));
		//
		// line2.addPoint(new Coordinate(30, 150));
		// line2.addPoint(new Coordinate(140, 50));

		// int epsilon = 50;

		// line1.addPoint(new Coordinate(0, 100));
		// line1.addPoint(new Coordinate(100, 0));
		//
		// line2.addPoint(new Coordinate(0, 50));
		// line2.addPoint(new Coordinate(100, 50));

		line1.appendPoint(new Coordinate(0, 200));
		line1.appendPoint(new Coordinate(200, 0));

		line2.appendPoint(new Coordinate(0, 100));
		line2.appendPoint(new Coordinate(200, 100));

		// line2.addPoint(new Coordinate(0, 200));
		// line2.addPoint(new Coordinate(200, 0));

		final DualLineEditorDistanceTerrain lineEditor = new DualLineEditorDistanceTerrain(
				size, size, line1, line2);

		GridBagConstraints c = new GridBagConstraints();

		JPanel mainPanel = new JPanel(new GridBagLayout());
		frame.setContentPane(mainPanel);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		c.gridy = 1;
		c.weighty = 1.0;
		mainPanel.add(lineEditor, c);

		frame.setVisible(true);
	}
}
