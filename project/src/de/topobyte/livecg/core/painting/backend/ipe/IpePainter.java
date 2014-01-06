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
package de.topobyte.livecg.core.painting.backend.ipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import noawt.java.awt.Rectangle;
import noawt.java.awt.Shape;
import noawt.java.awt.geom.AffineTransform;
import noawt.java.awt.geom.Rectangle2D;

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
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Image;
import de.topobyte.livecg.core.painting.Painter;

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

	public IpePainter(Document doc, Element root, int pwidth, int pheight)
	{
		this.doc = doc;
		this.root = root;
		int y = pheight;

		mxWs = AffineTransformUtil.scale(1, -1).multiplyFromRight(
				AffineTransformUtil.translate(0, -y));
		;
		trWs = new GeometryTransformer(mxWs);

		atWs = new AffineTransform();
		atWs.scale(1, -1);
		atWs.translate(0, -y);

		ipestyle = doc.createElementNS(null, "ipestyle");

		Element layout = doc.createElementNS(null, "layout");
		ipestyle.appendChild(layout);
		layout.setAttribute("paper", pwidth + " " + pheight);
		layout.setAttribute("origin", "0 0");
		layout.setAttribute("frame", pwidth + " " + pheight);

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

		IpePathBuilder pb = new IpePathBuilder(newline);
		pb.pathMoveTo(strb, applyTransforms(x1, y1));
		pb.pathLineTo(strb, applyTransforms(x2, y2));

		stroke(strb);
	}

	@Override
	public void drawPath(List<Coordinate> points, boolean close)
	{
		if (points.size() < 2) {
			return;
		}

		IpePathBuilder pb = new IpePathBuilder(newline);

		StringBuilder strb = new StringBuilder();
		Coordinate start = points.get(0);
		pb.pathMoveTo(strb, applyTransforms(start));

		for (int i = 1; i < points.size(); i++) {
			Coordinate c = points.get(i);
			pb.pathLineTo(strb, applyTransforms(c));
		}

		if (close) {
			pb.pathClose(strb);
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
		IpePathBuilder pb = new IpePathBuilder(newline);
		Coordinate start = chain.getCoordinate(0);
		pb.pathMoveTo(strb, applyTransforms(start));

		for (int i = 1; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			pb.pathLineTo(strb, applyTransforms(c));
		}

		if (chain.isClosed()) {
			pb.pathClose(strb);
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
		IpePathBuilder pb = new IpePathBuilder(newline);
		StringBuilder strb = pb.buildPath(tshape);
		stroke(strb);
	}

	@Override
	public void fill(Shape shape)
	{
		Shape tshape = applyTransforms(shape);
		IpePathBuilder pb = new IpePathBuilder(newline);
		StringBuilder strb = pb.buildPath(tshape);
		fill(strb);
	}

	@Override
	public void drawString(String text, double x, double y)
	{
		Coordinate c = applyTransforms(x, y);
		Element element = doc.createElement("text");
		element.setAttribute("pos",
				Double.toString(c.getX()) + " " + Double.toString(c.getY()));
		element.setAttribute("type", "label");

		// Need to produce valid latex, so replace "_" by "\_"
		text = text.replaceAll("_", "\\\\_");
		element.setTextContent(text);

		append(element);
	}

	/*
	 * Clipping
	 */

	private List<Shape> clipShapes = null;

	@Override
	public Object getClip()
	{
		if (clipShapes == null) {
			return null;
		}
		List<Shape> copy = new ArrayList<Shape>();
		for (Shape s : clipShapes) {
			copy.add(s);
		}
		return copy;
	}

	@Override
	public void setClip(Object clip)
	{
		if (clip == null) {
			clipShapes = null;
		} else {
			// TODO: this could be a bug, we should copy the input clip
			// to avoid insertions into the input object
			clipShapes = (List<Shape>) clip;
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
		if (clipShapes == null) {
			clipShapes = new ArrayList<Shape>();
		}
		shape = applyTransforms(shape);
		clipShapes.add(shape);
	}

	/*
	 * Appending elements to the document
	 */

	private void append(Element element)
	{
		Element e = page;
		if (clipShapes != null) {
			IpePathBuilder pb = new IpePathBuilder(" ");
			for (Shape shape : clipShapes) {
				Element g = doc.createElement("group");
				StringBuilder strb = new StringBuilder();
				strb.append(pb.buildPath(shape).toString());
				e.appendChild(g);
				g.setAttribute("clip", strb.toString());
				e = g;
			}
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
	public void drawImage(Image image, int x, int y)
	{
		IpeImage ipeImage;
		try {
			ipeImage = IpeImageEncoder.encode(image);
		} catch (IOException e) {
			logger.error("unable to draw image: " + e.getMessage());
			return;
		}

		int id = 1;

		Element bitmap = doc.createElement("bitmap");
		bitmap.setAttribute("id", "" + id);
		bitmap.setAttribute("width", Integer.toString(image.getWidth()));
		bitmap.setAttribute("height", Integer.toString(image.getHeight()));
		bitmap.setAttribute("encoding", "base64");
		bitmap.setAttribute("ColorSpace", "DeviceRGB");
		bitmap.setAttribute("Filter", "FlateDecode");
		bitmap.setAttribute("BitsPerComponent", "8");
		bitmap.setAttribute("length", "" + ipeImage.getLength());
		bitmap.setTextContent(ipeImage.getData());

		root.insertBefore(bitmap, ipestyle);

		Element img = doc.createElement("image");

		Coordinate c1 = applyTransforms(x, y + image.getHeight());
		Coordinate c2 = applyTransforms(x + image.getWidth(), y);
		img.setAttribute("bitmap", "" + id);
		img.setAttribute(
				"rect",
				Double.toString(c1.getX()) + " " + Double.toString(c1.getY())
						+ " " + Double.toString(c2.getX()) + " "
						+ Double.toString(c2.getY()));

		append(img);
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
