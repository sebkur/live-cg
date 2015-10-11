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
package de.topobyte.livecg.core.scrolling;

import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina.AffineTransformUtil;
import de.topobyte.livecg.core.lina.Matrix;

public class TransformHelper
{

	public static Matrix createMatrix(Rectangle scene, Viewport viewport)
	{
		Matrix shift = AffineTransformUtil.translate(-scene.getX1(),
				-scene.getY1());
		Matrix translate = AffineTransformUtil.translate(
				viewport.getPositionX(), viewport.getPositionY());
		Matrix scale = AffineTransformUtil.scale(viewport.getZoom(),
				viewport.getZoom());

		Matrix matrix = scale.multiplyFromRight(translate).multiplyFromRight(
				shift);
		return matrix;
	}

	public static Matrix createInverseMatrix(Rectangle scene, Viewport viewport)
	{
		return createMatrix(scene, viewport).invert();
	}

}
