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

import de.topobyte.lina.Matrix;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.viewports.geometry.Coordinate;
import de.topobyte.viewports.geometry.Rectangle;
import de.topobyte.viewports.scrolling.TransformHelper;

public abstract class TransformingVisualizationPainter
		extends BasicVisualizationPainter
{

	protected Rectangle scene;
	protected GeometryTransformer transformer;

	public TransformingVisualizationPainter(Rectangle scene, Painter painter)
	{
		super(painter);
		this.scene = scene;
	}

	public void setScene(Rectangle scene)
	{
		this.scene = scene;
	}

	public void preparePaint()
	{
		Matrix matrix = TransformHelper.createMatrix(scene, this);
		transformer = new GeometryTransformer(matrix);
	}

	protected void fillBackground(Color color)
	{
		painter.setColor(color);
		Coordinate t1 = transformer
				.transform(new Coordinate(scene.getX1(), scene.getY1()));
		Coordinate t2 = transformer
				.transform(new Coordinate(scene.getX2(), scene.getY2()));
		painter.fillRect(t1.getX(), t1.getY(), t2.getX() - t1.getX(),
				t2.getY() - t1.getY());
	}
}
