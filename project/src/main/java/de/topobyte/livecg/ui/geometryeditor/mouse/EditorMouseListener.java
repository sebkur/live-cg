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

package de.topobyte.livecg.ui.geometryeditor.mouse;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.viewports.scrolling.ViewportMouseListener;

public class EditorMouseListener extends EditPaneMouseListener
{
	private Map<MouseMode, ViewportMouseListener> listeners = new HashMap<>();

	public EditorMouseListener(GeometryEditPane editPane)
	{
		super(editPane);

		listeners.put(MouseMode.SELECT_MOVE, new MouseListenerSelectMove(
				editPane));
		listeners.put(MouseMode.EDIT, new MouseListenerEdit(editPane));
		listeners.put(MouseMode.DELETE, new MouseListenerDelete(editPane));
		listeners.put(MouseMode.ROTATE, new MouseListenerRotate(editPane));
		listeners.put(MouseMode.SCALE, new MouseListenerScale(editPane));
		listeners.put(MouseMode.SELECT_RECTANGULAR,
				new MouseListenerRectangularSelection(editPane));
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mouseClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mouseReleased(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mouseExited(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mouseMoved(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		ViewportMouseListener listener = listeners.get(editPane.getMouseMode());
		if (listener != null) {
			listener.mouseDragged(e);
		}
	}

}
