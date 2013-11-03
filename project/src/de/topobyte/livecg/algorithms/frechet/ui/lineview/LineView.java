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
package de.topobyte.livecg.algorithms.frechet.ui.lineview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;

import javax.swing.JPanel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import de.topobyte.carbon.geo.draw.IdentityCoordinateTransformer;
import de.topobyte.carbon.geometry.transformation.GeometryTransformator;
import de.topobyte.livecg.algorithms.frechet.freespace.EpsilonSettable;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;

public class LineView extends JPanel implements EpsilonSettable
{

	private static final long serialVersionUID = 9050114560510657609L;

	private final Chain line1;
	private final Chain line2;

	private int epsilon;

	private boolean drawSegmentBufferP, drawSegmentBufferQ, drawPointBufferP,
			drawPointBufferQ, drawEpsilon = true;

	public LineView(int epsilon, Chain line1, Chain line2,
			boolean pSegmentBuffers, boolean qSegmentBuffers,
			boolean pPointBuffers, boolean qPointBuffers)
	{
		this.epsilon = epsilon;
		this.line1 = line1;
		this.line2 = line2;
		this.drawSegmentBufferP = pSegmentBuffers;
		this.drawSegmentBufferQ = qSegmentBuffers;
		this.drawPointBufferP = pPointBuffers;
		this.drawPointBufferQ = qPointBuffers;
	}

	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

		draw(g, line1, "P", drawSegmentBufferP, drawPointBufferP);
		draw(g, line2, "Q", drawSegmentBufferQ, drawPointBufferQ);
	}

	private static final Color colorBuffersLines = new Color(0x22ff0000, true);
	private static final Color colorBuffersPoints = new Color(0xff0000ff, true);
	private static final Color colorLines = Color.BLACK;
	private static final Color colorPoints = Color.BLUE;
	private static final Color colorLabels = Color.BLACK;
	private static final Color colorEpsilon = Color.BLACK;

	private static final int offsetEpsilon = 10;
	private static final int heightEpsilonBar = 10;

	private void draw(Graphics2D g, Chain editable, String name,
			boolean drawLineBuffer, boolean drawPointBuffer)
	{
		int n = editable.getNumberOfNodes();
		if (n == 0) {
			return;
		}

		int height = getHeight() - getInsets().top - getInsets().bottom;
		// int width = getWidth() - getInsets().left - getInsets().right;

		if (drawEpsilon) {
			g.setColor(colorEpsilon);
			int halfHeight = heightEpsilonBar / 2;
			int ypos = height - offsetEpsilon - halfHeight;
			g.drawLine(offsetEpsilon, ypos, offsetEpsilon + epsilon, ypos);
			g.drawLine(offsetEpsilon, ypos - halfHeight, offsetEpsilon, ypos
					+ halfHeight);
			g.drawLine(offsetEpsilon + epsilon, ypos - halfHeight,
					offsetEpsilon + epsilon, ypos + halfHeight);
		}

		// complete line epsilon buffer
		// useAntialiasing(g, true);
		// g.setColor(colorBuffer);
		// Geometry geometry = editable.createGeometry();
		// Geometry buffer = geometry.buffer(epsilon);
		// Area area = GeometryTransformator.toShape((Polygon) buffer,
		// new IdentityCoordinateTransformer());
		// g.fill(area);

		Coordinate last;

		if (drawPointBuffer) {
			useAntialiasing(g, true);
			g.setColor(colorBuffersPoints);
			for (int i = 0; i < n; i++) {
				Coordinate c = editable.getCoordinate(i);
				g.drawArc((int) Math.round(c.getX() - epsilon),
						(int) Math.round(c.getY() - epsilon), epsilon * 2,
						epsilon * 2, 0, 360);
			}
		}

		// line segments epsilon buffer
		if (drawLineBuffer) {
			GeometryFactory factory = new GeometryFactory();
			useAntialiasing(g, true);
			g.setColor(colorBuffersLines);
			last = editable.getCoordinate(0);
			for (int i = 1; i < n; i++) {
				Coordinate current = editable.getCoordinate(i);
				com.vividsolutions.jts.geom.Coordinate[] cs = new com.vividsolutions.jts.geom.Coordinate[] {
						new com.vividsolutions.jts.geom.Coordinate(last.getX(),
								last.getY()),
						new com.vividsolutions.jts.geom.Coordinate(
								current.getX(), current.getY()) };
				LineString seg = factory.createLineString(cs);
				Geometry buffer = seg.buffer(epsilon);
				Area area = GeometryTransformator.toShape((Polygon) buffer,
						new IdentityCoordinateTransformer());
				g.fill(area);
				last = current;
			}
		}

		// line segments
		useAntialiasing(g, true);
		g.setColor(colorLines);
		g.setStroke(new BasicStroke(1.0f));
		last = editable.getCoordinate(0);
		for (int i = 1; i < n; i++) {
			Coordinate current = editable.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			g.drawLine(x1, y1, x2, y2);
			last = current;
		}
		// points
		useAntialiasing(g, false);
		g.setColor(colorPoints);
		g.setStroke(new BasicStroke(1.0f));
		for (int i = 0; i < n; i++) {
			Coordinate current = editable.getCoordinate(i);
			int x = (int) Math.round(current.getX());
			int y = (int) Math.round(current.getY());
			g.drawRect(x - 2, y - 2, 4, 4);
		}
		// label
		useAntialiasing(g, true);
		g.setColor(colorLabels);
		Coordinate first = editable.getFirstCoordinate();
		g.drawString(name, (int) Math.round(first.getX()) - 2,
				(int) Math.round(first.getY()) - 4);
	}

	private void useAntialiasing(Graphics2D g, boolean b)
	{
		if (b) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public void setEpsilon(int epsilon)
	{
		this.epsilon = epsilon;
		repaint();
	}

	public boolean isDrawPointBufferP()
	{
		return drawPointBufferP;
	}

	public boolean isDrawPointBufferQ()
	{
		return drawPointBufferQ;
	}

	public boolean isDrawSegmentBufferP()
	{
		return drawSegmentBufferP;
	}

	public boolean isDrawSegmentBufferQ()
	{
		return drawSegmentBufferQ;
	}

	public void setDrawSegmentBufferP(boolean drawSegmentBufferP)
	{
		this.drawSegmentBufferP = drawSegmentBufferP;
	}

	public void setDrawSegmentBufferQ(boolean drawSegmentBufferQ)
	{
		this.drawSegmentBufferQ = drawSegmentBufferQ;
	}

	public void setDrawPointBufferP(boolean drawPointBufferP)
	{
		this.drawPointBufferP = drawPointBufferP;
	}

	public void setDrawPointBufferQ(boolean drawPointBufferQ)
	{
		this.drawPointBufferQ = drawPointBufferQ;
	}

}
