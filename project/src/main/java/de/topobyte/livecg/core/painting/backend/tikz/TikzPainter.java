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
package de.topobyte.livecg.core.painting.backend.tikz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.lina.AffineTransformUtil;
import de.topobyte.lina.Matrix;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.lina.AwtTransformUtil;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Image;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.backend.ImageUtil;
import de.topobyte.livecg.util.CloneUtil;
import de.topobyte.viewports.geometry.Coordinate;
import noawt.java.awt.Shape;
import noawt.java.awt.geom.AffineTransform;
import noawt.java.awt.geom.Area;
import noawt.java.awt.geom.Rectangle2D;

public class TikzPainter implements Painter
{

	final static Logger logger = LoggerFactory.getLogger(TikzPainter.class);

	private StringBuilder header;
	private StringBuilder buffer;

	private double scale;

	// Scaling to unity square
	private AffineTransform atUnity;
	private Matrix mxUnity;
	private GeometryTransformer trUnity;

	// Applying the user transform
	private AffineTransform transform = null;
	private Matrix mxTransform;
	private GeometryTransformer trTransform;

	// Safety rectangle to clip geometries with, to avoid latex errors
	private Rectangle2D safetyRect = new Rectangle2D.Double(0, -1, 1, 1);
	private Area safetyArea = new Area(safetyRect);

	private String newline = "\n";

	private Color color;
	private double width = 1.0;
	private float[] dash = null;
	private float phase = 0;

	private double sceneWidth;
	private double imageWidth;
	private File images;
	private String imagePathPrefix;

