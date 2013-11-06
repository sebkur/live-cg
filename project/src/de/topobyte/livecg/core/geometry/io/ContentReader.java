/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.core.geometry.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.topobyte.livecg.geometryeditor.geometryeditor.Content;

public class ContentReader extends SetOfGeometryReader
{

	private Content content;

	@Override
	public Content read(File file) throws IOException,
			ParserConfigurationException, SAXException
	{
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		Content content = read(bis);
		bis.close();
		return content;
	}

	@Override
	public Content read(InputStream input) throws IOException,
			ParserConfigurationException, SAXException
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(input, this);

		return content;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		if (position.isEmpty()) {
			if (qName.equals("scene")) {
				position.push(Element.Data);
				String sw = attributes.getValue("width");
				String sh = attributes.getValue("height");
				double w = Double.valueOf(sw);
				double h = Double.valueOf(sh);

				content = new Content(w, h);
				super.content = content;
			}
		} else {
			super.startElement(uri, localName, qName, attributes);
		}
	}
}
