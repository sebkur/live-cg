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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import de.topobyte.livecg.algorithms.frechet.freespace.calc.FreeSpaceUtil;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.Interval;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.Vector;
import de.topobyte.livecg.util.DoubleUtil;
import de.topobyte.livecg.util.SwingUtil;

public class FreeSpacePainter implements EpsilonSettable
{

	private static boolean DEBUG = false;

	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private Config config;

	private int width;
	private int height;
	private int epsilon;
	private Interval LR1;
	private Interval BR1;

	private Color colorBackground = new Color(0x999999);
	private Color colorFreeSpace = new Color(0xFFFFFF);
	private Color colorReachableSpace = new Color(0xFFBBBB);
	private Color colorFreeSpaceOutline = new Color(0x333333);

	private Color colorFreeSpaceMarkers = new Color(0x000000);
	private Color colorReachableSpaceMarkers = new Color(0xAA3333);

	private float markerWidth = 2;
	private int markerLength = 3;

	public FreeSpacePainter(Config config, int epsilon)
	{
		this.config = config;
		this.epsilon = epsilon;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setEpsilon(int eps)
	{
		this.epsilon = eps;
	}

	public void setSegment1(LineSegment seg1)
	{
		this.seg1 = seg1;
	}

	public void setSegment2(LineSegment seg2)
	{
		this.seg2 = seg2;
	}

	public void setLR1(Interval LR1)
	{
		this.LR1 = LR1;
	}

	public void setBR1(Interval BR1)
	{
		this.BR1 = BR1;
	}

	public void paint(Graphics graphics)
	{
		if (seg1 == null || seg2 == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);
		if (DEBUG) {
			System.out.println("clip: " + g.getClip());
		}

		// Draw background
		g.setColor(colorBackground);
		g.fillRect(0, 0, width, height);

		// Set clip bounds
		g.clipRect(0, 0, width, height);

		AffineTransform f = createMatrix();

		AffineTransform tx = new AffineTransform();
		tx.translate(0, height); // Coordinate system vertical flip
		tx.scale(1, -1); // Coordinate system vertical flip
		tx.scale(width, height); // Scale from [0..1] to full panel size
		tx.concatenate(f); // Apply the inverse of f to g to draw the ellipse

		Arc2D arc = new Arc2D.Double(-epsilon, -epsilon, 2 * epsilon,
				2 * epsilon, 0, 360, Arc2D.CHORD);
		Shape ellipse = tx.createTransformedShape(arc);

		// Draw the ellipse -> free space
		g.setColor(colorFreeSpace);
		g.fill(ellipse);

		// Find the limits of the free space on the axes
		Interval BF1 = FreeSpaceUtil // bottom
				.freeSpace(seg2, seg1, 0, epsilon);
		Interval LF1 = FreeSpaceUtil // left
				.freeSpace(seg1, seg2, 0, epsilon);
		Interval BF2 = FreeSpaceUtil // top
				.freeSpace(seg2, seg1, 1, epsilon);
		Interval LF2 = FreeSpaceUtil // right
				.freeSpace(seg1, seg2, 1, epsilon);

		// Find the reachable space
		Interval LR2 = FreeSpaceUtil.reachableL(LR1, BR1, LF2, BF2);
		Interval BR2 = FreeSpaceUtil.reachableB(LR1, BR1, LF2, BF2);

		// Draw reachable space via clipping the ellipse
		if (config.isDrawReachableSpace()) {
			if (BR1 != null || LR1 != null) {
				Shape oldClip = g.getClip();
				Area a = new Area();
				if (BR1 != null) {
					double s = BR1.getStart();
					if (s < 0) {
						s = 0;
					}
					a.add(new Area(new Rectangle2D.Double((int) Math.round(s
							* width), 0, width, height)));
				}
				if (LR1 != null) {
					double s = LR1.getStart();
					if (s < 0) {
						s = 0;
					}
					a.add(new Area(new Rectangle2D.Double(0, 0, width, height
							- (int) Math.round(s * height))));
				}
				g.clip(a);
				g.setColor(colorReachableSpace);
				g.fill(ellipse);
				g.setClip(oldClip);
			}
		}

		// Draw the ellipse outline -> free space border
		g.setColor(colorFreeSpaceOutline);
		g.draw(ellipse);

		if (config.isDrawReachableSpaceMarkers()) {
			g.setColor(colorReachableSpaceMarkers);
			Stroke old = g.getStroke();
			g.setStroke(new BasicStroke(3));
			if (LR1 != null) {
				drawVerticalReachable(g, LR1, 0);
			}
			if (BR1 != null) {
				drawHorizontalReachable(g, BR1, height);
			}
			if (LR2 != null) {
				drawVerticalReachable(g, LR2, width);
			}
			if (BR2 != null) {
				drawHorizontalReachable(g, BR2, 0);
			}
			g.setStroke(old);
		}

		if (config.isDrawFreeSpaceMarkers()) {
			g.setColor(colorFreeSpaceMarkers);
			Stroke old = g.getStroke();
			g.setStroke(new BasicStroke(markerWidth));
			drawHorizontalInterval(g, BF1, width, height);
			drawVerticalInterval(g, LF1, height, 0);
			drawHorizontalInterval(g, BF2, width, 0);
			drawVerticalInterval(g, LF2, height, width);
			g.setStroke(old);
		}
	}

	private void drawVerticalReachable(Graphics2D g, Interval LR1, int x)
	{
		g.drawLine(x, (int) Math.round(height - LR1.getStart() * height), x,
				(int) Math.round(height - LR1.getEnd() * height));
	}

	private void drawHorizontalReachable(Graphics2D g, Interval BR1, int y)
	{
		g.drawLine((int) Math.round(BR1.getStart() * width), y,
				(int) Math.round(BR1.getEnd() * width), y);
	}

	private AffineTransform createMatrix()
	{
		Vector a = seg1.getStart();
		Vector b = seg1.getDirection();
		Vector c = seg2.getStart();
		Vector d = seg2.getDirection();

		// System.out.println(String.format("line1: %s + s . %s", a.toString(),
		// b.toString()));
		// System.out.println(String.format("line2: %s + s . %s", c.toString(),
		// d.toString()));

		Vector xa = a.add(new Vector(0, 0));
		Vector xb = b.add(new Vector(0, 0));
		Vector xc = c.add(new Vector(0, 0));
		Vector xd = d.add(new Vector(0, 0));

		while (true) {
			try {
				return createMatrix(xa, xb, xc, xd);
			} catch (NoninvertibleTransformException e) {
				// move not in arbitrary direction, but a random amount
				// in the direction perpendicular to a
				double ax = b.getX();
				double ay = b.getY();
				double norm = Math.sqrt(ax * ax + ay * ay);
				ax = ax / norm;
				ay = ay / norm;
				double iax = ay;
				double iay = -ax;

				Random r = new Random();
				double s = rd(r, 0.5, 1);

				double x = iax * s;
				double y = iay * s;

				xb = b.add(new Vector(x, y));
			}
		}
	}

	private double rd(Random r, double min, double max)
	{
		double diff = Math.abs(min - max);
		double g = r.nextDouble();
		return min + diff * g;
	}

	private AffineTransform createMatrix(Vector a, Vector b, Vector c, Vector d)
			throws NoninvertibleTransformException
	{
		double tlX = a.getX() - c.getX();
		double tlY = a.getY() - c.getY();
		AffineTransform f = new AffineTransform(b.getX(), b.getY(), -d.getX(),
				-d.getY(), tlX, tlY);
		f.invert();
		return f;
	}

	private void drawHorizontalInterval(Graphics2D g, Interval intervalP,
			int width, int y)
	{
		if (DoubleUtil.isValid(intervalP.getStart())) {
			int pos = (int) Math.round(intervalP.getStart() * width);
			g.drawLine(pos, y - markerLength, pos, y + markerLength);
		}
		if (DoubleUtil.isValid(intervalP.getEnd())) {
			int pos = (int) Math.round(intervalP.getEnd() * width);
			g.drawLine(pos, y - markerLength, pos, y + markerLength);
		}
	}

	private void drawVerticalInterval(Graphics2D g, Interval intervalQ,
			int height, int x)
	{
		if (DoubleUtil.isValid(intervalQ.getStart())) {
			int pos = height - (int) Math.round(intervalQ.getStart() * height);
			g.drawLine(x - markerLength, pos, x + markerLength, pos);
		}
		if (DoubleUtil.isValid(intervalQ.getEnd())) {
			int pos = height - (int) Math.round(intervalQ.getEnd() * height);
			g.drawLine(x - markerLength, pos, x + markerLength, pos);
		}
	}

}
