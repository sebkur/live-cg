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
package de.topobyte.livecg.core.painting;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.topobyte.livecg.core.export.SvgExporter;
import de.topobyte.livecg.core.export.TikzExporter;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.datastructures.content.ContentConfig;
import de.topobyte.livecg.datastructures.content.ContentPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class Test
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, TransformerException
	{
		String path = "res/presets/Startup.geom";
		ContentReader contentReader = new ContentReader();
		Content content = contentReader.read(new File(path));

		Rectangle scene = content.getScene();
		ContentConfig config = new ContentConfig();
		ContentPainter contentPainter = new ContentPainter(scene, content,
				config, null);

		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		File svg = new File("/tmp/test.svg");
		File tikz = new File("/tmp/test.tikz");

		SvgExporter.exportSVG(svg, contentPainter, width, height);
		TikzExporter.exportTikz(tikz, contentPainter, width, height);
	}
}
