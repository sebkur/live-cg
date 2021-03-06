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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.topobyte.awt.util.GraphicsUtil;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.core.painting.backend.awt.AwtPainter;

public class GraphicsExporter
{

	public static void exportPNG(File file,
			VisualizationPainter visualizationPainter, int width, int height)
			throws IOException
	{
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D graphics = image.createGraphics();
		GraphicsUtil.useAntialiasing(graphics, true);

		AwtPainter painter = new AwtPainter(graphics);

		visualizationPainter.setPainter(painter);
		visualizationPainter.setWidth(width);
		visualizationPainter.setHeight(height);
		visualizationPainter.paint();

		ImageIO.write(image, "png", file);
	}

}
