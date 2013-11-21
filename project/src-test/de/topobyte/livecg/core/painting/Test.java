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
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelConverter;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.datastructures.content.ContentConfig;
import de.topobyte.livecg.datastructures.content.ContentPainter;
import de.topobyte.livecg.datastructures.dcel.DcelConfig;
import de.topobyte.livecg.datastructures.dcel.DcelPainter;
import de.topobyte.livecg.datastructures.dcel.InstanceDcelPainter;
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

		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		File svg1 = new File("/tmp/test1.svg");
		File tikz1 = new File("/tmp/test1.tikz");

		ContentConfig contentConfig = new ContentConfig();
		ContentPainter contentPainter = new ContentPainter(scene, content,
				contentConfig, null);

		SvgExporter.exportSVG(svg1, contentPainter, width, height);
		TikzExporter.exportTikz(tikz1, contentPainter, width, height);

		File svg2 = new File("/tmp/test2.svg");
		File tikz2 = new File("/tmp/test2.tikz");

		DcelConfig dcelConfig = new DcelConfig();
		DCEL dcel = DcelConverter.convert(content);
		DcelPainter dcelPainter = new InstanceDcelPainter(scene, dcel,
				dcelConfig, null);

		SvgExporter.exportSVG(svg2, dcelPainter, width, height);
		TikzExporter.exportTikz(tikz2, dcelPainter, width, height);
	}
}
