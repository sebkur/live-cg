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
package de.topobyte.livecg.core.geometry.geom;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.lina.Matrix;
import de.topobyte.lina.Vector;
import de.topobyte.lina.VectorType;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.CoordinateTransformer;
import de.topobyte.viewports.geometry.Rectangle;

public class GeometryTransformer extends CoordinateTransformer
{

	public GeometryTransformer(Matrix matrix)
	{
		super(matrix);
	}

	@Override
	public Coordinate transform(Coordinate c)
	{
		Vector v = new Vector(3, VectorType.Column);
		v.setValue(0, c.getX());
		v.setValue(1, c.getY());
		v.setValue(2, 1);
		Matrix r = matrix.multiplyFromRight(v);
		double x = r.getValue(0, 0);
		double y = r.getValue(0, 1);
		return new Coordinate(x, y);
	}

	public Chain transform(Chain chain)
	{
		Chain r = new Chain();
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			Coordinate ct = transform(c);
			r.appendPoint(ct);
		}
		try {
			r.setClosed(chain.isClosed());
		} catch (CloseabilityException e) {
			// should not happen
		}
		return r;
	}

	public Polygon transform(Polygon polygon)
	{
		Chain shell = polygon.getShell();
		List<Chain> holes = polygon.getHoles();
		Chain tshell = transform(shell);
		List<Chain> tholes = new ArrayList<>();
		for (Chain hole : holes) {
			tholes.add(transform(hole));
		}
		return new Polygon(tshell, tholes);
	}

	public Rectangle transform(Rectangle rectangle)
	{
		Coordinate c1 = new Coordinate(rectangle.getX1(), rectangle.getY1());
		Coordinate c2 = new Coordinate(rectangle.getX2(), rectangle.getY2());
		Coordinate tc1 = transform(c1);
		Coordinate tc2 = transform(c2);
		return new Rectangle(tc1.getX(), tc1.getY(), tc2.getX(), tc2.getY());
	}
}
