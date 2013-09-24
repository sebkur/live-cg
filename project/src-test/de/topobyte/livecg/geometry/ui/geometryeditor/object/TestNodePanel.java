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
package de.topobyte.livecg.geometry.ui.geometryeditor.object;

import javax.swing.JFrame;

import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;

public class TestNodePanel
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame(PolygonalChainPanel.class.getSimpleName());

		GeometryEditPane editPane = new GeometryEditPane();
		Coordinate coordinate = new Coordinate(200, 100);
		Node node = new Node(coordinate);

		NodePanel panel = new NodePanel(editPane, node);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLocationByPlatform(true);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}
}
