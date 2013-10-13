package de.topobyte.livecg.core.painting;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.List;
import java.util.Locale;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class SvgPainter implements Painter
{
	private String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

	private Document doc;
	private Element root;

	private Color color;
	private double width = 1;

	public SvgPainter(Document doc, Element root)
	{
		this.doc = doc;
		this.root = root;
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
	public void fillRect(int x, int y, int width, int height)
	{
		Element rectangle = doc.createElementNS(svgNS, "rect");
		rectangle.setAttributeNS(null, "x", Integer.toString(x));
		rectangle.setAttributeNS(null, "y", Integer.toString(y));
		rectangle.setAttributeNS(null, "width", Integer.toString(width));
		rectangle.setAttributeNS(null, "height", Integer.toString(height));
		rectangle.setAttributeNS(null, "fill", getCurrentColor());

		root.appendChild(rectangle);
	}

	@Override
	public void fillRect(double x, double y, double width, double height)
	{
		Element rectangle = doc.createElementNS(svgNS, "rect");
		rectangle.setAttributeNS(null, "x", Double.toString(x));
		rectangle.setAttributeNS(null, "y", Double.toString(y));
		rectangle.setAttributeNS(null, "width", Double.toString(width));
		rectangle.setAttributeNS(null, "height", Double.toString(height));
		rectangle.setAttributeNS(null, "fill", getCurrentColor());

		root.appendChild(rectangle);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		drawLine((double) x1, (double) y1, (double) x2, (double) y2);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		Element path = doc.createElementNS(svgNS, "path");
		path.setAttributeNS(
				null,
				"style",
				"fill:none;stroke:"
						+ getCurrentColor()
						+ ";stroke-width:"
						+ width
						+ "px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
		path.setAttributeNS(null, "d",
				String.format(Locale.US, "M %f,%f %f,%f", x1, y1, x2, y2));

		root.appendChild(path);
	}

	@Override
	public void drawPath(List<Coordinate> points)
	{
		if (points.size() < 2) {
			return;
		}

		StringBuilder strb = new StringBuilder();
		Coordinate start = points.get(0);
		pathMoveTo(strb, start);

		for (int i = 1; i < points.size(); i++) {
			Coordinate c = points.get(i);
			pathLineTo(strb, c);
		}

		stroke(strb);
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", Double.toString(x));
		circle.setAttributeNS(null, "cy", Double.toString(y));
		circle.setAttributeNS(null, "r", Double.toString(radius));
		circle.setAttributeNS(null, "fill", "none");
		circle.setAttributeNS(null, "stroke", getCurrentColor());
		circle.setAttributeNS(null, "stroke-width", width + "px");

		root.appendChild(circle);
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", Double.toString(x));
		circle.setAttributeNS(null, "cy", Double.toString(y));
		circle.setAttributeNS(null, "r", Double.toString(radius));
		circle.setAttributeNS(null, "fill", getCurrentColor());

		root.appendChild(circle);
	}

	private String getCurrentColor()
	{
		return String.format("#%06x", color.getRGB());
	}

	private static void pathMoveTo(StringBuilder strb, double x, double y)
	{
		strb.append(String.format(Locale.US, "M %f,%f", x, y));
	}

	private static void pathMoveTo(StringBuilder strb, Coordinate c)
	{
		pathMoveTo(strb, c.getX(), c.getY());
	}

	private static void pathLineTo(StringBuilder strb, double x, double y)
	{
		strb.append(String.format(Locale.US, " %f,%f", x, y));
	}

	private static void pathLineTo(StringBuilder strb, Coordinate c)
	{
		pathLineTo(strb, c.getX(), c.getY());
	}

	private static void pathClose(StringBuilder strb)
	{
		strb.append(" Z");
	}

	private void pathQuadraticTo(StringBuilder strb, double x1, double y1,
			double x, double y)
	{
		strb.append(String.format(Locale.US, "Q %f %f %f %f", x1, y1, x, y));
	}

	private void pathCubicTo(StringBuilder strb, double x1, double y1,
			double x2, double y2, double x, double y)
	{
		strb.append(String.format(Locale.US, "C %f %f %f %f %f %f", x1, y1, x2,
				y2, x, y));
	}

	private void stroke(StringBuilder strb)
	{
		Element path = doc.createElementNS(svgNS, "path");
		path.setAttributeNS(
				null,
				"style",
				"fill:none;stroke:"
						+ getCurrentColor()
						+ ";stroke-width:"
						+ width
						+ "px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
		path.setAttributeNS(null, "d", strb.toString());

		root.appendChild(path);
	}

	private void fill(StringBuilder strb)
	{
		Element path = doc.createElementNS(svgNS, "path");
		path.setAttributeNS(null, "style",
				"fill:" + getCurrentColor()
						+ ";fill-rule:evenodd;stroke:none;fill-opacity:"
						+ color.getAlpha());
		path.setAttributeNS(null, "d", strb.toString());

		root.appendChild(path);
	}

	@Override
	public void drawPolygon(Polygon polygon)
	{
		Chain shell = polygon.getShell();
		drawChain(shell);
		for (Chain hole : polygon.getHoles()) {
			drawChain(hole);
		}
	}

	private void drawChain(Chain chain)
	{
		if (chain.getNumberOfNodes() < 2) {
			return;
		}

		StringBuilder strb = new StringBuilder();
		appendChain(strb, chain);

		stroke(strb);
	}

	private void appendChain(StringBuilder strb, Chain chain)
	{
		Coordinate start = chain.getCoordinate(0);
		pathMoveTo(strb, start);

		for (int i = 1; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			pathLineTo(strb, c);
		}

		if (chain.isClosed()) {
			pathClose(strb);
		}
	}

	@Override
	public void fillPolygon(Polygon polygon)
	{
		StringBuilder strb = new StringBuilder();

		Chain shell = polygon.getShell();
		appendChain(strb, shell);
		for (Chain hole : polygon.getHoles()) {
			strb.append(" ");
			appendChain(strb, hole);
		}

		fill(strb);
	}

	@Override
	public void draw(Shape shape)
	{
		StringBuilder strb = buildPath(shape);
		stroke(strb);
	}

	@Override
	public void fill(Shape shape)
	{
		StringBuilder strb = buildPath(shape);
		fill(strb);
	}

	private StringBuilder buildPath(Shape shape)
	{
		StringBuilder strb = new StringBuilder();

		PathIterator pathIterator = shape
				.getPathIterator(new AffineTransform());
		while (!pathIterator.isDone()) {
			double[] coords = new double[6];
			int type = pathIterator.currentSegment(coords);
			pathIterator.next();

			switch (type) {
			case PathIterator.SEG_MOVETO:
				double cx = coords[0];
				double cy = coords[1];
				pathMoveTo(strb, cx, cy);
				break;
			case PathIterator.SEG_LINETO:
				cx = coords[0];
				cy = coords[1];
				pathLineTo(strb, cx, cy);
				break;
			case PathIterator.SEG_CLOSE:
				pathClose(strb);
				break;
			case PathIterator.SEG_QUADTO:
				cx = coords[2];
				cy = coords[3];
				double c1x = coords[0];
				double c1y = coords[1];
				pathQuadraticTo(strb, c1x, c1y, cx, cy);
				break;
			case PathIterator.SEG_CUBICTO:
				cx = coords[4];
				cy = coords[5];
				c1x = coords[0];
				c1y = coords[1];
				double c2x = coords[2];
				double c2y = coords[3];
				pathCubicTo(strb, c1x, c1y, c2x, c2y, cx, cy);
				break;
			default:
				System.out.println("Not implemented! PathIterator type: "
						+ type);
			}
		}
		return strb;
	}

	@Override
	public void drawString(String text, double x, double y)
	{
		// TODO Auto-generated method stub

	}

}
