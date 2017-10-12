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
package de.topobyte.livecg.core.lina;

import de.topobyte.lina.Matrix;
import noawt.java.awt.geom.AffineTransform;

public class AwtTransformUtil
{

	public static Matrix convert(AffineTransform transform)
	{
		Matrix matrix = new Matrix(2, 3);
		matrix.setValue(0, 0, transform.getScaleX());
		matrix.setValue(0, 1, transform.getShearY());
		matrix.setValue(1, 0, transform.getShearX());
		matrix.setValue(1, 1, transform.getScaleY());
		matrix.setValue(2, 0, transform.getTranslateX());
		matrix.setValue(2, 1, transform.getTranslateY());
		return matrix;
	}

}
