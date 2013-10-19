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

package de.topobyte.livecg.geometryeditor.geometryeditor;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.geometryeditor.geometryeditor.action.MouseAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.NewAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.OpenAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.SaveAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeDescriptions;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeProvider;

public class Toolbar extends JToolBar
{

	private static final long serialVersionUID = 8604389649262908523L;

	public Toolbar(GeometryEditPane editPane,
			MouseModeProvider mouseModeProvider)
	{
		NewAction newAction = new NewAction(editPane);
		OpenAction openAction = new OpenAction(this, editPane);
		SaveAction saveAction = new SaveAction(this, editPane);

		add(newAction);
		add(openAction);
		add(saveAction);
		addSeparator();

		for (MouseMode mode : new MouseMode[] { MouseMode.SELECT_MOVE,
				MouseMode.SELECT_RECTANGULAR, MouseMode.EDIT, MouseMode.DELETE }) {
			MouseAction mouseAction = new MouseAction(null,
					MouseModeDescriptions.getShort(mode), mode,
					mouseModeProvider);
			JToggleButton button = new JToggleButton(mouseAction);
			add(button);
		}
	}
}
