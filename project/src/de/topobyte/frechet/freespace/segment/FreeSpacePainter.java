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

package de.topobyte.frechet.freespace.segment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Random;

import de.topobyte.frechet.freespace.EpsilonSettable;
import de.topobyte.frechet.freespace.calc.Interval;
import de.topobyte.frechet.freespace.calc.LineSegment;
import de.topobyte.frechet.freespace.calc.Vector;
import de.topobyte.util.DoubleUtil;
import de.topobyte.util.SwingUtil;

public class FreeSpacePainter implements EpsilonSettable
{

	private static boolean DEBUG = false;

	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private int width;
	private int height;
	private int epsilon;

	private Color colorBackground = new Color(0x999999);
	private Color colorFreeSpace = new Color(0xFFFFFF);
	private Color colorFreeSpaceOutline = new Color(0x333333);

	private boolean drawBorder;
	private boolean drawAxisIntersection;

	private float markerWidth = 2;
	private int markerLength = 3;

	public FreeSpacePainter(int epsilon, boolean drawBorder,
			boolean drawAxisIntersection)
	{
		this.epsilon = epsilon;
		this.drawBorder = drawBorder;
		this.drawAxisIntersection = drawAxisIntersection;
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

		// Draw the ellipse
		g.setColor(colorFreeSpace);
		g.fill(ellipse);
		g.setColor(colorFreeSpaceOutline);
		g.draw(ellipse);

		if (drawBorder) {
			// Draw the boundaries again
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, width, height);
		}

		Vector a = seg1.getDirection();
		Vector b = seg1.getStart();
		Vector c = seg2.getDirection();
		Vector d = seg2.getStart();

		if (drawAxisIntersection) {
			// Find the limits of the free space on the axes
			g.setColor(Color.BLACK);
			Stroke old = g.getStroke();
			g.setStroke(new BasicStroke(markerWidth));
			Interval intervalP1 = makeAxis(c, d, a, b, 0); // bottom
			Interval intervalQ1 = makeAxis(a, b, c, d, 0); // left
			Interval intervalP2 = makeAxis(c, d, a, b, 1); // top
			Interval intervalQ2 = makeAxis(a, b, c, d, 1); // right
			drawHorizontalInterval(g, intervalP1, width, height, height);
			drawVerticalInterval(g, intervalQ1, width, height, 0);
			drawHorizontalInterval(g, intervalP2, width, height, 0);
			drawVerticalInterval(g, intervalQ2, width, height, width);
			g.setStroke(old);
		}
	}

	private AffineTransform createMatrix()
	{
		Vector a = seg1.getDirection();
		Vector b = seg1.getStart();
		Vector c = seg2.getDirection();
		Vector d = seg2.getStart();

		// System.out.println(String.format("line1: %s + s . %s", b.toString(),
		// a.toString()));
		// System.out.println(String.format("line2: %s + s . %s", d.toString(),
		// c.toString()));

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
				double ax = a.getX();
				double ay = a.getY();
				double norm = Math.sqrt(ax * ax + ay * ay);
				ax = ax / norm;
				ay = ay / norm;
				double iax = ay;
				double iay = -ax;

				Random r = new Random();
				double s = rd(r, 0.5, 1);

				double x = iax * s;
				double y = iay * s;

				xa = a.add(new Vector(x, y));
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
		double tlX = b.getX() - d.getX();
		double tlY = b.getY() - d.getY();
		AffineTransform f = new AffineTransform(a.getX(), a.getY(), -c.getX(),
				-c.getY(), tlX, tlY);
		f.invert();
		return f;
	}

	private void drawHorizontalInterval(Graphics2D g, Interval intervalP,
			int width, int height, int y)
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
			int width, int height, int x)
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

	private Interval makeAxis(Vector a, Vector b, Vector c, Vector d, double f)
	{
		Vector m = b.sub(d).add(a.mult(f));
		double mxcx = m.getX() * c.getX();
		double mycy = m.getY() * c.getY();
		double cx2 = c.getX() * c.getX();
		double cy2 = c.getY() * c.getY();
		double mx2 = m.getX() * m.getX();
		double my2 = m.getY() * m.getY();
		double eps2 = epsilon * epsilon;
		double ha = (mxcx + mycy) / (cx2 + cy2);
		double hb = (ha * ha) - ((mx2 + my2 - eps2) / (cx2 + cy2));
		double rhb = Math.sqrt(hb);
		double i1 = ha + rhb;
		double i2 = ha - rhb;
		if (DEBUG) {
			System.out.println(String.format("%f -> %f", i1, i2));
		}
		return new Interval(i1, i2);
	}

}
