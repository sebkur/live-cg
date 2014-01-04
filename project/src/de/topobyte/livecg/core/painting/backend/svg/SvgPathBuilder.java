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
package de.topobyte.livecg.core.painting.backend.svg;

import java.util.Locale;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.painting.backend.PathBuilder;

public class SvgPathBuilder extends PathBuilder
{

	@Override
	public void initPath(StringBuilder strb)
	{
		// This is a no-op
	}

	@Override
	public void pathMoveTo(StringBuilder strb, double x, double y)
	{
		strb.append(String.format(Locale.US, "M %f,%f", x, y));
	}

	@Override
	public void pathMoveTo(StringBuilder strb, Coordinate c)
	{
		pathMoveTo(strb, c.getX(), c.getY());
	}

	@Override
	public void pathLineTo(StringBuilder strb, double x, double y)
	{
		strb.append(String.format(Locale.US, " %f,%f", x, y));
	}

	@Override
	public void pathLineTo(StringBuilder strb, Coordinate c)
	{
		pathLineTo(strb, c.getX(), c.getY());
	}

	@Override
	public void pathClose(StringBuilder strb)
	{
		strb.append(" Z");
	}

	@Override
	public void pathQuadraticTo(StringBuilder strb, double x1, double y1,
			double x, double y)
	{
		strb.append(String.format(Locale.US, "Q %f %f %f %f", x1, y1, x, y));
	}

	@Override
	public void pathCubicTo(StringBuilder strb, double x1, double y1,
			double x2, double y2, double x, double y)
	{
		strb.append(String.format(Locale.US, "C %f %f %f %f %f %f", x1, y1, x2,
				y2, x, y));
	}

}
