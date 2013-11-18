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
package de.topobyte.livecg.ui.geometryeditor.action.visualizations;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.datastructures.dcel.DcelDialog;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class DcelAction extends BasicAction
{

	private static final long serialVersionUID = -2845627216951446330L;

	private GeometryEditPane editPane;

	public DcelAction(GeometryEditPane editPane)
	{
		super("DCEL of the Arrangement", "Display the DCEL of the Arrangement",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Content content = editPane.getContent();
		new DcelDialog(content);
	}

}
