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

import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingVisualizationPainter;
import de.topobyte.livecg.ui.geometryeditor.SetOfGeometries;

public class BufferPainter extends TransformingVisualizationPainter
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

		List<Polygon> buffer = algorithm.getResult();
		List<Polygon> tBuffer = new ArrayList<Polygon>();
		for (Polygon p : buffer) {
			tBuffer.add(transformer.transform(p));
		}

		if (config.getDistance() > 0) {
			painter.setColor(colorBufferFill);
			for (Polygon p : tBuffer) {
				painter.fillPolygon(p);
			}
		}

		if (config.isDrawOriginal()) {
			SetOfGeometries input = algorithm.getOriginal();
			for (Polygon polygon : input.getPolygons()) {
				Polygon tPolygon = transformer.transform(polygon);
				painter.setColor(colorOriginalFill);
				painter.fillPolygon(tPolygon);
				painter.setColor(colorOriginalOutline);
				painter.drawPolygon(tPolygon);
			}
			for (Chain chain : input.getChains()) {
				Chain tChain = transformer.transform(chain);
				painter.setColor(colorOriginalOutline);
				if (chain.getNumberOfNodes() == 1) {
					Coordinate c = tChain.getCoordinate(0);
					painter.fillCircle(c.getX(), c.getY(), 1);
				} else {
					painter.drawChain(tChain);
				}
			}
		}

		if (config.getDistance() != 0) {
			painter.setColor(colorBufferOutline);
			for (Polygon p : tBuffer) {
				painter.drawPolygon(p);
			}
		}

		if (config.getDistance() < 0) {
			painter.setColor(colorBufferFill);
			for (Polygon p : tBuffer) {
				painter.fillPolygon(p);
			}
		}
	}
}
