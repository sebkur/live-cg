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
package de.topobyte.livecg.core.ui.geometryeditor.presets;

import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.topobyte.livecg.core.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.util.ImageLoader;
import de.topobyte.livecg.util.resources.ResourceFile;
import de.topobyte.livecg.util.resources.ResourceLoader;

public class PresetMenu
{

	private JMenu presetsMenu;

	private Icon folder = ImageLoader.load("res/images/24x24/folder.png");
	
	public PresetMenu(JMenu presets)
	{
		this.presetsMenu = presets;
	}

	public void build(GeometryEditPane editPane)
	{
		try {
			ResourceFile presetsDir = ResourceLoader.open("res/presets");
			if (presetsDir == null) {
				return;
			}
			if (presetsDir.isDirectory()) {
				List<ResourceFile> files = presetsDir.listFiles();
				for (ResourceFile file : files) {
					handle(presetsMenu, file, editPane);
				}
			}
		} catch (IOException e) {
			System.out.println("Error while building presets menu: "
					+ e.getMessage());
		}
	}

	private void handle(JMenu menu, ResourceFile file, GeometryEditPane editPane)
	{
		if (file.isDirectory()) {
			JMenu subMenu = new JMenu(file.getName());
			subMenu.setIcon(folder);
			List<ResourceFile> files = file.listFiles();
			if (files.size() > 0) {
				menu.add(subMenu);
				for (ResourceFile sub : files) {
					handle(subMenu, sub, editPane);
				}
			}
		} else {
			String fileName = file.getName();
			PresetMenuAction action = new PresetMenuAction(editPane, fileName, file);
			JMenuItem item = new JMenuItem(action);
			menu.add(item);
		}
	}
}
