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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.lina.AffineTransformUtil;
import de.topobyte.livecg.core.lina.Matrix;

public class TikzPainter implements Painter
{

	private StringBuffer buffer;
	private double div;

	private Matrix matrix;
	private GeometryTransformer transformer;

	private String newline = "\n";

	private Color color;
	private double width = 1.0;
	private float[] dash = null;
	private float phase = 0;

	public TikzPainter(StringBuffer buffer, double div)
	{
		this.buffer = buffer;
		this.div = div;

		matrix = AffineTransformUtil.scale(1 / div, -1 / div);
		transformer = new GeometryTransformer(matrix);
	}

	@Override
	public void setColor(Color color)
	{
		this.color = color;
	}

	@Override
	public void setStrokeWidth(double width)
	{
		this.width = width;
	}

	@Override
	public void setStrokeNormal()
	{
		dash = null;
		phase = 0;
	}

	@Override
	public void setStrokeDash(float[] dash, float phase)
	{
		this.dash = dash;
		this.phase = phase;
	}

	private void appendColorDefine()
	{
		int rgb = color.getRGB();
		double r = ((rgb & 0xff0000) >> 16) / 255.0;
		double g = ((rgb & 0xff00) >> 8) / 255.0;
		double b = (rgb & 0xff) / 255.0;
		buffer.append(String.format("\\definecolor{c}{rgb}{%.5f,%.5f,%.5f}", r,
				g, b));
		buffer.append(newline);
	}

	private String line()
	{
		return String.format("line width=%.5fmm", width / 5.0);
	}

	@Override
	public void drawRect(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawRect(double x, double y, double width, double height)
	{
		Coordinate c1 = transformer.transform(new Coordinate(x, y));
		Coordinate c2 = transformer.transform(new Coordinate(x + width, y
				+ height));
		buffer.append(String.format("\\draw[" + line()
				+ "] (%.5f, %.5f) rectangle (%.5f, %.5f);", c1.getX(),
				c1.getY(), c2.getX(), c2.getY()));
		buffer.append(newline);
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void fillRect(double x, double y, double width, double height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPath(List<Coordinate> points, boolean close)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		// TODO Auto-generated method stub

	}

	private void appendChain(Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			buffer.append(String.format("(%.5f,%.5f)", c.getX(), c.getY()));
			if (i < chain.getNumberOfNodes() - 1) {
				buffer.append(" -- ");
			}
		}
		if (chain.isClosed()) {
			buffer.append(" -- cycle");
		}
	}

	@Override
	public void drawChain(Chain chain)
	{
		buffer.append("\\draw[" + line() + "] ");
		appendChain(transformer.transform(chain));
		buffer.append(";");
		buffer.append(newline);
	}

	@Override
	public void drawPolygon(Polygon polygon)
	{
		Polygon tpolygon = transformer.transform(polygon);
		buffer.append("\\draw[" + line() + "] ");
		Chain shell = tpolygon.getShell();
		appendChain(shell);
		for (Chain hole : tpolygon.getHoles()) {
			buffer.append(" ");
			appendChain(hole);
		}
		buffer.append(";");
		buffer.append(newline);
	}

	@Override
	public void fillPolygon(Polygon polygon)
	{
		appendColorDefine();
		Polygon tpolygon = transformer.transform(polygon);
		buffer.append("\\fill[color=c, even odd rule] ");
		Chain shell = tpolygon.getShell();
		appendChain(shell);
		for (Chain hole : tpolygon.getHoles()) {
			buffer.append(" ");
			appendChain(hole);
		}
		buffer.append(";");
		buffer.append(newline);
	}

	@Override
	public void draw(Shape shape)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void fill(Shape shape)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawString(String text, double x, double y)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object getClip()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClip(Object clip)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clipRect(double x, double y, double width, double height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clipArea(Shape shape)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public AffineTransform getTransform()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTransform(AffineTransform t)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void drawImage(BufferedImage image, int x, int y)
	{
		// TODO Auto-generated method stub

	}

}
