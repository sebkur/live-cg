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
package de.topobyte.livecg.ui.geometryeditor.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.util.action.SimpleAction;

public class HighlightEndpointNodesAction extends SimpleAction
{

	private static final long serialVersionUID = -2428195809704521270L;

	private GeometryEditPane editPane;

	public HighlightEndpointNodesAction(GeometryEditPane editPane)
	{
		super("Endpoints", "Highlight endpoint nodes",
				"res/images/24x24/empty.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		editPane.setDebugHighlightEndpoints(!editPane
				.isDebugHighlightEndpoints());
		firePropertyChange(Action.SELECTED_KEY, false, true);
		editPane.repaint();
	}

	@Override
	public Object getValue(String key)
	{
		if (key.equals(Action.SELECTED_KEY)) {
			return editPane.isDebugHighlightEndpoints();
		}
		return super.getValue(key);
	}
}
