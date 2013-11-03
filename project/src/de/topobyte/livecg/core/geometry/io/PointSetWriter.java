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
package de.topobyte.livecg.core.geometry.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

import de.topobyte.livecg.core.geometry.pointset.Point;
import de.topobyte.livecg.core.geometry.pointset.PointSet;

public class PointSetWriter
{

	public static void write(PointSet pointSet, OutputStream out)
			throws IOException
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

		PrintWriter printer = new PrintWriter(out);
		for (Point point : pointSet.getPoints()) {
			printer.println(numberFormat.format(point.getX()) + ", "
					+ numberFormat.format(point.getY()));
		}

		printer.close();
	}
}
