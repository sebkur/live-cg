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
package de.topobyte.livecg.algorithms.convexhull.chan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.util.resources.ContentResources;

public class TestChansAlgorithm
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getLogger(ChansAlgorithm.class).setLevel(Level.DEBUG);

		String path = "res/presets/chan/Chan1.geom";
		Content content = ContentResources.load(path);

		List<Polygon> polygons = new ArrayList<>();

		for (Polygon polygon : content.getPolygons()) {
			if (PolygonHelper.isCounterClockwiseOriented(polygon)) {
				polygons.add(polygon);
			} else {
				Chain shell = polygon.getShell();
				try {
					polygon = new Polygon(ChainHelper.invert(shell), null);
					polygons.add(polygon);
				} catch (CloseabilityException e) {
					// Should not happen
				}
			}
		}

		ChansAlgorithm algorithm = new ChansAlgorithm(polygons);
		ChansAlgorithmDialog dialog = new ChansAlgorithmDialog(algorithm);
		dialog.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
