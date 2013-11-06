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
package de.topobyte.livecg.geometryeditor.geometryeditor.mouse;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.scrolling.ViewportWithSignals;

public class ViewportMouseListener extends MouseAdapter
{

	private ViewportWithSignals viewport;

	public ViewportMouseListener(ViewportWithSignals viewport)
	{
		this.viewport = viewport;
	}

	protected double getX(MouseEvent e)
	{
		return e.getX() / viewport.getZoom() - viewport.getPositionX();
	}

	protected double getY(MouseEvent e)
	{
		return e.getY() / viewport.getZoom() - viewport.getPositionY();
	}

	protected Coordinate getCoordinate(MouseEvent e)
	{
		double posX = viewport.getPositionX();
		double posY = viewport.getPositionY();
		return new Coordinate(e.getX() / viewport.getZoom() - posX, e.getY()
				/ viewport.getZoom() - posY);
	}

}
