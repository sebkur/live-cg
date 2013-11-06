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
package de.topobyte.livecg.core.geometry.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.geometryeditor.geometryeditor.Content;

public class ContentWriter extends SetOfGeometryWriter
{

	public void write(Content content, File file) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		write(content, bos);
		bos.close();
	}

	public void write(Content content, OutputStream output) throws IOException
	{
		this.output = output;
		chains = content.getChains();
		polygons = content.getPolygons();
		Rectangle scene = content.getScene();

		output.write(("<scene width=\"" + scene.getWidth() + "\" height=\""
				+ scene.getHeight() + "\">").getBytes());
		writeNodes();
		writeChains();
		writePolygons();
		output.write("\n</scene>\n".getBytes());
	}
}
