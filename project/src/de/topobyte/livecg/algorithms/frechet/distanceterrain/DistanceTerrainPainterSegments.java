/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

import java.awt.image.BufferedImage;

import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;

public class DistanceTerrainPainterSegments extends BasicAlgorithmPainter
{
	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private boolean drawBorder;

	public DistanceTerrainPainterSegments(boolean drawBorder, Painter painter)
	{
		super(painter);
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

	public void paint()
	{
		if (seg1 == null || seg2 == null) {
			return;
		}

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		DistanceTerrainImagePainter imagePainter = new DistanceTerrainImagePainter(
				image, 0, 0, width, height, seg1, seg2);
		imagePainter.paint();

		painter.drawImage(image, 0, 0);

		if (drawBorder) {
			painter.setColor(new Color(0x000000));
			painter.drawRect(0, 0, width, height);
		}
	}

}