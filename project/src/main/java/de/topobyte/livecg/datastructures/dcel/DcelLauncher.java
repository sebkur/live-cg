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
package de.topobyte.livecg.datastructures.dcel;

import javax.swing.JFrame;

import de.topobyte.livecg.ui.ContentLauncher;
import de.topobyte.livecg.ui.LaunchException;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class DcelLauncher implements ContentLauncher
{

	@Override
	public void launch(Content content, boolean exit) throws LaunchException
	{
		if (content.getChains().size() == 0
				&& content.getPolygons().size() == 0) {
			throw new LaunchException("the scene contains no objects");
		}
		DcelDialog dialog = new DcelDialog(content);

		if (exit) {
			dialog.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

}
