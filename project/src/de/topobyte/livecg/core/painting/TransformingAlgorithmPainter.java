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
package de.topobyte.livecg.core.painting;

import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina.AffineTransformUtil;
import de.topobyte.livecg.core.lina.Matrix;

public abstract class TransformingAlgorithmPainter extends
		BasicAlgorithmPainter
{

	protected Rectangle scene;
	protected GeometryTransformer transformer;

	public TransformingAlgorithmPainter(Rectangle scene, Painter painter)
	{
		super(painter);
		this.scene = scene;
	}

	@Override
	public void paint()
	{
		Matrix shift = AffineTransformUtil.translate(-scene.getX1(),
				-scene.getY1());
		Matrix translate = AffineTransformUtil.translate(positionX, positionY);
		Matrix scale = AffineTransformUtil.scale(zoom, zoom);

		Matrix matrix = scale.multiplyFromRight(translate).multiplyFromRight(
				shift);
		transformer = new GeometryTransformer(matrix);
	}

}
