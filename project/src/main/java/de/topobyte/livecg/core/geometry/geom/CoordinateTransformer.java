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

import de.topobyte.livecg.core.lina.Matrix;
import de.topobyte.livecg.core.lina.Vector;
import de.topobyte.livecg.core.lina.VectorType;

public class CoordinateTransformer
{

	protected Matrix matrix;

	public CoordinateTransformer(Matrix matrix)
	{
		this.matrix = matrix;
	}

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

}
