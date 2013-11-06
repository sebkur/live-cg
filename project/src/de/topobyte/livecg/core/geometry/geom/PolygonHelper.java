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

import de.topobyte.livecg.util.circular.IntRing;

public class PolygonHelper
{

	public static boolean isCounterClockwiseOriented(Polygon polygon)
	{
		return isCounterClockwiseOriented(polygon.getShell());
	}

	public static boolean isCounterClockwiseOriented(Chain chain)
	{
		double sum1 = 0, sum2 = 0;

		IntRing ring = new IntRing(chain.getNumberOfNodes());
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			int pre = ring.prevValue();
			int suc = ring.next().value();
			Node nodePre = chain.getNode(pre);
			Node nodeSuc = chain.getNode(suc);

			Coordinate c = node.getCoordinate();
			Coordinate cPre = nodePre.getCoordinate();
			Coordinate cSuc = nodeSuc.getCoordinate();

			double angle = GeomMath.angle(c, cPre, cSuc);

			sum1 += angle;
			sum2 += Math.PI * 2 - angle;
		}

		return sum1 <= sum2;
	}

	public static Coordinate center(Polygon polygon)
	{
		double x = 0, y = 0;
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Coordinate c = shell.getNode(i).getCoordinate();
			x += c.getX();
			y += c.getY();
		}
		x /= shell.getNumberOfNodes();
		y /= shell.getNumberOfNodes();
		return new Coordinate(x, y);
	}
}
