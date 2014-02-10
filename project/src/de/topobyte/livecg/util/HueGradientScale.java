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
package de.topobyte.livecg.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.topobyte.livecg.core.export.IpeExporter;
import de.topobyte.livecg.core.painting.BasicVisualizationPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Image;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.util.colorgradient.Gradient;
import de.topobyte.livecg.util.colorgradient.HueGradient;

public class HueGradientScale
{
	public static void main(String[] args) throws TransformerException,
			IOException, ParserConfigurationException
	{
		File file = new File(args[0]);
		TestPainter painter = new TestPainter(null);
		IpeExporter.exportIpe(file, painter, 40, 200);
	}

	private static class TestPainter extends BasicVisualizationPainter
	{

		public TestPainter(Painter painter)
		{
			super(painter);
		}

		@Override
		public void paint()
		{
			Image image = new Image(getWidth(), getHeight());
			Gradient gradient = new HueGradient();
			for (int j = 0; j < image.getHeight(); j++) {
				double value = 1 - j / (double) image.getHeight();
				Color c = gradient.getColor(value);
				for (int i = 0; i < image.getWidth(); i++) {
					image.setRGB(i, j, c.getRGB());
				}
			}
			painter.drawImage(image, 0, 0);
		}

	}
}
