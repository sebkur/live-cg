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
package de.topobyte.livecg.util;

import java.awt.geom.GeneralPath;

import noawt.java.awt.Shape;
import noawt.java.awt.geom.AffineTransform;
import noawt.java.awt.geom.PathIterator;

public class NoAwtUtil
{

	public static java.awt.geom.AffineTransform convert(AffineTransform t)
	{
		double[] values = new double[6];
		t.getMatrix(values);
		return new java.awt.geom.AffineTransform(values);
	}

	public static AffineTransform convert(java.awt.geom.AffineTransform t)
	{
		double[] values = new double[6];
		t.getMatrix(values);
		return new AffineTransform(values);
	}

	public static java.awt.Shape convert(Shape shape)
	{
		GeneralPath path = new GeneralPath();
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
				path.moveTo(cx, cy);
				break;
			case PathIterator.SEG_LINETO:
				cx = coords[0];
				cy = coords[1];
				path.lineTo(cx, cy);
				break;
			case PathIterator.SEG_CLOSE:
				path.closePath();
				break;
			case PathIterator.SEG_QUADTO:
				cx = coords[2];
				cy = coords[3];
				double c1x = coords[0];
				double c1y = coords[1];
				path.quadTo(c1x, c1y, cx, cy);
				break;
			case PathIterator.SEG_CUBICTO:
				cx = coords[4];
				cy = coords[5];
				c1x = coords[0];
				c1y = coords[1];
				double c2x = coords[2];
				double c2y = coords[3];
				path.curveTo(c1x, c1y, c2x, c2y, cx, cy);
				break;
			}
		}
		return path;
	}
}
