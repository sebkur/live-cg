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
package de.topobyte.livecg.algorithms.voronoi.fortune.status;

public class FortuneStatusParser
{

	public static Position parse(String argument)
	{
		try {
			double value = Double.parseDouble(argument);
			return new PixelPosition(value);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (argument.startsWith("e")) {
			String rest = argument.substring(1);
			try {
				int value = Integer.parseInt(rest);
				return new EventPosition(value);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		throw new IllegalArgumentException();
	}
}
