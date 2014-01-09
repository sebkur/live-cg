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
package de.topobyte.livecg.algorithms.jts.buffer;

import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingAlgorithmPainter;

public class BufferPainter extends TransformingAlgorithmPainter
{

	private BufferAlgorithm algorithm;
	private BufferConfig config;

	public BufferPainter(BufferAlgorithm algorithm, BufferConfig config,
			Painter painter)
	{
		super(algorithm.getScene(), painter);
		this.algorithm = algorithm;
		this.config = config;
	}

	@Override
	public void paint()
	{
		Color colorBG = new Color(0xffffff);
		Color colorOriginalFill = new Color(0xffffaaaa, true);
		Color colorOriginalOutline = new Color(0x000000);
		Color colorBufferFill = new Color(0x660000ff, true);
		Color colorBufferOutline = new Color(0x000000);

		preparePaint();
		fillBackground(colorBG);

		Polygon buffer = algorithm.getResult();
		Polygon tBuffer = null;
		if (buffer != null) {
			tBuffer = transformer.transform(buffer);
		}

		if (buffer != null && config.getDistance() > 0) {
			painter.setColor(colorBufferFill);
			painter.fillPolygon(tBuffer);
		}

		Polygon tOriginal = transformer.transform(algorithm.getOriginal());
		if (config.isDrawOriginal()) {
			painter.setColor(colorOriginalFill);
			painter.fillPolygon(tOriginal);
			painter.setColor(colorOriginalOutline);
			painter.drawPolygon(tOriginal);
		}

		if (buffer != null && config.getDistance() != 0) {
			painter.setColor(colorBufferOutline);
			painter.drawPolygon(tBuffer);
		}

		if (buffer != null && config.getDistance() < 0) {
			painter.setColor(colorBufferFill);
			painter.fillPolygon(tBuffer);
		}
	}
}
