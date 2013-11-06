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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.lina.Matrix;

public class ViewportMouseAdapter<T extends Viewport & HasScene> extends
		MouseAdapter
{
	private T viewport;

	public ViewportMouseAdapter(T viewport)
	{
		this.viewport = viewport;
	}

	public Coordinate getSceneCoordinate(MouseEvent e)
	{
		Matrix matrix = TransformHelper.createInverseMatrix(
				viewport.getScene(), viewport);
		GeometryTransformer transformer = new GeometryTransformer(matrix);
		Coordinate c = transformer
				.transform(new Coordinate(e.getX(), e.getY()));
		return c;
	}

}
