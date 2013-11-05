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
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class SvgPainter implements Painter
{
	final static Logger logger = LoggerFactory.getLogger(SvgPainter.class);

	private String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

	private Document doc;
	private Element root;
	private Element defs;

	private Color color;
	private double width = 1;

	private AffineTransform transform = null;

	public SvgPainter(Document doc, Element root)
	{
		this.doc = doc;
		this.root = root;

		defs = doc.createElementNS(svgNS, "defs");
		root.appendChild(defs);
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
	public void drawRect(int x, int y, int width, int height)
	{
		Element rectangle = doc.createElementNS(svgNS, "rect");
		rectangle.setAttributeNS(null, "x", Integer.toString(x));
		rectangle.setAttributeNS(null, "y", Integer.toString(y));
		rectangle.setAttributeNS(null, "width", Integer.toString(width));
		rectangle.setAttributeNS(null, "height", Integer.toString(height));
		rectangle.setAttributeNS(null, "stroke", getCurrentColor());
		rectangle.setAttributeNS(null, "stroke-width", this.width + "px");

		append(rectangle);
	}

	@Override
	public void drawRect(double x, double y, double width, double height)
	{
		Element rectangle = doc.createElementNS(svgNS, "rect");
		rectangle.setAttributeNS(null, "x", Double.toString(x));
		rectangle.setAttributeNS(null, "y", Double.toString(y));
		rectangle.setAttributeNS(null, "width", Double.toString(width));
		rectangle.setAttributeNS(null, "height", Double.toString(height));
		rectangle.setAttributeNS(null, "stroke", getCurrentColor());
		rectangle.setAttributeNS(null, "stroke-width", this.width + "px");

		append(rectangle);
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

		append(rectangle);
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

		append(rectangle);
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

		append(path);
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

		append(circle);
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		Element circle = doc.createElementNS(svgNS, "circle");
		circle.setAttributeNS(null, "cx", Double.toString(x));
		circle.setAttributeNS(null, "cy", Double.toString(y));
		circle.setAttributeNS(null, "r", Double.toString(radius));
		circle.setAttributeNS(null, "fill", getCurrentColor());

		append(circle);
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

		append(path);
	}

	private void fill(StringBuilder strb)
	{
		Element path = doc.createElementNS(svgNS, "path");
		path.setAttributeNS(null, "style",
				"fill:" + getCurrentColor()
						+ ";fill-rule:evenodd;stroke:none;fill-opacity:"
						+ color.getAlpha());
		path.setAttributeNS(null, "d", strb.toString());

		append(path);
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
				logger.error("Not implemented! PathIterator type: " + type);
			}
		}
		return strb;
	}

	@Override
	public void drawString(String text, double x, double y)
	{
		Element element = doc.createElementNS(svgNS, "text");
		element.setAttributeNS(null, "style", "fill:" + getCurrentColor()
				+ ";stroke:none;fill-opacity:" + color.getAlpha()
				+ ";font-family:Sans;font-size:12px");
		element.setAttributeNS(null, "x", Double.toString(x));
		element.setAttributeNS(null, "y", Double.toString(y));
		element.setTextContent(text);

		append(element);
	}

	/*
	 * Clipping
	 */

	private static final String CLIP_PATH_PREFIX = "clip";
	private int clipId = 1;
	private List<Integer> clipIds = null;
	private Map<Integer, Shape> clipShapes = new HashMap<Integer, Shape>();

	@Override
	public Object getClip()
	{
		if (clipIds == null) {
			return null;
		}
		List<Integer> copy = new ArrayList<Integer>();
		for (int i : clipIds) {
			copy.add(i);
		}
		return copy;
	}

	@Override
	public void setClip(Object clip)
	{
		if (clip == null) {
			clipIds = null;
		} else {
			clipIds = (List<Integer>) clip;
		}
	}

	@Override
	public void clipRect(double x, double y, double width, double height)
	{
		clipArea(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void clipArea(Shape shape)
	{
		int index = clipId++;
		if (clipIds == null) {
			clipIds = new ArrayList<Integer>();
		}
		clipIds.add(index);
		clipShapes.put(index, shape);
		addToDefs(index, shape);
	}

	private void addToDefs(int index, Shape shape)
	{
		Element clipPath = doc.createElementNS(svgNS, "clipPath");
		clipPath.setAttributeNS(null, "id", CLIP_PATH_PREFIX + index);

		StringBuilder strb = buildPath(shape);

		Element path = doc.createElementNS(svgNS, "path");
		path.setAttributeNS(null, "d", strb.toString());

		if (transform != null) {
			path.setAttributeNS(null, "transform", transformValue());
		}

		clipPath.appendChild(path);
		defs.appendChild(clipPath);
	}

	/*
	 * Transformations
	 */

	@Override
	public AffineTransform getTransform()
	{
		if (transform == null) {
			return new AffineTransform();
		}
		return new AffineTransform(transform);
	}

	@Override
	public void setTransform(AffineTransform t)
	{
		transform = t;
	}

	private String transformValue()
	{
		double[] matrix = new double[6];
		transform.getMatrix(matrix);
		StringBuilder buffer = new StringBuilder();
		buffer.append("matrix(");
		for (int i = 0; i < matrix.length; i++) {
			buffer.append(matrix[i]);
			if (i < matrix.length - 1) {
				buffer.append(" ");
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	/*
	 * Appending elements to the document
	 */

	private void append(Element element)
	{
		Element e = root;
		if (clipIds != null) {
			for (int id : clipIds) {
				Element g = doc.createElementNS(svgNS, "g");
				g.setAttributeNS(null, "clip-path", "url(#" + CLIP_PATH_PREFIX
						+ id + ")");
				e.appendChild(g);
				e = g;
			}
		}
		if (transform != null && !transform.isIdentity()) {
			Element g = doc.createElementNS(svgNS, "g");
			g.setAttributeNS(null, "transform", transformValue());
			e.appendChild(g);
			e = g;
		}
		e.appendChild(element);
	}

	/*
	 * Image embedding
	 */

	private static int LINE_WIDTH = 76;

	private static String format(String text)
	{
		StringBuilder strb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		int length = text.length();
		int size = LINE_WIDTH;
		strb.append(newLine);
		for (int i = 0; i < length; i += size) {
			int end = i + size;
			if (end >= length) {
				end = length;
			}
			String line = text.substring(i, end);
			strb.append(line);
			strb.append(newLine);
		}
		return strb.toString();
	}

	@Override
	public void drawImage(BufferedImage image, int x, int y)
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			boolean written = ImageIO.write(image, "png", output);
			if (!written) {
				logger.error("unable to draw image: no writer found");
			}
		} catch (IOException e) {
			logger.error("unable to draw image: " + e.getMessage());
			return;
		}
		byte[] bytes = output.toByteArray();
		String base64 = Base64.encodeBase64String(bytes);

		Element element = doc.createElementNS(svgNS, "image");
		element.setAttributeNS(null, "x", Integer.toString(x));
		element.setAttributeNS(null, "y", Integer.toString(y));
		element.setAttributeNS(null, "width",
				Integer.toString(image.getWidth()));
		element.setAttributeNS(null, "height",
				Integer.toString(image.getHeight()));
		element.setAttributeNS(null, "xlink:href", "data:image/png;base64,"
				+ base64);

		append(element);
	}
}
