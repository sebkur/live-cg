package fortune.sweep.export.svg;

import java.util.List;
import java.util.Locale;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fortune.sweep.gui.core.Color;
import fortune.sweep.gui.core.Coordinate;
import fortune.sweep.gui.core.Painter;

public class SvgPainter implements Painter
{
	private String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

	private Document doc;
	private Element root;

	private Color color;

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
						+ ";stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
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
		strb.append(String.format(Locale.US, "M %f,%f", start.getX(),
				start.getY()));

		for (int i = 1; i < points.size(); i++) {
			Coordinate c = points.get(i);
			strb.append(String.format(Locale.US, " %f,%f", c.getX(), c.getY()));
		}

		Element path = doc.createElementNS(svgNS, "path");
		path.setAttributeNS(
				null,
				"style",
				"fill:none;stroke:"
						+ getCurrentColor()
						+ ";stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1");
		path.setAttributeNS(null, "d", strb.toString());

		root.appendChild(path);
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
		circle.setAttributeNS(null, "stroke-width", "1");

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

}
