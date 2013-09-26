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

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.CloseabilityException;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.object.multiple.MultiplePanel;

public class TestMultipleObjectsPanel
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame(MultiplePanel.class.getSimpleName());

		GeometryEditPane editPane = new GeometryEditPane();
		
		Chain chain = new Chain();
		chain.appendPoint(new Coordinate(100, 100));
		chain.appendPoint(new Coordinate(200, 100));
		chain.appendPoint(new Coordinate(200, 200));
		
		Chain shell = new Chain();
		shell.appendPoint(new Coordinate(10, 10));
		shell.appendPoint(new Coordinate(20, 100));
		shell.appendPoint(new Coordinate(300, 300));
		shell.appendPoint(new Coordinate(300, 40));
		try {
			shell.setClosed(true);
		} catch (CloseabilityException e) {
			// ignore
		}
		Polygon polygon = new Polygon(shell);
		editPane.getContent().addPolygon(polygon);
		
		editPane.addCurrentChain(chain);
		editPane.addCurrentPolygon(polygon);

		MultiplePanel panel = new MultiplePanel(editPane);
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLocationByPlatform(true);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}
}