	public TikzPainter(StringBuilder header, StringBuilder buffer,
			double scale, double sceneWidth, double imageWidth, File images,
			String imagePathPrefix)
	{
		this.header = header;
		this.buffer = buffer;
		this.scale = scale;
		this.sceneWidth = sceneWidth;
		this.imageWidth = imageWidth;
		this.images = images;
		this.imagePathPrefix = imagePathPrefix;

		mxUnity = AffineTransformUtil.scale(scale, -scale);
		trUnity = new GeometryTransformer(mxUnity);

		atUnity = new AffineTransform();
		atUnity.scale(scale, -scale);

		buffer.append("\\clip (0,0) rectangle (1,-1);");
		buffer.append(newline);
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

	private double convertToMM(double value)
	{
		return value / 4.0;
	}

	private String line()
	{
		return String.format("line width=%.5fmm", convertToMM(width));
	}

	private Set<String> definedNames = new HashSet<>();

	private String appendColorDefine()
	{
		int rgb = color.getRGB();
		String name = String.format("%06X", rgb);
		if (!definedNames.contains(name)) {
			definedNames.add(name);
			double r = ((rgb & 0xff0000) >> 16) / 255.0;
			double g = ((rgb & 0xff00) >> 8) / 255.0;
			double b = (rgb & 0xff) / 255.0;
			header.append(String.format("\\definecolor{" + name
					+ "}{rgb}{%.5f,%.5f,%.5f}", r, g, b));
			header.append(newline);
		}
		return name;
	}

	private String colorDefinition()
	{
		String c = appendColorDefine();
		if (color.getAlpha() == 1.0) {
			return "color=" + c;
		}
		return "color=" + c + ", opacity="
				+ String.format("%.2f", color.getAlpha());
	}

	private void appendDraw()
	{
		String c = "";
		if (color != null) {
			c = ", " + colorDefinition();
		}
		if (dash == null) {
			buffer.append("\\draw[" + line() + ", join=round, cap=round" + c
					+ "] ");
		} else {
			String d = createDash();
			buffer.append("\\draw[" + line() + ", join=round, cap=round" + c
					+ ", " + d + "] ");
		}
	}

	private String createDash()
	{
		StringBuilder strb = new StringBuilder();
		strb.append("dash pattern=");
		for (int i = 0; i < dash.length; i++) {
			if (i > 0) {
				strb.append(" ");
			}
			String d = ((i % 2) == 0) ? "on" : "off";
			strb.append(d);
			strb.append(" ");
			strb.append(convertToMM(dash[i]));
			strb.append("mm");
		}
		strb.append(", dash phase=");
		strb.append(phase);
		return strb.toString();
	}

	private void appendFill()
	{
		String c = colorDefinition();
		buffer.append("\\fill[" + c + "] ");
	}

	private void appendFillEvenOdd()
	{
		String c = colorDefinition();
		buffer.append("\\fill[" + c + ", even odd rule] ");
	}

	private void append(Coordinate c)
	{
		append(buffer, c);
	}

	private void append(StringBuilder strb, Coordinate c)
	{
		strb.append(String.format("(%.5f,%.5f)", c.getX(), c.getY()));
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
		return trUnity.transform(c);
	}

	private Shape applyTransforms(Shape shape)
	{
		Shape s = shape;
		if (transform != null) {
			s = transform.createTransformedShape(s);
		}
		return atUnity.createTransformedShape(s);
	}

	private Shape applyUserTransforms(Shape shape)
	{
		Shape s = shape;
		if (transform != null) {
			s = transform.createTransformedShape(s);
		}
		return s;
	}

	private void appendRect(double x, double y, double width, double height)
	{
		Coordinate c1 = applyTransforms(x, y);
		Coordinate c2 = applyTransforms(x + width, y + height);
		buffer.append(String.format("(%.5f, %.5f) rectangle (%.5f, %.5f);",
				c1.getX(), c1.getY(), c2.getX(), c2.getY()));
		buffer.append(newline);
	}

	private void appendCircle(double x, double y, double radius)
	{
		Coordinate c = applyTransforms(x, y);
		buffer.append(String.format("(%.5f,%.5f) circle (%.5f);", c.getX(),
				c.getY(), radius * scale));
		buffer.append(newline);
	}

	@Override
	public void drawRect(int x, int y, int width, int height)
	{
		drawRect((double) x, (double) y, (double) width, (double) height);
	}

	@Override
	public void drawRect(double x, double y, double width, double height)
	{
		appendClipScopeBegin();

		appendDraw();
		appendRect(x, y, width, height);

		appendClipScopeEnd();
	}

	@Override
	public void fillRect(int x, int y, int width, int height)
	{
		fillRect((double) x, (double) y, (double) width, (double) height);
	}

	@Override
	public void fillRect(double x, double y, double width, double height)
	{
		appendClipScopeBegin();

		appendFill();
		appendRect(x, y, width, height);

		appendClipScopeEnd();
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		drawLine((double) x1, (double) y1, (double) x2, (double) y2);
	}

	@Override
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		appendClipScopeBegin();

		appendDraw();
		append(applyTransforms(x1, y1));
		buffer.append(" -- ");
		append(applyTransforms(x2, y2));
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
	}

	@Override
	public void drawPath(List<Coordinate> points, boolean close)
	{
		appendClipScopeBegin();

		appendDraw();
		for (int i = 0; i < points.size(); i++) {
			append(applyTransforms(points.get(i)));
			if (i < points.size() - 1) {
				buffer.append(" -- ");
			}
		}
		if (close) {
			buffer.append(" -- cycle");
		}
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
	}

	@Override
	public void drawCircle(double x, double y, double radius)
	{
		appendClipScopeBegin();

		appendDraw();
		appendCircle(x, y, radius);

		appendClipScopeEnd();
	}

	@Override
	public void fillCircle(double x, double y, double radius)
	{
		appendClipScopeBegin();

		appendFill();
		appendCircle(x, y, radius);

		appendClipScopeEnd();
	}

	private void appendChain(Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			append(applyTransforms(chain.getCoordinate(i)));
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
		appendClipScopeBegin();

		appendDraw();
		appendChain(chain);
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
	}

	@Override
	public void drawPolygon(Polygon polygon)
	{
		appendClipScopeBegin();

		appendDraw();
		Chain shell = polygon.getShell();
		appendChain(shell);
		for (Chain hole : polygon.getHoles()) {
			buffer.append(" ");
			appendChain(hole);
		}
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
	}

