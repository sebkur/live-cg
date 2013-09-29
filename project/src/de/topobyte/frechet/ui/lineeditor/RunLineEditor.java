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

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;

import de.topobyte.frechet.ui.misc.Menu;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;

public class RunLineEditor {

	public static void main(String[] args) {
		runProgrammatically(true);
	}

	public static void runProgrammatically(boolean exitOnClose) {
		BasicConfigurator.configure();

		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Line Editor");

		int size = 200;

		Menu menu = new Menu();
		frame.setJMenuBar(menu);

		Chain line = new Chain();

		line.appendPoint(new Coordinate(20, 50));
		line.appendPoint(new Coordinate(180, 150));

		LineEditor lineEditor = new LineEditor(size, size, line);

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
		mainPanel.add(lineEditor, c);

		frame.setVisible(true);
	}

}
