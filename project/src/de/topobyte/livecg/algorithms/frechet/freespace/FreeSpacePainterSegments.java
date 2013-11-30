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
package de.topobyte.livecg.algorithms.frechet.freespace;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import de.topobyte.livecg.algorithms.frechet.freespace.calc.FreeSpaceUtil;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.Interval;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.util.DoubleUtil;

public class FreeSpacePainterSegments extends BasicAlgorithmPainter implements
		EpsilonSettable
{

	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private FreeSpaceConfig config;

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

	public FreeSpacePainterSegments(FreeSpaceConfig config, int epsilon, Painter painter)
	{
		super(painter);
		this.config = config;
		this.epsilon = epsilon;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	@Override
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

	@Override
	public void paint()
	{
		if (seg1 == null || seg2 == null) {
			return;
		}

		// Draw background
		painter.setColor(colorBackground);
		painter.fillRect(0, 0, width, height);

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
		painter.setColor(colorFreeSpace);
		painter.fill(ellipse);

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
				Object oldClip = painter.getClip();
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
				painter.clipArea(a);
				painter.setColor(colorReachableSpace);
				painter.fill(ellipse);
				painter.setClip(oldClip);
			}
		}

		// Draw the ellipse outline -> free space border
		painter.setColor(colorFreeSpaceOutline);
		painter.draw(ellipse);

		if (config.isDrawReachableSpaceMarkers()) {
			painter.setColor(colorReachableSpaceMarkers);
			painter.setStrokeWidth(3);
			if (LR1 != null) {
				drawVerticalReachable(LR1, 0);
			}
			if (BR1 != null) {
				drawHorizontalReachable(BR1, height);
			}
			if (LR2 != null) {
				drawVerticalReachable(LR2, width);
			}
			if (BR2 != null) {
				drawHorizontalReachable(BR2, 0);
			}
			painter.setStrokeWidth(1);
		}

		if (config.isDrawFreeSpaceMarkers()) {
			painter.setColor(colorFreeSpaceMarkers);
			painter.setStrokeWidth(markerWidth);
			drawHorizontalInterval(BF1, width, height);
			drawVerticalInterval(LF1, height, 0);
			drawHorizontalInterval(BF2, width, 0);
			drawVerticalInterval(LF2, height, width);
			painter.setStrokeWidth(1);
		}
	}

	private void drawVerticalReachable(Interval LR1, int x)
	{
		painter.drawLine(x, (int) Math.round(height - LR1.getStart() * height),
				x, (int) Math.round(height - LR1.getEnd() * height));
	}

	private void drawHorizontalReachable(Interval BR1, int y)
	{
		painter.drawLine((int) Math.round(BR1.getStart() * width), y,
				(int) Math.round(BR1.getEnd() * width), y);
	}

	private AffineTransform createMatrix()
	{
		Vector2 a = seg1.getStart();
		Vector2 b = seg1.getDirection();
		Vector2 c = seg2.getStart();
		Vector2 d = seg2.getDirection();

		// System.out.println(String.format("line1: %s + s . %s", a.toString(),
		// b.toString()));
		// System.out.println(String.format("line2: %s + s . %s", c.toString(),
		// d.toString()));

		Vector2 xa = a.add(new Vector2(0, 0));
		Vector2 xb = b.add(new Vector2(0, 0));
		Vector2 xc = c.add(new Vector2(0, 0));
		Vector2 xd = d.add(new Vector2(0, 0));

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

				xb = b.add(new Vector2(x, y));
			}
		}
	}

	private double rd(Random r, double min, double max)
	{
		double diff = Math.abs(min - max);
		double g = r.nextDouble();
		return min + diff * g;
	}

	private AffineTransform createMatrix(Vector2 a, Vector2 b, Vector2 c,
			Vector2 d) throws NoninvertibleTransformException
	{
		double tlX = a.getX() - c.getX();
		double tlY = a.getY() - c.getY();
		AffineTransform f = new AffineTransform(b.getX(), b.getY(), -d.getX(),
				-d.getY(), tlX, tlY);
		f.invert();
		return f;
	}

	private void drawHorizontalInterval(Interval intervalP, int width, int y)
	{
		if (DoubleUtil.isValid(intervalP.getStart())) {
			int pos = (int) Math.round(intervalP.getStart() * width);
			painter.drawLine(pos, y - markerLength, pos, y + markerLength);
		}
		if (DoubleUtil.isValid(intervalP.getEnd())) {
			int pos = (int) Math.round(intervalP.getEnd() * width);
			painter.drawLine(pos, y - markerLength, pos, y + markerLength);
		}
	}

	private void drawVerticalInterval(Interval intervalQ, int height, int x)
	{
		if (DoubleUtil.isValid(intervalQ.getStart())) {
			int pos = height - (int) Math.round(intervalQ.getStart() * height);
			painter.drawLine(x - markerLength, pos, x + markerLength, pos);
		}
		if (DoubleUtil.isValid(intervalQ.getEnd())) {
			int pos = height - (int) Math.round(intervalQ.getEnd() * height);
			painter.drawLine(x - markerLength, pos, x + markerLength, pos);
		}
	}

}
