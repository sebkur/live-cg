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

package de.topobyte.livecg.geometry.ui.misc;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.MouseAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.NewAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.OpenAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.SaveAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.SelectAllAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.SelectNothingAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeDescriptions;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeProvider;

public class Menu extends JMenuBar
{

	private static final long serialVersionUID = -7983876851509766368L;

	public Menu(GeometryEditPane editPane, MouseModeProvider mouseModeProvider)
	{
		JMenu file = new JMenu("File");
		JMenu tools = new JMenu("Tools");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");
		add(file);
		add(tools);
		add(edit);
		add(help);

		JMenuItem exit = new JMenuItem(new ExitAction());
		file.add(new NewAction(editPane));
		file.add(new OpenAction(this, editPane));
		file.add(new SaveAction(this, editPane));
		file.add(exit);

		for (MouseMode mode : new MouseMode[] { MouseMode.SELECT_MOVE,
				MouseMode.EDIT, MouseMode.DELETE }) {
			MouseAction mouseAction = new MouseAction(
					MouseModeDescriptions.getShort(mode),
					MouseModeDescriptions.getLong(mode), mode,
					mouseModeProvider);
			JMenuItem mouseItem = new JMenuItem(mouseAction);
			tools.add(mouseItem);
		}
		
		edit.add(new JMenuItem(new SelectNothingAction(editPane)));
		edit.add(new JMenuItem(new SelectAllAction(editPane)));

		JMenuItem about = new JMenuItem(new AboutAction());
		JMenuItem license = new JMenuItem(new LicenseAction());
		help.add(about);
		help.add(license);
	}
}
