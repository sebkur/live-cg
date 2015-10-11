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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestSvgStrokes
{
	public static void main(String[] args) throws TransformerException,
			IOException
	{
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document doc = impl.createDocument(svgNS, "svg", null);

		Element svgRoot = doc.getDocumentElement();

		svgRoot.setAttributeNS(null, "width", "400");
		svgRoot.setAttributeNS(null, "height", "450");

		Element rectangle = doc.createElementNS(svgNS, "rect");
		rectangle.setAttributeNS(null, "x", "10");
		rectangle.setAttributeNS(null, "y", "20");
		rectangle.setAttributeNS(null, "width", "100");
		rectangle.setAttributeNS(null, "height", "50");
		rectangle.setAttributeNS(null, "fill", "none");
		rectangle.setAttributeNS(null, "stroke", "red");
		rectangle.setAttributeNS(null, "stroke-width", "4px");
		rectangle.setAttributeNS(null, "stroke-linecap", "round");
		rectangle.setAttributeNS(null, "stroke-dasharray", "8,12");
		rectangle.setAttributeNS(null, "stroke-dashoffset", "4");

		svgRoot.appendChild(rectangle);

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		File file = new File("/tmp/foo.svg");
		FileOutputStream fos = new FileOutputStream(file);
		StreamResult result = new StreamResult(fos);

		transformer.transform(source, result);

		fos.close();
	}

}
