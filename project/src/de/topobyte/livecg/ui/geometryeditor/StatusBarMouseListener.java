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
package de.topobyte.livecg.ui.geometryeditor;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class StatusBarMouseListener extends MouseInputAdapter
{

	private StatusBar statusBar;

	public StatusBarMouseListener(StatusBar statusBar)
	{
		this.statusBar = statusBar;
		statusBar.setText(createText(null));
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		statusBar.setText(createText(e));
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		statusBar.setText(createText(null));
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		statusBar.setText(createText(e));
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		statusBar.setText(createText(e));
	}

	private String createText(MouseEvent e)
	{
		if (e == null) {
			return "Mouse: -";
		}
		return String.format("Mouse: %.2f, %.2f", (double) e.getX(),
				(double) e.getY());
	}

}
