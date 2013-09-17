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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.BasicConfigurator;

import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.misc.Menu;

public class RunGeometryEditor {

	public static void main(String[] args) {
		runProgrammatically(true);
	}

	public static void runProgrammatically(boolean exitOnClose) {
		BasicConfigurator.configure();

		JFrame frame = new JFrame();
		frame.setSize(500, 400);
		if (exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		frame.setTitle("Line Editor");

		Menu menu = new Menu();
		GeometryEditor lineEditor = new GeometryEditor();
		Toolbar toolbar = new Toolbar(lineEditor.getEditPane().getContent(),
				lineEditor.getEditPane());

		toolbar.setFloatable(false);

		GridBagConstraints c = new GridBagConstraints();

		JPanel mainPanel = new JPanel(new GridBagLayout());
		frame.setJMenuBar(menu);
		frame.setContentPane(mainPanel);

		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;

		c.gridy = 0;
		c.weighty = 0.0;
		mainPanel.add(toolbar, c);

		c.gridy = 1;
		c.weighty = 1.0;
		mainPanel.add(lineEditor, c);

		frame.setVisible(true);

		Editable line1 = new Editable();
		line1.addPoint(new Coordinate(100, 100));
		line1.addPoint(new Coordinate(200, 120));
		line1.addPoint(new Coordinate(300, 150));
		Editable line2 = new Editable();
		line2.addPoint(new Coordinate(100, 140));
		line2.addPoint(new Coordinate(200, 150));
		line2.addPoint(new Coordinate(300, 100));
		lineEditor.getEditPane().getContent().addLine(line1);
		lineEditor.getEditPane().getContent().addLine(line2);

	}
}
