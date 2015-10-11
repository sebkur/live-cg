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
package de.topobyte.livecg.ui.geometryeditor.presets;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.presets.PresetMenu;

public class TestPresetBuilder
{
	public static void main(String[] args)
	{
		JMenu menu = new JMenu("Presets");
		PresetMenu presetMenu = new PresetMenu(menu);
		presetMenu.build(new GeometryEditPane());

		print(menu, 0);
	}

	private static void print(JMenuItem item, int depth)
	{
		for (int i = 0; i < depth; i++) {
			System.out.print(" ");
		}
		System.out.println(item.getText());
		if (item instanceof JMenu) {
			JMenu menu = (JMenu) item;
			for (int i = 0; i < menu.getItemCount(); i++) {
				JMenuItem subItem = menu.getItem(i);
				print(subItem, depth + 1);
			}
		}
	}
}
