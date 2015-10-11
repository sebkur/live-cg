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
package de.topobyte.livecg.core.painting.backend.awt;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import noawt.java.awt.Shape;
import noawt.java.awt.geom.AffineTransform;
import noawt.java.awt.geom.Rectangle2D;
import de.topobyte.livecg.core.geometry.geom.AwtHelper;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Image;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.backend.ImageUtil;
import de.topobyte.livecg.util.NoAwtUtil;
import de.topobyte.livecg.util.ShapeUtilAwt;

public class AwtPainter implements Painter
{

	private Graphics2D g;

	public AwtPainter(Graphics2D g)
	{
		setGraphics(g);
	}

	public void setGraphics(Graphics2D g)
	{
		this.g = g;
		if (g == null) {
			return;
		}
		updateStroke();
	}

	@Override
	public void setColor(Color color)
	{
		g.setColor(new java.awt.Color(color.getARGB(), true));
	}

	@Override
	public void drawRect(int x, int y, int width, int height)
	{
		g.drawRect(x, y, width, height);
	}

	@Override
	public void drawRect(double x, double y, double width, double height)
	{
		java.awt.geom.Rectangle2D.Double rect = new java.awt.geom.Rectangle2D.Double(
				x, y, width, height);
		g.draw(rect);
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		g.fillRect(x, y, width, height);
	}

	@Override
	public void fillRect(double x, double y, double width, double height)
	{
		java.awt.geom.Rectangle2D.Double rect = new java.awt.geom.Rectangle2D.Double(
				x, y, width, height);
		g.fill(rect);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		g.draw(p);
	}

	@Override
	public void drawPath(List<Coordinate> points, boolean close)
	{
		if (points.size() < 2) {
			return;
		}

		java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
		p.moveTo(points.get(0).getX(), points.get(0).getY());
		for (int i = 1; i < points.size(); i++) {
			Coordinate c = points.get(i);
			p.lineTo(c.getX(), c.getY());
		}
		if (close) {
			p.closePath();
		}
		g.draw(p);
	}

	@Override
	public void drawChain(Chain chain)
	{
		if (chain.getNumberOfNodes() < 2) {
			return;
		}

		java.awt.geom.GeneralPath p = new java.awt.geom.GeneralPath();
		Coordinate c0 = chain.getCoordinate(0);
		p.moveTo(c0.getX(), c0.getY());
		for (int i = 1; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			p.lineTo(c.getX(), c.getY());
		}
		if (chain.isClosed()) {
			p.closePath();
		}
		g.draw(p);
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		java.awt.geom.Arc2D arc = ShapeUtilAwt.createArc(x, y, radius);
		g.draw(arc);
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		java.awt.geom.Arc2D arc = ShapeUtilAwt.createArc(x, y, radius);
		g.fill(arc);
	}

	@Override
	public void drawPolygon(Polygon polygon)
	{
		java.awt.geom.Area area = AwtHelper.toShape(polygon);
		g.draw(area);
	}

	@Override
	public void fillPolygon(Polygon polygon)
	{
		java.awt.geom.Area area = AwtHelper.toShape(polygon);
		g.fill(area);
	}

	@Override
	public void draw(Shape shape)
	{
		g.draw(NoAwtUtil.convert(shape));
	}

	@Override
	public void fill(Shape shape)
	{
		g.fill(NoAwtUtil.convert(shape));
	}

	@Override
	public void drawString(String text, double x, double y)
	{
		Font font = new Font("Sans", Font.PLAIN, 12);
		g.setFont(font);
		g.drawString(text, (float) x, (float) y);
	}

	@Override
	public Object getClip()
	{
		return g.getClip();
	}

	@Override
	public void setClip(Object clip)
	{
		g.setClip((java.awt.Shape) clip);
	}

	@Override
	public void clipRect(double x, double y, double width, double height)
	{
		clipArea(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void clipArea(Shape shape)
	{
		g.clip(NoAwtUtil.convert(shape));
	}

	@Override
	public AffineTransform getTransform()
	{
		return NoAwtUtil.convert(g.getTransform());
	}

	@Override
	public void setTransform(AffineTransform t)
	{
		g.setTransform(NoAwtUtil.convert(t));
	}

	@Override
	public void drawImage(Image image, int x, int y)
	{
		BufferedImage im = ImageUtil.convert(image);
		g.drawImage(im, x, y, null);
	}

	/*
	 * Stroke
	 */

	private double width = 1.0;
	private float[] dash = null;
	private float phase = 0;

	private void updateStroke()
	{
		if (dash == null) {
			g.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
		} else {
			g.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND, 0, dash, phase));
		}
	}

	@Override
	public void setStrokeWidth(double width)
	{
		this.width = width;
		updateStroke();
	}

	@Override
	public void setStrokeNormal()
	{
		dash = null;
		phase = 0;
		updateStroke();
	}

	@Override
	public void setStrokeDash(float[] dash, float phase)
	{
		this.dash = dash;
		this.phase = phase;
		updateStroke();
	}

}
