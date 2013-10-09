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
package de.topobyte.polygon.shortestpath;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.ChainHelper;
import de.topobyte.livecg.geometry.geom.CloseabilityException;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.geom.PolygonHelper;
import de.topobyte.livecg.geometry.io.ContentReader;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class TestSleeve
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		String path = "res/presets/polygons/Small.geom";
		int a = 0, b = 4;

		path = "res/presets/polygons/Big.geom";
		a = 10;
		b = 25;

		path = "res/presets/polygons/Real-Big.geom";
		a = 37;
		b = 77;

		ContentReader contentReader = new ContentReader();
		Content content = contentReader.read(new File(path));
		List<Polygon> polygons = content.getPolygons();
		Polygon polygon = polygons.get(0);
		if (!PolygonHelper.isCounterClockwiseOriented(polygon)) {
			Chain shell = polygon.getShell();
			try {
				polygon = new Polygon(ChainHelper.invert(shell), null);
			} catch (CloseabilityException e) {
				// Should not happen
			}
		}
		Chain shell = polygon.getShell();
		Node nodeStart = shell.getNode(a);
		Node nodeTarget = shell.getNode(b);

		SleevePanel sleevePanel = new SleevePanel(polygon, nodeStart,
				nodeTarget);

		JFrame frame = new JFrame(SleevePanel.class.getSimpleName());
		frame.setContentPane(sleevePanel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
