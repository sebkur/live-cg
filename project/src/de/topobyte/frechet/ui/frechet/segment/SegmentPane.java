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

package de.topobyte.frechet.ui.frechet.segment;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JPanel;

import de.topobyte.frechet.ui.frechet.EpsilonSettable;
import de.topobyte.frechet.ui.frechet.calc.Calculator;
import de.topobyte.frechet.ui.frechet.calc.Ellipse;
import de.topobyte.frechet.ui.frechet.calc.Interval;
import de.topobyte.frechet.ui.frechet.calc.LineSegment;
import de.topobyte.frechet.ui.frechet.calc.Vector;
import de.topobyte.frechet.ui.lineeditor.LineChangeListener;
import de.topobyte.util.DoubleUtil;
import de.topobyte.util.SwingUtil;

public class SegmentPane extends JPanel implements LineChangeListener,
		EpsilonSettable {

	private static final long serialVersionUID = 8167797259833415618L;

	private static boolean DEBUG = false;
	
	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private int epsilon;

	private Color colorBackground = new Color(0x333333);
	private Color colorFreeSpace = new Color(0xFFFFFF);
	private Color colorFreeSpaceOuter = new Color(0xAAAAAA);

	public SegmentPane(int epsilon) {
		this.epsilon = epsilon;
	}

	public void setEpsilon(int eps) {
		this.epsilon = eps;
		repaint();
	}

	public void setSegment1(LineSegment seg1) {
		this.seg1 = seg1;
	}

	public void setSegment2(LineSegment seg2) {
		this.seg2 = seg2;
	}

	@Override
	public void lineChanged() {
		repaint();
	}

	@Override
	public void paint(Graphics graphics) {
		if (seg1 == null || seg2 == null) {
			return;
		}

		Graphics2D g = (Graphics2D) graphics;
		 SwingUtil.useAntialiasing(g, true);
		if (DEBUG) {
			System.out.println("clip: " + g.getClip());
		}

		int width = getWidth();
		int height = getHeight();

		g.setColor(colorBackground);
		g.fillRect(0, 0, getWidth(), getHeight());

		AffineTransform transform = new AffineTransform(g.getTransform());
		// transform.translate(10, 50);
		// g.setTransform(transform);

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, height);

		g.setColor(colorFreeSpace);

		Vector a = seg1.getDirection();
		Vector b = seg1.getStart();
		Vector c = seg2.getDirection();
		Vector d = seg2.getStart();

		// System.out.println(String.format("line1: %s + s . %s", b.toString(),
		// a.toString()));
		// System.out.println(String.format("line2: %s + s . %s", d.toString(),
		// c.toString()));

		check(g, seg1, seg2, width, height);

		double tlX = b.getX() - d.getX();
		double tlY = b.getY() - d.getY();
		AffineTransform f = new AffineTransform(a.getX(), a.getY(), -c.getX(),
				-c.getY(), tlX, tlY);
		try {
			f.invert();
		} catch (NoninvertibleTransformException e) {
			System.out.println("not invertable: " + f);
			f = tryHarder(a, c, tlX, tlY, 0.001, 0);
			if (f == null) {
				return;
			}
		}

		// g.setClip(null);

		AffineTransform tx = new AffineTransform(g.getTransform());
		tx.translate(0, height); // coordinate system vertical flip
		tx.scale(1, -1); // coordinate system vertical flip
		tx.scale(width, height); // scale from [0..1] to full panel size
		tx.concatenate(f); // apply the inverse of f to g to draw the ellipse

		// fill the arc
		g.setColor(colorFreeSpaceOuter);
		g.setTransform(tx);
		g.fillArc(-epsilon, -epsilon, 2 * epsilon, 2 * epsilon, 0, 360);

		// set clip bounds
		g.setTransform(transform);
		g.clipRect(0, 0, width, height);

		// fill the arc again
		g.setColor(colorFreeSpace);
		g.setTransform(tx);
		g.fillArc(-epsilon, -epsilon, 2 * epsilon, 2 * epsilon, 0, 360);

		// draw the boundaries again
		// g.setClip(null);
		g.setTransform(transform);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width, height);

		// find the limits of the free space on the axes
		Interval intervalP = makeAxis(c, d, a, b);
		Interval intervalQ = makeAxis(a, b, c, d);
		drawHorizontalInterval(g, intervalP, width, height);
		drawVerticalInterval(g, intervalQ, width, height);
	}

	private void drawHorizontalInterval(Graphics2D g, Interval intervalP,
			int width, int height) {
		if (DoubleUtil.isValid(intervalP.getStart())) {
			int pos = (int) Math.round(intervalP.getStart() * width);
			g.fillRect(pos - 1, height - 1, 3, 3);
		}
		if (DoubleUtil.isValid(intervalP.getEnd())) {
			int pos = (int) Math.round(intervalP.getEnd() * width);
			g.fillRect(pos - 1, height - 1, 3, 3);
		}
	}

	private void drawVerticalInterval(Graphics2D g, Interval intervalQ,
			int width, int height) {
		if (DoubleUtil.isValid(intervalQ.getStart())) {
			int pos = height - (int) Math.round(intervalQ.getStart() * height);
			g.fillRect(-1, pos - 1, 3, 3);
		}
		if (DoubleUtil.isValid(intervalQ.getEnd())) {
			int pos = height - (int) Math.round(intervalQ.getEnd() * height);
			g.fillRect(-1, pos - 1, 3, 3);
		}
	}

	private Interval makeAxis(Vector a, Vector b, Vector c, Vector d) {
		Vector m = b.sub(d);
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

	private AffineTransform tryHarder(Vector a, Vector c, double tlX,
			double tlY, double initialDelta, int position) {
		// We officially cheat here. Since we cannot find an elliptic solution
		// in the degenerate case of the space between two parallel lines, we
		// just create a nearly correct, huge ellipse that looks pretty good.
		double delta = initialDelta;
		while (true) {
			AffineTransform f = null;
			switch (position) {
			case 0:
				f = new AffineTransform(a.getX() + delta, a.getY(), -c.getX(),
						-c.getY(), tlX, tlY);
				break;
			case 1:
				f = new AffineTransform(a.getX(), a.getY() + delta, -c.getX(),
						-c.getY(), tlX, tlY);
				break;
			case 2:
				f = new AffineTransform(a.getX() - delta, a.getY(), -c.getX(),
						-c.getY(), tlX, tlY);
				break;
			case 3:
				f = new AffineTransform(a.getX(), a.getY() - delta, -c.getX(),
						-c.getY(), tlX, tlY);
				break;

			}
			try {
				f.invert();
				return f;
			} catch (NoninvertibleTransformException e) {
				System.out.println("unable to invert with delta: " + delta);
			}
			delta *= 2;
			if (delta > 1) {
				if (position == 3) {
					return null;
				} else {
					return tryHarder(a, c, tlX, tlY, initialDelta, position + 1);
				}
			}
		}
	}

	// TODO: try to calculate the equation of the ellipse and plot the ellipse's
	// main axis and secondary axis
	private void check(Graphics2D g, LineSegment seg1, LineSegment seg2,
			int width, int height) {
		Ellipse ellipse = Calculator.calc(seg1, seg2, epsilon);
		g.setColor(Color.RED);
		double theta = ellipse.getTheta();

		int x0 = width / 2;
		int y0 = height / 2;

		int x1 = (int) (x0 + Math.cos(theta) * width);
		int y1 = (int) (y0 + Math.sin(theta) * height);

//		g.drawLine(x0, y0, x1, y1);
	}

}
