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
package de.topobyte.livecg.core.geometry.geom;

public class CrossingsTestHelper
{
	public static boolean covers(Chain chain, Chain other)
	{
		CrossingsTest test = new CrossingsTest(chain);
		boolean covers = true;
		for (int i = 0; i < other.getNumberOfNodes(); i++) {
			Coordinate c = other.getCoordinate(i);
			boolean cc = test.covers(c);
			if (cc) {
				continue;
			}
			covers = false;
			break;
		}
		return covers;
	}
}
