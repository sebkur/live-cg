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

package de.topobyte.frechet.ui.frechet.lines;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import de.topobyte.frechet.ui.frechet.EpsilonSettable;
import de.topobyte.frechet.ui.frechet.calc.LineSegment;
import de.topobyte.frechet.ui.frechet.segment.FreeSpacePainter;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;

public class FrechetDiagram extends JPanel implements EpsilonSettable
{
	private static final long serialVersionUID = 5024820193840910054L;

	private int epsilon;
	private final Chain line1;
	private final Chain line2;

	public FrechetDiagram(int epsilon, Chain line1, Chain line2)
	{
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

	private LineSegment getSegment(Chain line, int n)
	{
		Coordinate c1 = line.getCoordinate(n);
		Coordinate c2 = line.getCoordinate(n + 1);
		return new LineSegment(c1, c2);
	}

	public void update()
	{
		// called when chains have changed
	}

	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;

		int width = getWidth();
		int height = getHeight();

		int nSegmentsP = line1.getNumberOfNodes() - 1;
		int nSegmentsQ = line2.getNumberOfNodes() - 1;

		int w = width / nSegmentsP;
		int h = height / nSegmentsQ;

		FreeSpacePainter painter = new FreeSpacePainter(epsilon, false, true);

		AffineTransform transform = g.getTransform();
		Shape clip = g.getClip();
		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				LineSegment segP = getSegment(line1, x);
				LineSegment segQ = getSegment(line2, nSegmentsQ - y - 1);

				int lx = x * w;
				int ly = y * h;
				AffineTransform t = new AffineTransform(transform);
				t.translate(lx, ly);
				g.setTransform(t);
				g.setClip(0, 0, w, h);

				painter.setSegment1(segP);
				painter.setSegment2(segQ);
				painter.setSize(w, h);
				painter.paint(g);
			}
		}
		g.setTransform(transform);
		g.setClip(clip);
		
		// Draw grid
		g.setColor(Color.BLACK);
		for (int x = 0; x <= nSegmentsP; x++) {
			int lx = x * w;
			g.drawLine(lx, 0, lx, height);
		}
		for (int y = 0; y <= nSegmentsQ; y++) {
			int ly = y * h;
			g.drawLine(0, ly, width, ly);
		}
	}
}
