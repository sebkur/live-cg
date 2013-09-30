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
package de.topobyte.frechet;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.frechet.ui.polylineeditor.FrechetDialog2;
import de.topobyte.livecg.geometry.io.ContentReader;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class TestFrechet2
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		String path = "res/presets/frechet/Paper.geom";
		ContentReader contentReader = new ContentReader();
		Content content = contentReader.read(new File(path));
		
		FrechetDialog2 dialog = new FrechetDialog2(content);
		dialog.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
