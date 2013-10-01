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

package de.topobyte.frechet.ui.distanceterrain.segment;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import de.topobyte.frechet.ui.freespace.calc.LineSegment;
import de.topobyte.frechet.ui.freespace.calc.Vector;
import de.topobyte.util.SwingUtil;

public class DistanceTerrainPainter
{
	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private int width;
	private int height;

	private Color color1 = new Color(0xCC3333);
	private Color color2 = new Color(0xFFFF99);

	private boolean drawBorder;

	public DistanceTerrainPainter(boolean drawBorder)
	{
		this.drawBorder = drawBorder;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setSegment1(LineSegment seg1)
	{
		this.seg1 = seg1;
	}

	public void setSegment2(LineSegment seg2)
	{
		this.seg2 = seg2;
	}

	public void paint(Graphics graphics)
	{
		if (seg1 == null || seg2 == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics;

		SwingUtil.useAntialiasing(g, false);
		for (int y = 0; y < height; y++) {
			double t = y / (double) height;
			Vector Qt = get(seg2, t);
			for (int x = 0; x < width; x++) {
				double s = x / (double) width;
				Vector Ps = get(seg1, s);

				double dx = Qt.getX() - Ps.getX();
				double dy = Qt.getY() - Ps.getY();

				double d = Math.sqrt(dx * dx + dy * dy);
				g.setColor(getColor(d));
				g.drawRect(x, height - y, 1, 1);
			}
		}

		SwingUtil.useAntialiasing(g, true);
		if (drawBorder) {
			// Draw the boundaries again
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, width, height);
		}
	}

	private Vector get(LineSegment seg, double s)
	{
		return seg.getStart().add(seg.getDirection().mult(s));
	}

	public Color getColor(double distance)
	{
		// The smaller distance, the more influence has color1
		// The bigger distance, the more influence has color2
		int max = 100;
		double f = 0;
		if (distance <= 0) {
			f = 0;
		} else if (distance >= max) {
			f = 1;
		} else {
			f = distance / (double) max;
		}

		int r1 = color1.getRed();
		int g1 = color1.getGreen();
		int b1 = color1.getBlue();

		int r2 = color2.getRed();
		int g2 = color2.getGreen();
		int b2 = color2.getBlue();

		int r = (int) Math.round(r1 + (r2 - r1) * f);
		int g = (int) Math.round(g1 + (g2 - g1) * f);
		int b = (int) Math.round(b1 + (b2 - b1) * f);

		return new Color(r, g, b);
	}
}
