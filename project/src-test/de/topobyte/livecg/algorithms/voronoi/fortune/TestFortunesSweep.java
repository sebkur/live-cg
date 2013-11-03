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
package de.topobyte.livecg.algorithms.voronoi.fortune;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;

public class TestFortunesSweep
{

	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		Logger.getLogger(Algorithm.class.getPackage().getName()).setLevel(
				Level.DEBUG);
		// Logger.getLogger(Algorithm.class).setLevel(Level.DEBUG);
		// Logger.getLogger(HistoryEventQueue.class).setLevel(Level.DEBUG);

		FortuneDialog dialog = new FortuneDialog();
		Algorithm algorithm = dialog.getAlgorithm();

		// String path = "res/presets/voronoi/Points1.geom";
		// ContentReader contentReader = new ContentReader();
		// Content content = contentReader.read(new File(path));
		// for (Chain chain : content.getChains()) {
		// for (int i = 0; i < chain.getNumberOfNodes(); i++) {
		// Node node = chain.getNode(i);
		// Coordinate c = node.getCoordinate();
		// algorithm.addSite(new Point(c.getX(), c.getY()), false);
		// }
		// }

		algorithm.addSite(new Point(150, 180), false);
		algorithm.addSite(new Point(300, 100), false);
		algorithm.addSite(new Point(300, 200), false);
		algorithm.addSite(new Point(300, 300), false);
		algorithm.addSite(new Point(300, 400), false);
		algorithm.addSite(new Point(450, 250), false);

		algorithm.restart();
	}
}
