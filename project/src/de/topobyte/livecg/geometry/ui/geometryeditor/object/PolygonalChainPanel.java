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
package de.topobyte.livecg.geometry.ui.geometryeditor.object;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.object.action.OpenCloseRingAction;

public class PolygonalChainPanel extends JPanel
{

	private static final long serialVersionUID = 5640771403274002420L;

	private Editable editable;
	private JLabel label;
	private JToggleButton closedButton;

	public PolygonalChainPanel(GeometryEditPane editPane, Editable editable)
	{
		this.editable = editable;
		setLayout(new GridBagLayout());
		label = new JLabel();

		closedButton = new JToggleButton("closed");
		closedButton.setSelected(editable.isClosed());
		closedButton.setAction(new OpenCloseRingAction(editPane, editable));

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		add(label, c);
		add(closedButton, c);
		
		update();
	}

	public void update()
	{
		label.setText(getLabelText());
		closedButton.setSelected(editable.isClosed());
		closedButton.setEnabled(editable.getNumberOfNodes() > 2);
	}

	private String getLabelText()
	{
		return "polygonal chain with " + editable.getNumberOfNodes()
				+ " nodes";
	}

}
