/* This file is part of LiveCG.
 *
 * Copyright (C) 2016 Sebastian Kuerten
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

package de.topobyte.livecg.util.resources;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class ContentResources
{

	public static Content load(String path) throws IOException,
			ParserConfigurationException, SAXException
	{
		ResourceFile resourceFile = ResourceLoader.open(path);
		ContentReader contentReader = new ContentReader();
		InputStream input = resourceFile.open();
		Content content = contentReader.read(input);
		input.close();
		return content;
	}

}
