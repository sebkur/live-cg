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

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.painting.backend.tikz.TikzPainter;

public class TikzExporter
{

	public static void exportTikz(File file, AlgorithmPainter algorithmPainter,
			int width, int height) throws IOException
	{
		File parent = file.getParentFile();
		String name = file.getName();
		String imageDir = name + "-images";
		File images = new File(parent, imageDir);

		double imageWidth = 13.0;

		StringBuilder header = new StringBuilder();
		StringBuilder buffer = new StringBuilder();
		// The TikzPainter will paint in bounds (0,0) to (1,-1), by applying
		// this scale, we scale everything to 13cm width
		header.append("\\begin{tikzpicture}[scale=" + imageWidth + "]\n");

		// By this factor everything will be scaled so that drawing happens in
		// the unit square bounds
		int div = Math.max(width, height);
		double scale = 1 / (double) div;

		// Clip the image to the actual bound so that it will not be too large
		// (because the TikzPainter will clip with the unit square which will
		// lead to an image of exactly the unit square's size)
		double clipX = width / (double) div;
		double clipY = -height / (double) div;
		String clip = String.format("\\clip (0,0) rectangle (%f,%f);\n", clipX,
				clipY);
		header.append(clip);

		TikzPainter painter = new TikzPainter(header, buffer, scale, width,
				imageWidth, images, imageDir);

		algorithmPainter.setPainter(painter);
		algorithmPainter.setWidth(width);
		algorithmPainter.setHeight(height);
		algorithmPainter.paint();

		buffer.append("\\end{tikzpicture}");

		FileOutputStream fos = new FileOutputStream(file);
		fos.write(header.toString().getBytes());
		fos.write(buffer.toString().getBytes());
		fos.close();
	}
}
