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
package de.topobyte.livecg.algorithms.jts.buffer;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import de.topobyte.livecg.ui.LaunchException;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.util.action.SimpleAction;

public class PolygonBufferAction extends SimpleAction
{

	private static final long serialVersionUID = 8237600362605952257L;

	private GeometryEditPane editPane;

	public PolygonBufferAction(GeometryEditPane editPane)
	{
		super("Polygon Buffer", "Visualize the Polygon Buffer",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		try {
			new PolygonBufferLauncher().launch(editPane.getContent(), false);
		} catch (LaunchException e) {
			JOptionPane.showMessageDialog(editPane, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
