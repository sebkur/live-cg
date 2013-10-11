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

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.topobyte.livecg.algorithms.polygon.monotonepieces.DiagonalUtil;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.Graph;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.TriangulationDialog;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.core.ui.geometryeditor.Content;

public class TestPolygonTriangulation
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		Logger.getLogger(Graph.class).setLevel(Level.DEBUG);
		Logger.getLogger(DiagonalUtil.class).setLevel(Level.DEBUG);

		String path = "res/presets/polygons/Small.geom";
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

		TriangulationDialog dialog = new TriangulationDialog(polygon);

		dialog.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}