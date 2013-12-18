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

import java.awt.Rectangle;
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

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.lina.AffineTransformUtil;
import de.topobyte.livecg.core.lina.AwtTransformUtil;
import de.topobyte.livecg.core.lina.Matrix;

public class IpePainter implements Painter
{
	final static Logger logger = LoggerFactory.getLogger(IpePainter.class);

	// Scaling to workspace
	private AffineTransform atWs;
	private Matrix mxWs;
	private GeometryTransformer trWs;

	// Applying the user transform
	private AffineTransform transform = null;
	private Matrix mxTransform;
	private GeometryTransformer trTransform;

	private Document doc;
	private Element root;
	private Element ipestyle;
	private Element page;

	private Color color;

	private static String newline = "\n";

	public IpePainter(Document doc, Element root)
	{
		this.doc = doc;
		this.root = root;

		mxWs = AffineTransformUtil.scale(1, -1);
		trWs = new GeometryTransformer(mxWs);

		atWs = new AffineTransform();
		atWs.scale(1, -1);

		ipestyle = doc.createElementNS(null, "ipestyle");

		for (int i = 0; i <= 100; i += 10) {
			Element opacity = doc.createElementNS(null, "opacity");
			ipestyle.appendChild(opacity);
			opacity.setAttributeNS(null, "name", i + "%");
			opacity.setAttributeNS(null, "value",
					String.format("%.2f", i / (double) 100));
		}

		page = doc.createElementNS(null, "page");
		root.appendChild(ipestyle);
		root.appendChild(page);
	}

	private Coordinate applyTransforms(double x, double y)
	{
		return applyTransforms(new Coordinate(x, y));
	}

	private Coordinate applyTransforms(Coordinate c)
	{
		if (transform != null) {
			c = trTransform.transform(c);
		}
		return trWs.transform(c);
	}

	private Shape applyTransforms(Shape shape)
	{
		Shape s = shape;
		if (transform != null) {
			s = transform.createTransformedShape(s);
		}
		return atWs.createTransformedShape(s);
	}

	private Shape applyUserTransforms(Shape shape)
	{
		Shape s = shape;
		if (transform != null) {
			s = transform.createTransformedShape(s);
		}
		return s;
	}

	@Override
	public void setColor(Color color)
	{
		this.color = color;
	}

	@Override
	public void drawRect(int x, int y, int width, int height)
	{
		Rectangle rect = new Rectangle(x, y, width, height);
		setMiterJoin();
		String j = join;
		setMiterJoin();
		draw(rect);
		join = j;
	}

	@Override
	public void drawRect(double x, double y, double width, double height)
	{
		Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
		setMiterJoin();
		String j = join;
		setMiterJoin();
		draw(rect);
		join = j;
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		Rectangle rect = new Rectangle(x, y, width, height);
		fill(rect);
	}

	@Override
	public void fillRect(double x, double y, double width, double height)
	{
		Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
		fill(rect);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		drawLine((double) x1, (double) y1, (double) x2, (double) y2);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		StringBuilder strb = new StringBuilder();

		pathMoveTo(strb, applyTransforms(x1, y1));
		pathLineTo(strb, applyTransforms(x2, y2));

		stroke(strb);
	}

	@Override
	public void drawPath(List<Coordinate> points, boolean close)
	{
		if (points.size() < 2) {
			return;
		}

		StringBuilder strb = new StringBuilder();
		Coordinate start = points.get(0);
		pathMoveTo(strb, applyTransforms(start));

		for (int i = 1; i < points.size(); i++) {
			Coordinate c = points.get(i);
			pathLineTo(strb, applyTransforms(c));
		}

		if (close) {
			pathClose(strb);
		}

		stroke(strb);
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		StringBuilder strb = new StringBuilder();
		Coordinate c = applyTransforms(x, y);

		pathCircle(strb, c, radius);

		stroke(strb);
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		StringBuilder strb = new StringBuilder();
		Coordinate c = applyTransforms(x, y);

		pathCircle(strb, c, radius);

		fill(strb);
	}

	private void pathCircle(StringBuilder strb, Coordinate c, double radius)
	{
		strb.append(radius);
		strb.append(" 0 0 ");
		strb.append(radius);
		strb.append(" ");
		strb.append(c.getX());
		strb.append(" ");
		strb.append(c.getY());
		strb.append(" e");
	}

	private String getCurrentColor()
	{
		float r = ((color.getRGB() & 0xFF0000) >>> 16) / 255.0f;
		float g = ((color.getRGB() & 0xFF00) >>> 8) / 255.0f;
		float b = ((color.getRGB() & 0xFF)) / 255.0f;
		return String.format("%f %f %f", r, g, b);
	}

	private static void pathMoveTo(StringBuilder strb, double x, double y)
	{
		strb.append(String.format(Locale.US, "%f %f m", x, y));
		strb.append(newline);
	}

