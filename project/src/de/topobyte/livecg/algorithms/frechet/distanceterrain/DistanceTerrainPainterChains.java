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
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;

public class DistanceTerrainPainterChains extends BasicAlgorithmPainter
{

	private Color colorCellBoundaries = new Color(0x000000);

	private final Chain line1;
	private final Chain line2;

	public DistanceTerrainPainterChains(Chain line1, Chain line2,
			Painter painter)
	{
		super(painter);
		this.line1 = line1;
		this.line2 = line2;
	}

	private LineSegment getSegment(Chain line, int n)
	{
		Coordinate c1 = line.getCoordinate(n);
		Coordinate c2 = line.getCoordinate(n + 1);
		return new LineSegment(c1, c2);
	}

	@Override
	public void paint()
	{
		int width = getWidth();
		int height = getHeight();

		int nSegmentsP = line1.getNumberOfNodes() - 1;
		int nSegmentsQ = line2.getNumberOfNodes() - 1;

		int w = width / nSegmentsP;
		int h = height / nSegmentsQ;
		int usedWidth = w * nSegmentsP;
		int usedHeight = h * nSegmentsQ;

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				LineSegment segP = getSegment(line1, x);
				LineSegment segQ = getSegment(line2, nSegmentsQ - y - 1);

				int lx = x * w;
				int ly = y * h;

				DistanceTerrainImagePainter imagePainter = new DistanceTerrainImagePainter(
						image, lx, ly, w, h, segP, segQ);
				imagePainter.paint();
			}
		}
		
		painter.drawImage(image, 0, 0);

		// Draw grid
		painter.setColor(colorCellBoundaries);
		for (int x = 0; x <= nSegmentsP; x++) {
			int lx = x * w;
			painter.drawLine(lx, 0, lx, usedHeight);
		}
		for (int y = 0; y <= nSegmentsQ; y++) {
			int ly = y * h;
			painter.drawLine(0, ly, usedWidth, ly);
		}
	}
}
