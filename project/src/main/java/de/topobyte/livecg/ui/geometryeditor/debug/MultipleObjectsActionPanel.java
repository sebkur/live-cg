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
package de.topobyte.livecg.ui.geometryeditor.debug;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class MultipleObjectsActionPanel extends JPanel
{

	private static final long serialVersionUID = 4923635443745050622L;

	private GeometryEditPane editPane;

	public MultipleObjectsActionPanel(GeometryEditPane editPane)
	{
		this.editPane = editPane;

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor editor = new GridBagConstraintsEditor(c);

		editor.fill(GridBagConstraints.BOTH);
		editor.gridPos(GridBagConstraints.RELATIVE, 0);
	}

	public void update()
	{
		// Nothing to do here
	}
}
