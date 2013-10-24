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

package de.topobyte.livecg.algorithms.frechet.freespace;

import java.awt.geom.AffineTransform;

import de.topobyte.livecg.algorithms.frechet.freespace.calc.FrechetUtil;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.ReachableSpace;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;

public class FreeSpacePainterChains extends BasicAlgorithmPainter implements
		EpsilonSettable
{

	private FreeSpacePainterSegments segmentPainter;

	private Config config;

	private Color colorCellBoundaries = new Color(0x000000);

	private int epsilon;
	private final Chain line1;
	private final Chain line2;

	public FreeSpacePainterChains(Config config, int epsilon, Chain line1,
			Chain line2, Painter painter)
	{
		super(painter);
		this.config = config;
		this.epsilon = epsilon;
		this.line1 = line1;
		this.line2 = line2;
		segmentPainter = new FreeSpacePainterSegments(config, epsilon, painter);
	}

	@Override
	public void setEpsilon(int epsilon)
	{
		this.epsilon = epsilon;
	}

	public void paint()
	{
		segmentPainter.setEpsilon(epsilon);
		segmentPainter.setSize(getWidth(), getHeight());
		segmentPainter.setPainter(painter);

		int width = getWidth();
		int height = getHeight();

		int nSegmentsP = line1.getNumberOfNodes() - 1;
		int nSegmentsQ = line2.getNumberOfNodes() - 1;

		int w = width / nSegmentsP;
		int h = height / nSegmentsQ;
		int usedWidth = w * nSegmentsP;
		int usedHeight = h * nSegmentsQ;

		ReachableSpace reachableSpace = new ReachableSpace(line1, line2,
				epsilon);

		AffineTransform transform = painter.getTransform();
		Object clip = painter.getClip();
		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				int ry = nSegmentsQ - y - 1;
				LineSegment segP = FrechetUtil.getSegment(line1, x);
				LineSegment segQ = FrechetUtil.getSegment(line2, ry);

				int lx = x * w;
				int ly = y * h;
				painter.clipRect(lx, ly, w, h);

				AffineTransform t = new AffineTransform(transform);
				t.translate(lx, ly);
				painter.setTransform(t);

				segmentPainter.setSegment1(segP);
				segmentPainter.setSegment2(segQ);
				segmentPainter.setSize(w, h);
				segmentPainter.setLR1(reachableSpace.getLR(x, ry));
				segmentPainter.setBR1(reachableSpace.getBR(x, ry));
				segmentPainter.paint();

				painter.setTransform(transform);
				painter.setClip(clip);
			}
		}

		// Draw grid
		if (config.isDrawGrid()) {
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
