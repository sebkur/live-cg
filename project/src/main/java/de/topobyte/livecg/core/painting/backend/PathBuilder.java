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
package de.topobyte.livecg.core.painting.backend;

import noawt.java.awt.Shape;
import noawt.java.awt.geom.AffineTransform;
import noawt.java.awt.geom.PathIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Coordinate;

public abstract class PathBuilder
{
	final static Logger logger = LoggerFactory.getLogger(PathBuilder.class);

	public StringBuilder buildPath(Shape shape)
	{
		StringBuilder strb = new StringBuilder();

		initPath(strb);

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

	public abstract void initPath(StringBuilder strb);

	public abstract void pathMoveTo(StringBuilder strb, double x, double y);

	public abstract void pathMoveTo(StringBuilder strb, Coordinate c);

	public abstract void pathLineTo(StringBuilder strb, double x, double y);

	public abstract void pathLineTo(StringBuilder strb, Coordinate c);

	public abstract void pathClose(StringBuilder strb);

	public abstract void pathQuadraticTo(StringBuilder strb, double x1,
			double y1, double x, double y);

	public abstract void pathCubicTo(StringBuilder strb, double x1, double y1,
			double x2, double y2, double x, double y);
}
