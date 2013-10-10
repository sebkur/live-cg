/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

package de.topobyte.frechet.lineeditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;

public class LineEditMouseListener extends MouseAdapter
{
	private final LineEditPane editPane;

	private Integer end = null;

	public LineEditMouseListener(LineEditPane editPane)
	{
		this.editPane = editPane;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);
	}

	private Integer selectEnd(Coordinate coord)
	{
		Chain line = editPane.getLine();
		Coordinate c1 = line.getCoordinate(0);
		Coordinate c2 = line.getCoordinate(1);
		if (c1.distance(coord) < 10) {
			return 0;
		}
		if (c2.distance(coord) < 10) {
			return 1;
		}
		return null;
	}

	private Coordinate createCoordinate(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		if (x < 0) {
			x = 0;
		} else if (x > editPane.getMaxWidth()) {
			x = editPane.getMaxWidth();
		}
		if (y < 0) {
			y = 0;
		} else if (y > editPane.getMaxHeight()) {
			y = editPane.getMaxHeight();
		}
		Coordinate coord = new Coordinate(x, y);
		return coord;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);
		Coordinate coord = createCoordinate(e);
		end = selectEnd(coord);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);
		end = null;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (end == null) {
			return;
		}
		Coordinate coord = createCoordinate(e);
		editPane.getLine().setCoordinate(end, coord);
		editPane.repaint();

		editPane.triggerChange();
	}

}
