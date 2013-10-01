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
package de.topobyte.polygon.monotonepieces;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.io.ContentReader;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.object.multiple.MultiplePanel;

public class TestMonotonePieces
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		String path = "res/presets/triangulation/Small.geom";
		ContentReader contentReader = new ContentReader();
		Content content = contentReader.read(new File(path));
		List<Polygon> polygons = content.getPolygons();
		Polygon polygon = polygons.get(0);

		MonotonePiecesPanel monotonePiecesPanel = new MonotonePiecesPanel(
				polygon);

		JFrame frame = new JFrame(MultiplePanel.class.getSimpleName());
		frame.setContentPane(monotonePiecesPanel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
