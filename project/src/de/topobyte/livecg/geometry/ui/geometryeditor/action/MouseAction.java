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

package de.topobyte.livecg.geometry.ui.geometryeditor.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeListener;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeProvider;

public class MouseAction extends BasicAction implements MouseModeListener
{

	private static final long serialVersionUID = 5507751575393463234L;

	static final Logger logger = LoggerFactory.getLogger(MouseAction.class);

	private MouseModeProvider mouseModeProvider;
	private MouseMode mouseMode;

	// keep track of our recognized state here
	private boolean active = false;

	private void updateActivityStatus()
	{
		active = mouseModeProvider.getMouseMode() == mouseMode;
	}

	public MouseAction(String name, String description, MouseMode mouseMode,
			MouseModeProvider mouseModeProvider)
	{
		super(name, description, getIconPath(mouseMode));
		this.mouseMode = mouseMode;
		this.mouseModeProvider = mouseModeProvider;
		mouseModeProvider.addMouseModeListener(this);
		updateActivityStatus();
	}

	private static String getIconPath(MouseMode mouseMode)
	{
		switch (mouseMode) {
		case DELETE:
			return "res/images/24x24/delete.png";
		case EDIT:
			return "res/images/24x24/add.png";
		case SELECT_MOVE:
			return "res/images/24x24/move.png";
		case SELECT_RECTANGULAR:
			return "res/images/24x24/select.png";
		default:
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		mouseModeProvider.setMouseMode(mouseMode);
		firePropertyChange(Action.SELECTED_KEY, false, true);
		updateActivityStatus();
	}

	@Override
	public void mouseModeChanged(MouseMode mode)
	{
		// logger.debug("button: " + mouseMode);
		// logger.debug("change: " + mode);
		boolean myMode = mode == mouseMode;
		// logger.debug("my mode: " + myMode);
		if (myMode) {
			// our mode has been activated
			// System.out.println("me selected? " + active);
			if (!active) {
				firePropertyChange(Action.SELECTED_KEY, false, true);
				updateActivityStatus();
			}
		} else {
			// another mode has been activated
			// System.out.println("me selected? " + active);
			firePropertyChange(Action.SELECTED_KEY, true, false);
			updateActivityStatus();
		}
	}

	@Override
	public Object getValue(String key)
	{
		if (key.equals(Action.SELECTED_KEY)) {
			return new Boolean(mouseModeProvider.getMouseMode() == mouseMode);
		}
		return super.getValue(key);
	}

}
