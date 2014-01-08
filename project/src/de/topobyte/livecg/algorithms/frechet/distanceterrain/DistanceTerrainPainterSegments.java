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
package de.topobyte.livecg.algorithms.frechet.distanceterrain;

import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Image;
import de.topobyte.livecg.core.painting.Painter;

public class DistanceTerrainPainterSegments extends BasicAlgorithmPainter
{
	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private DistanceTerrainConfig config;
	private boolean drawBorder;

	public DistanceTerrainPainterSegments(DistanceTerrainConfig config,
			boolean drawBorder, Painter painter)
	{
		super(painter);
		this.config = config;
		this.drawBorder = drawBorder;
	}

	public void setSegment1(LineSegment seg1)
	{
		this.seg1 = seg1;
	}

	public void setSegment2(LineSegment seg2)
	{
		this.seg2 = seg2;
	}

	@Override
	public void paint()
	{
		if (seg1 == null || seg2 == null) {
			return;
		}

		Image image = new Image(width, height);

		DistanceTerrainImagePainter imagePainter = new DistanceTerrainImagePainter(
				image, 0, 0, width, height, seg1, seg2, config.getScale());
		imagePainter.paint();

		painter.drawImage(image, 0, 0);

		if (drawBorder) {
			painter.setColor(new Color(0x000000));
			painter.drawRect(0, 0, width, height);
		}
	}

}
