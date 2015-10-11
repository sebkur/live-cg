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
package de.topobyte.livecg.core.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import noawt.java.awt.geom.Arc2D;
import noawt.java.awt.geom.Area;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.topobyte.livecg.core.geometry.geom.NoAwtHelper;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.backend.svg.SvgPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.util.ShapeUtilNoAwt;

public class TestSvgDrawStrokes
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, TransformerException
	{
		File file = new File("/tmp/strokes.svg");
		String path = "res/presets/polygons/Hole.geom";

		ContentReader contentReader = new ContentReader();
		Content content = contentReader.read(new File(path));
		List<Polygon> polygons = content.getPolygons();
		Polygon polygon = polygons.get(0);
		Area area = NoAwtHelper.toShape(polygon);

		int width = 600, height = 600;

		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document doc = impl.createDocument(svgNS, "svg", null);

		Element svgRoot = doc.getDocumentElement();

		svgRoot.setAttributeNS(null, "width", Integer.toString(width));
		svgRoot.setAttributeNS(null, "height", Integer.toString(height));

		/*
		 * Drawing
		 */

		SvgPainter painter = new SvgPainter(doc, svgRoot);

		painter.setColor(new Color(0x99000099, true));

		painter.fill(area);

		Arc2D arc = ShapeUtilNoAwt.createArc(200, 200, 100);
		painter.fill(arc);

		painter.setColor(new Color(0xaa990000, true));
		painter.setStrokeWidth(2);

		painter.draw(arc);

		painter.setStrokeDash(new float[] { 10, 10 }, 0f);
		painter.draw(area);

		for (int i = 0; i <= 8; i++) {
			painter.setColor(new Color(0x990000));
			painter.setStrokeWidth(4);
			painter.setStrokeDash(new float[] { 8, 12 }, i);
			painter.drawRect(400, 30 + i * 60, 50, 50);
		}

		painter.setStrokeWidth(2.0);
		painter.setStrokeDash(new float[] { 10, 10, 5, 5 }, 0f);
		painter.drawCircle(150, 450, 100);

		painter.drawLine(460, 100, 500, 200);

		painter.setStrokeNormal();
		painter.drawCircle(380, 450, 100);

		painter.drawLine(480, 100, 520, 200);

		/*
		 * Output
		 */

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream fos = new FileOutputStream(file);
		StreamResult result = new StreamResult(fos);

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(source, result);

		fos.close();
	}
}
