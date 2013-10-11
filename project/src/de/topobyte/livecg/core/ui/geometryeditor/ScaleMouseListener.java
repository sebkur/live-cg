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

package de.topobyte.livecg.core.ui.geometryeditor;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import de.topobyte.livecg.core.ui.geometryeditor.scale.Scale;

public class ScaleMouseListener extends MouseInputAdapter
{

	private final Scale scaleX;
	private final Scale scaleY;

	public ScaleMouseListener(Scale scaleX, Scale scaleY)
	{
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	/*
	 * enter / exit / move
	 */

	@Override
	public void mouseEntered(MouseEvent e)
	{
		super.mouseEntered(e);
		updateScale(e);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		super.mouseExited(e);
		updateScale(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		super.mouseMoved(e);
		updateScale(e);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		super.mouseMoved(e);
		updateScale(e);
	}

	private void updateScale(MouseEvent e)
	{
		scaleX.setMarker(e.getX());
		scaleY.setMarker(e.getY());

		scaleX.repaint();
		scaleY.repaint();
	}

}
