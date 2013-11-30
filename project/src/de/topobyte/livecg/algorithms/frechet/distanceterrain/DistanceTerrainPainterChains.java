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

	private DistanceTerrainConfig config;

	private final Chain chain1;
	private final Chain chain2;

	public DistanceTerrainPainterChains(DistanceTerrainConfig config, Chain chain1,
			Chain chain2, Painter painter)
	{
		super(painter);
		this.config = config;
		this.chain1 = chain1;
		this.chain2 = chain2;
	}

	private LineSegment getSegment(Chain chain, int n)
	{
		Coordinate c1 = chain.getCoordinate(n);
		Coordinate c2 = chain.getCoordinate(n + 1);
		return new LineSegment(c1, c2);
	}

	@Override
	public void paint()
	{
		int width = getWidth();
		int height = getHeight();

		int nSegmentsP = chain1.getNumberOfNodes() - 1;
		int nSegmentsQ = chain2.getNumberOfNodes() - 1;

		int w = width / nSegmentsP;
		int h = height / nSegmentsQ;
		int usedWidth = w * nSegmentsP;
		int usedHeight = h * nSegmentsQ;

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				LineSegment segP = getSegment(chain1, x);
				LineSegment segQ = getSegment(chain2, nSegmentsQ - y - 1);

				int lx = x * w;
				int ly = y * h;

				DistanceTerrainImagePainter imagePainter = new DistanceTerrainImagePainter(
						image, lx, ly, w, h, segP, segQ);
				imagePainter.paint();
			}
		}

		painter.drawImage(image, 0, 0);

		if (config.isDrawGrid()) {
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
}
