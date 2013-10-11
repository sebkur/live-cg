package de.topobyte.livecg.core.painting;

import java.awt.Shape;
import java.util.List;

import de.topobyte.livecg.algorithms.voronoi.fortune.gui.core.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public interface Painter
{
	public void setColor(Color color);

	public void fillRect(int x, int y, int width, int height);

	public void fillRect(double x, double y, double width, double height);

	public void drawLine(int x1, int y1, int x2, int y2);

	public void drawLine(double x1, double y1, double x2, double y2);

	public void drawPath(List<Coordinate> points);

	public void drawCircle(double x, double y, double radius);

	public void fillCircle(double x, double y, double radius);

	public void drawPolygon(Polygon polygon);

	public void fillPolygon(Polygon polygon);

	public void draw(Shape shape);

	public void fill(Shape shape);

	public void drawString(String text, double x, double y);

}
