package de.topobyte.livecg.core.painting;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.AwtHelper;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.ShapeUtil;

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
		g.setColor(new java.awt.Color(color.getARGB(), true));
	}

	@Override
	public void setStrokeWidth(double width)
	{
		g.setStroke(new BasicStroke((float) width));
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
		GeneralPath p = new GeneralPath();
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		g.draw(p);
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
		Arc2D arc = ShapeUtil.createArc(x, y, radius);
		g.draw(arc);
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		Arc2D arc = ShapeUtil.createArc(x, y, radius);
		g.fill(arc);
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

	@Override
	public Object getClip()
	{
		return g.getClip();
	}

	@Override
	public void setClip(Object clip)
	{
		g.setClip((Shape) clip);
	}

	@Override
	public void clipRect(double x, double y, double width, double height)
	{
		clipArea(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void clipArea(Shape shape)
	{
		g.clip(shape);
	}

	@Override
	public AffineTransform getTransform()
	{
		return g.getTransform();
	}

	@Override
	public void setTransform(AffineTransform t)
	{
		g.setTransform(t);
	}

}
