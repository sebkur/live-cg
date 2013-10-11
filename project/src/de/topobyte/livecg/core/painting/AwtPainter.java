package de.topobyte.livecg.core.painting;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.List;

import de.topobyte.livecg.algorithms.voronoi.fortune.gui.core.Coordinate;
import de.topobyte.livecg.core.geometry.geom.AwtHelper;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class AwtPainter implements Painter
{

	private Graphics2D g;

	public AwtPainter(Graphics2D g)
	{
		this.g = g;
	}

	public void setGraphics(Graphics2D g)
	{
		this.g = g;
	}

	@Override
	public void setColor(Color color)
	{
		g.setColor(new java.awt.Color(color.getRGB()));
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		g.fillRect(x, y, width, height);
	}

	@Override
	public void fillRect(double x, double y, double width, double height)
	{
		int ix = (int) Math.round(x);
		int iy = (int) Math.round(x);
		int w = (int) Math.round(x + width - ix);
		int h = (int) Math.round(y + height - iy);
		g.fillRect(ix, iy, w, h);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		g.drawLine((int) Math.round(x1), (int) Math.round(y1),
				(int) Math.round(x2), (int) Math.round(y2));
	}

	@Override
	public void drawPath(List<Coordinate> points)
	{
		if (points.size() < 2) {
			return;
		}
		for (int i = 0; i < points.size() - 1; i++) {
			Coordinate c1 = points.get(i);
			Coordinate c2 = points.get(i + 1);
			drawLine(c1.getX(), c1.getY(), c2.getX(), c2.getY());
		}
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		double diam = radius * 2;
		int d = (int) Math.round(diam);
		int px = (int) Math.round(x - radius);
		int py = (int) Math.round(y - radius);
		g.drawOval(px, py, d, d);
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		double diam = radius * 2;
		int d = (int) Math.round(diam);
		int px = (int) Math.round(x - radius);
		int py = (int) Math.round(y - radius);
		g.fillOval(px, py, d, d);
	}

	@Override
	public void drawPolygon(Polygon polygon)
	{
		Area area = AwtHelper.toShape(polygon);
		g.draw(area);
	}

	@Override
	public void fillPolygon(Polygon polygon)
	{
		Area area = AwtHelper.toShape(polygon);
		g.fill(area);
	}

	@Override
	public void draw(Shape shape)
	{
		g.draw(shape);
	}

	@Override
	public void fill(Shape shape)
	{
		g.fill(shape);
	}

	@Override
	public void drawString(String text, double x, double y)
	{
		g.drawString(text, (float) x, (float) y);
	}

}
