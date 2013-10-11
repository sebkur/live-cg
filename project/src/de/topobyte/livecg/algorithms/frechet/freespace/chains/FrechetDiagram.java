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

package de.topobyte.livecg.algorithms.frechet.freespace.chains;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.frechet.freespace.Config;
import de.topobyte.livecg.algorithms.frechet.freespace.EpsilonSettable;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpacePainter;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.FrechetUtil;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.ReachableSpace;
import de.topobyte.livecg.core.geometry.geom.Chain;

public class FrechetDiagram extends JPanel implements EpsilonSettable
{
	private static final long serialVersionUID = 5024820193840910054L;

	private Config config;

	private Color colorCellBoundaries = new Color(0x000000);

	private int epsilon;
	private final Chain line1;
	private final Chain line2;

	public FrechetDiagram(Config config, int epsilon, Chain line1, Chain line2)
	{
		this.config = config;
		this.epsilon = epsilon;
		this.line1 = line1;
		this.line2 = line2;
	}

	@Override
	public void setEpsilon(int epsilon)
	{
		this.epsilon = epsilon;
		repaint();
	}

	public void update()
	{
		// called when chains have changed
	}

	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

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

		FreeSpacePainter painter = new FreeSpacePainter(config, epsilon);

		AffineTransform transform = g.getTransform();
		Shape clip = g.getClip();
		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				int ry = nSegmentsQ - y - 1;
				LineSegment segP = FrechetUtil.getSegment(line1, x);
				LineSegment segQ = FrechetUtil.getSegment(line2, ry);

				int lx = x * w;
				int ly = y * h;
				AffineTransform t = new AffineTransform(transform);
				t.translate(lx, ly);
				g.setTransform(t);
				g.setClip(0, 0, w, h);

				painter.setSegment1(segP);
				painter.setSegment2(segQ);
				painter.setSize(w, h);
				painter.setLR1(reachableSpace.getLR(x, ry));
				painter.setBR1(reachableSpace.getBR(x, ry));
				painter.paint(g);
			}
		}
		g.setTransform(transform);
		g.setClip(clip);

		// Draw grid
		if (config.isDrawGrid()) {
			g.setColor(colorCellBoundaries);
			for (int x = 0; x <= nSegmentsP; x++) {
				int lx = x * w;
				g.drawLine(lx, 0, lx, usedHeight);
			}
			for (int y = 0; y <= nSegmentsQ; y++) {
				int ly = y * h;
				g.drawLine(0, ly, usedWidth, ly);
			}
		}
	}
}
