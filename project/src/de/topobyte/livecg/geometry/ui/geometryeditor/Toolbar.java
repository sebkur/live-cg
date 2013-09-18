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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.geometry.ui.geometryeditor.action.LoadAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.MouseAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.NewAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.SaveAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeProvider;

public class Toolbar extends JToolBar
{

	private static final long serialVersionUID = 8604389649262908523L;

	public Toolbar(GeometryEditPane editPane, MouseModeProvider mouseModeProvider)
	{
		NewAction newAction = new NewAction(editPane);
		LoadAction loadAction = new LoadAction(this, editPane);
		SaveAction saveAction = new SaveAction(this, editPane);

		MouseAction selectAction = new MouseAction("select / move",
				MouseMode.SELECT_MOVE, mouseModeProvider);
		MouseAction editAction = new MouseAction("add", MouseMode.EDIT,
				mouseModeProvider);
		MouseAction deleteAction = new MouseAction("delete", MouseMode.DELETE,
				mouseModeProvider);

		JToggleButton buttonSelect = new JToggleButton(selectAction);
		JToggleButton buttonEdit = new JToggleButton(editAction);
		JToggleButton buttonDelete = new JToggleButton(deleteAction);

		add(newAction);
		add(loadAction);
		add(saveAction);
		add(buttonSelect);
		add(buttonEdit);
		add(buttonDelete);
	}
}
