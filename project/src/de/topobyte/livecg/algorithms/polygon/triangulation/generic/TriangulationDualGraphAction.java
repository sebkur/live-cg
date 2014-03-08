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
package de.topobyte.livecg.algorithms.polygon.triangulation.generic;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import de.topobyte.livecg.ui.LaunchException;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class TriangulationDualGraphAction extends BasicAction
{

	private static final long serialVersionUID = -602204318277526170L;

	private GeometryEditPane editPane;

	public TriangulationDualGraphAction(GeometryEditPane editPane)
	{
		super("Triangulation with dual graph",
				"Show the triangulation of the polygon with its dual graph",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try {
			new TriangulationDualGraphLauncher().launch(editPane.getContent());
		} catch (LaunchException e) {
			JOptionPane.showMessageDialog(editPane, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
