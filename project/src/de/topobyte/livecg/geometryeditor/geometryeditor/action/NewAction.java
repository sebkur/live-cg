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

package de.topobyte.livecg.geometryeditor.geometryeditor.action;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.Content;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class NewAction extends BasicAction
{

	private static final long serialVersionUID = -7366716164763979848L;

	private final GeometryEditPane editPane;

	public NewAction(GeometryEditPane editPane)
	{
		super("New", "Start with a fresh and empty document",
				"org/freedesktop/tango/22x22/actions/document-new.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Content content = new Content();
		editPane.setContent(content);

		content.fireContentChanged();
	}

}
