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
package de.topobyte.livecg.geometryeditor.geometryeditor.rectangle;

import de.topobyte.livecg.core.geometry.geom.Rectangle;

public class EightHandles
{
	private Rectangle r;
	private double d;

	public EightHandles(Rectangle rectangle, double d)
	{
		this.r = rectangle;
		this.d = d;
	}

	public Position get(double x, double y)
	{
		double w = r.getX2() - r.getX1();
		double h = r.getY2() - r.getY1();
		if (d(x, y, r.getX1(), r.getY1()) < d) {
			return Position.NW;
		} else if (d(x, y, r.getX2(), r.getY1()) < d) {
			return Position.NE;
		} else if (d(x, y, r.getX1(), r.getY2()) < d) {
			return Position.SW;
		} else if (d(x, y, r.getX2(), r.getY2()) < d) {
			return Position.SE;
		} else if (d(x, y, r.getX1(), r.getY1() + h / 2) < d) {
			return Position.W;
		} else if (d(x, y, r.getX2(), r.getY1() + h / 2) < d) {
			return Position.E;
		} else if (d(x, y, r.getX1() + w / 2, r.getY1()) < d) {
			return Position.N;
		} else if (d(x, y, r.getX1() + w / 2, r.getY2()) < d) {
			return Position.S;
		}
		return null;
	}

	private double d(double x1, double y1, double x2, double y2)
	{
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
}
