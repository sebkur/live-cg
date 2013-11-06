/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.core.geometry.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Locale;

import de.topobyte.livecg.core.geometry.pointset.Point;
import de.topobyte.livecg.core.geometry.pointset.PointSet;
import de.topobyte.livecg.util.exception.ParseException;

public class PointSetReader
{

	public static PointSet read(InputStream in) throws IOException,
			ParseException
	{
		PointSet pointSet = new PointSet();

		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			String[] parts = line.split(",");
			if (parts.length != 2) {
				throw new ParseException("number of fields is not 2");
			}
			String sx = parts[0].trim();
			String sy = parts[1].trim();
			double x, y;
			try {
				x = numberFormat.parse(sx).doubleValue();
			} catch (java.text.ParseException e) {
				throw new ParseException("unable to parse x value: '" + sx
						+ "'");
			}
			try {
				y = numberFormat.parse(sy).doubleValue();
			} catch (java.text.ParseException e) {
				throw new ParseException("unable to parse y value: '" + sy
						+ "'");
			}
			pointSet.add(new Point(x, y));
		}

		return pointSet;
	}
}
