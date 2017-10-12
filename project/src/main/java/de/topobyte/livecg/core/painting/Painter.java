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
package de.topobyte.livecg.core.painting;

import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.viewports.geometry.Coordinate;
import noawt.java.awt.Shape;
import noawt.java.awt.geom.AffineTransform;

public interface Painter
{
	public void setColor(Color color);

	public void setStrokeWidth(double width);

	public void setStrokeNormal();

	public void setStrokeDash(float[] dash, float phase);

	public void drawRect(int x, int y, int width, int height);

	public void drawRect(double x, double y, double width, double height);

	public void fillRect(int x, int y, int width, int height);

	public void fillRect(double x, double y, double width, double height);

	public void drawLine(int x1, int y1, int x2, int y2);

	public void drawLine(double x1, double y1, double x2, double y2);

	public void drawPath(List<Coordinate> points, boolean close);

	public void drawCircle(double x, double y, double radius);

	public void fillCircle(double x, double y, double radius);

	public void drawChain(Chain chain);

	public void drawPolygon(Polygon polygon);

	public void fillPolygon(Polygon polygon);

	public void draw(Shape shape);

	public void fill(Shape shape);

	public void drawString(String text, double x, double y);

	public Object getClip();

	public void setClip(Object clip);

	public void clipRect(double x, double y, double width, double height);

	public void clipArea(Shape shape);

	public AffineTransform getTransform();

	public void setTransform(AffineTransform t);

	public void drawImage(Image image, int x, int y);
}
