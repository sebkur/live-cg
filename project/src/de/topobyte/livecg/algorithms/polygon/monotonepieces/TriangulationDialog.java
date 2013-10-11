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
package de.topobyte.livecg.algorithms.polygon.monotonepieces;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Polygon;

public class TriangulationDialog
{

	private JFrame frame;

	public TriangulationDialog(Polygon polygon)
	{
		frame = new JFrame("Triangulation with dual graph");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		TriangulationPanel tp = new TriangulationPanel(polygon);

		Settings settings = new Settings(tp);

		main.add(settings, BorderLayout.NORTH);
		main.add(tp, BorderLayout.CENTER);

		frame.setLocationByPlatform(true);
		frame.setSize(800, 500);
		frame.setVisible(true);
	}

	public JFrame getFrame()
	{
		return frame;
	}

}