	private static void pathMoveTo(StringBuilder strb, Coordinate c)
	{
		pathMoveTo(strb, c.getX(), c.getY());
	}

	private static void pathLineTo(StringBuilder strb, double x, double y)
	{
		strb.append(String.format(Locale.US, "%f %f l", x, y));
		strb.append(newline);
	}

	private static void pathLineTo(StringBuilder strb, Coordinate c)
	{
		pathLineTo(strb, c.getX(), c.getY());
	}

	private static void pathClose(StringBuilder strb)
	{
		strb.append("h");
		strb.append(newline);
	}

	private void pathQuadraticTo(StringBuilder strb, double x1, double y1,
			double x, double y)
	{
		strb.append(String.format(Locale.US, "%f %f", x1, y1));
		strb.append(newline);
		strb.append(String.format(Locale.US, "%f %f q", x, y));
		strb.append(newline);
	}

	private void pathCubicTo(StringBuilder strb, double x1, double y1,
			double x2, double y2, double x, double y)
	{
		strb.append(String.format(Locale.US, "%f %f", x1, y1));
		strb.append(newline);
		strb.append(String.format(Locale.US, "%f %f", x2, y2));
		strb.append(newline);
		strb.append(String.format(Locale.US, "%f %f c", x, y));
		strb.append(newline);
	}

	private void stroke(StringBuilder strb)
	{
		Element path = doc.createElementNS(null, "path");
		addStrokeAttributes(path);
		setOpacity(path);
		path.setTextContent(strb.toString());
		append(path);
	}

	private void fill(StringBuilder strb)
	{
		Element path = doc.createElementNS(null, "path");
		path.setAttributeNS(null, "fill", getCurrentColor());
		setOpacity(path);
		path.setTextContent(strb.toString());
		append(path);
	}

	private void setOpacity(Element path)
	{
		if (color.getAlpha() != 1) {
			double alpha = color.getAlpha();
			int a = (int) (Math.round(alpha * 10) * 10);
			path.setAttributeNS(null, "opacity", a + "%");
		}
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

	@Override
	public void drawChain(Chain chain)
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
		pathMoveTo(strb, applyTransforms(start));

		for (int i = 1; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			pathLineTo(strb, applyTransforms(c));
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
		Shape tshape = applyTransforms(shape);
		StringBuilder strb = buildPath(tshape);
		stroke(strb);
	}

	@Override
	public void fill(Shape shape)
	{
		Shape tshape = applyTransforms(shape);
		StringBuilder strb = buildPath(tshape);
		fill(strb);
	}

	private StringBuilder buildPath(Shape shape)
	{
		StringBuilder strb = new StringBuilder();
		strb.append(newline);

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
		Element element = doc.createElementNS(null, "text");
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
			// TODO: this could be a bug, we should copy the input clip
			// to avoid insertions into the input object
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
	}

	/*
	 * Appending elements to the document
	 */

	private void append(Element element)
	{
		Element e = page;
		if (clipIds != null) {
			for (int id : clipIds) {
				Element g = doc.createElementNS(null, "g");
				g.setAttributeNS(null, "clip-path", "url(#" + CLIP_PATH_PREFIX
						+ id + ")");
				e.appendChild(g);
				e = g;
			}
		}
		if (transform != null && !transform.isIdentity()) {
			Element g = doc.createElementNS(null, "g");
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

		Element element = doc.createElementNS(null, "image");
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

	/*
	 * Stroke
	 */

	private double width = 1.0;
	private float[] dash = null;
	private float phase = 0;

	private String join = null;

	private void setNormalJoin()
	{
		join = null;
	}

	private void setMiterJoin()
	{
		join = "0";
	}

	private void addStrokeAttributes(Element element)
	{
		if (dash == null) {
			element.setAttributeNS(null, "stroke", getCurrentColor());
			element.setAttribute("join", join != null ? join : "1");
			element.setAttributeNS(null, "pen", width + "");
			element.setAttributeNS(null, "cap", "1");
		} else {
			element.setAttributeNS(null, "stroke", getCurrentColor());
			element.setAttributeNS(null, "pen", width + "");
			element.setAttributeNS(null, "cap", "1");
			element.setAttributeNS(null, "join", "1");
			StringBuilder strb = new StringBuilder();
			for (int i = 0; i < dash.length; i++) {
				strb.append(dash[i]);
				if (i < dash.length - 1) {
					strb.append(",");
				}
			}
			element.setAttributeNS(null, "stroke-dasharray", strb.toString());
			element.setAttributeNS(null, "stroke-dashoffset", "" + phase);
			element.setAttributeNS(null, "stroke-opacity",
					"" + color.getAlpha());
		}

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
		mxTransform = AwtTransformUtil.convert(t);
		trTransform = new GeometryTransformer(mxTransform);
	}
}
