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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.FilePropertiesDialog;

public class TestFilePropertiesDialog
{

	public static void main(String[] args)
	{
		Content content = new Content(500.3, 300);
		FilePropertiesDialog dialog = new FilePropertiesDialog(null, content);

		dialog.setLocationByPlatform(true);
		dialog.pack();
		dialog.setVisible(true);

		dialog.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}

		});
	}
}