	@Override
	public void fillPolygon(Polygon polygon)
	{
		appendClipScopeBegin();

		appendFillEvenOdd();
		Chain shell = polygon.getShell();
		appendChain(shell);
		for (Chain hole : polygon.getHoles()) {
			buffer.append(" ");
			appendChain(hole);
		}
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
	}

	private void appendClipScopeBegin()
	{
		if (clipShapes != null && !clipShapes.isEmpty()) {
			appendScopeBegin();
			appendClip();
		}
	}

	private void appendClipScopeEnd()
	{
		if (clipShapes != null && !clipShapes.isEmpty()) {
			appendScopeEnd();
		}
	}

	private void appendScopeBegin()
	{
		buffer.append("\\begin{scope}");
		buffer.append(newline);
	}

	private void appendScopeEnd()
	{
		buffer.append("\\end{scope}");
		buffer.append(newline);
	}

	private void appendClip()
	{
		if (clipShapes.isEmpty()) {
			return;
		}
		TikzPathBuilder pb = new TikzPathBuilder(safetyRect);
		for (int i = 0; i < clipShapes.size(); i++) {
			Shape shape = clipShapes.get(i);
			StringBuilder buf = pb.buildPath(atUnity
					.createTransformedShape(shape));
			buffer.append("\\clip ");
			buffer.append(buf.toString());
			buffer.append(";");
			buffer.append(newline);
		}
	}

	@Override
	public void draw(Shape shape)
	{
		Shape tshape = applyTransforms(shape);

		appendClipScopeBegin();

		appendDraw();
		TikzPathBuilder pb = new TikzPathBuilder(safetyRect);
		StringBuilder path = pb.buildPath(tshape);
		buffer.append(path.toString());
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
	}

	@Override
	public void fill(Shape shape)
	{
		Shape tshape = applyTransforms(shape);

		Area area = new Area(tshape);
		area.intersect(safetyArea);

		appendClipScopeBegin();

		appendFillEvenOdd();
		TikzPathBuilder pb = new TikzPathBuilder(safetyRect);
		StringBuilder path = pb.buildPath(area);
		buffer.append(path.toString());
		buffer.append(";");
		buffer.append(newline);

		appendClipScopeEnd();
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
		return CloneUtil.clone(clipShapes);
	}

	@Override
	public void setClip(Object clip)
	{
		if (clip == null) {
			clipShapes = null;
		}
		clipShapes = CloneUtil.clone((List<Shape>) clip);
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
			clipShapes = new ArrayList<>();
		}
		shape = applyUserTransforms(shape);
		clipShapes.add(shape);
	}

	/*
	 * Text
	 */

	@Override
	public void drawString(String text, double x, double y)
	{
		appendDraw();
		append(applyTransforms(x, y));
		text = text.replaceAll("_", "\\\\_");
		String node = "node[anchor=west] {" + text + "};";
		buffer.append(node);
	}

	/*
	 * Images
	 */

	private int imageCoutner = 1;

	@Override
	public void drawImage(Image image, int x, int y)
	{
		BufferedImage im = ImageUtil.convert(image);

		appendDraw();
		append(applyTransforms(x, y));
		String node = "node[inner sep=0pt, below right]";
		String imageName = "image" + (imageCoutner++) + ".png";
		File output = new File(images, imageName);
		String relative = imagePathPrefix + "/" + imageName;
		double w = image.getWidth() / sceneWidth * imageWidth;
		String include = " {\\includegraphics[width=" + w + "cm]{" + relative
				+ "}};";
		buffer.append(newline);
		buffer.append(node);
		buffer.append(newline);
		buffer.append(include);
		buffer.append(newline);

		output.getParentFile().mkdirs();
		try {
			ImageIO.write(im, "png", output);
		} catch (IOException e) {
			logger.error("Error while writing image.");
			logger.error("Image path: " + output);
			logger.error("Exception message: " + e.getMessage());
		}
	}

}
