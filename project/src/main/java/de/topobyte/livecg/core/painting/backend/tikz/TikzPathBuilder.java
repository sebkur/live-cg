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

import noawt.com.bric.geom.Clipper;
import noawt.java.awt.Shape;
import noawt.java.awt.geom.GeneralPath;
import noawt.java.awt.geom.Rectangle2D;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.painting.backend.PathBuilder;

public class TikzPathBuilder extends PathBuilder
{

	private Rectangle2D safetyRect;

	public TikzPathBuilder(Rectangle2D safetyRect)
	{
		this.safetyRect = safetyRect;
	}

	@Override
	public StringBuilder buildPath(Shape shape)
	{
		GeneralPath clipped = Clipper.clipToRect(shape, safetyRect);
		return super.buildPath(clipped);
	}

	@Override
	public void initPath(StringBuilder strb)
	{
		// nothing to do here
	}

	@Override
	public void pathMoveTo(StringBuilder strb, Coordinate c)
	{
		strb.append(" ");
		append(strb, c);
	}

	@Override
	public void pathLineTo(StringBuilder strb, Coordinate c)
	{
		strb.append(" -- ");
		append(strb, c);
	}

	@Override
	public void pathMoveTo(StringBuilder strb, double cx, double cy)
	{
		strb.append(" ");
		append(strb, new Coordinate(cx, cy));
	}

	@Override
	public void pathLineTo(StringBuilder strb, double cx, double cy)
	{
		strb.append(" -- ");
		append(strb, new Coordinate(cx, cy));
	}

	@Override
	public void pathClose(StringBuilder strb)
	{
		strb.append(" -- ");
		strb.append("cycle");
	}

	@Override
	public void pathQuadraticTo(StringBuilder strb, double c1x, double c1y,
			double cx, double cy)
	{
		strb.append(" .. controls ");
		append(strb, new Coordinate(c1x, c1y));
		strb.append(" .. ");
		append(strb, new Coordinate(cx, cy));
	}

	@Override
	public void pathCubicTo(StringBuilder strb, double c1x, double c1y,
			double c2x, double c2y, double cx, double cy)
	{
		strb.append(" .. controls ");
		append(strb, new Coordinate(c1x, c1y));
		strb.append(" and ");
		append(strb, new Coordinate(c2x, c2y));
		strb.append(" .. ");
		append(strb, new Coordinate(cx, cy));
	}

	private void append(StringBuilder strb, Coordinate c)
	{
		strb.append(String.format("(%.5f,%.5f)", c.getX(), c.getY()));
	}

}
