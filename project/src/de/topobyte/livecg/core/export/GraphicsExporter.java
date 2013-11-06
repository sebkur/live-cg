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
package de.topobyte.livecg.core.export;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.painting.AwtPainter;

public class GraphicsExporter
{

	public static void exportPNG(File file, AlgorithmPainter algorithmPainter,
			int width, int height) throws IOException
	{
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AwtPainter painter = new AwtPainter(graphics);

		algorithmPainter.setPainter(painter);
		algorithmPainter.setWidth(width);
		algorithmPainter.setHeight(height);
		algorithmPainter.paint();

		ImageIO.write(image, "png", file);
	}

}
