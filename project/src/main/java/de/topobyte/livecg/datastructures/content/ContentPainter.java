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
package de.topobyte.livecg.datastructures.content;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingVisualizationPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;

public class ContentPainter extends TransformingVisualizationPainter
{

	private Content content;
	private ContentConfig config;

	public ContentPainter(Rectangle scene, Content content,
			ContentConfig config, Painter painter)
	{
		super(scene, painter);
		this.content = content;
		this.config = config;
	}

	@Override
	public void paint()
	{
		super.preparePaint();

		super.fillBackground(new Color(0xffffff));

		painter.setColor(new Color(0xffaaaa));
		for (Polygon polygon : content.getPolygons()) {
			Polygon tpolygon = transformer.transform(polygon);
			painter.fillPolygon(tpolygon);
		}

		painter.setColor(new Color(0x000000));
		for (Chain chain : content.getChains()) {
			drawChain(chain);
		}
		for (Polygon polygon : content.getPolygons()) {
			drawChain(polygon.getShell());
			for (Chain hole : polygon.getHoles()) {
				drawChain(hole);
			}
		}

		painter.setColor(new Color(0x000000));
		if (config.isDrawNodes()) {
			for (Chain chain : content.getChains()) {
				drawChainNodes(chain);
			}
			for (Polygon polygon : content.getPolygons()) {
				drawChainNodes(polygon.getShell());
				for (Chain hole : polygon.getHoles()) {
					drawChainNodes(hole);
				}
			}
		}
	}

	private void drawChain(Chain chain)
	{
		Chain tchain = transformer.transform(chain);
		if (tchain.getNumberOfNodes() == 1) {
			if (!config.isDrawNodes()) {
				Coordinate c = tchain.getCoordinate(0);
				painter.fillCircle(c.getX(), c.getY(), 1);
			}
		} else {
			painter.drawChain(tchain);
		}
	}

	private void drawChainNodes(Chain chain)
	{
		Chain tchain = transformer.transform(chain);
		for (int i = 0; i < tchain.getNumberOfNodes(); i++) {
			Coordinate c = tchain.getCoordinate(i);
			painter.drawRect(c.getX() - 2, c.getY() - 2, 4, 4);
		}
	}

}
