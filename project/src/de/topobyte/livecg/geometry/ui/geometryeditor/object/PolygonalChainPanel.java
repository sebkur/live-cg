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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.topobyte.livecg.geometry.ui.geom.Chain;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.object.action.OpenCloseRingAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.object.action.ToPolygonAction;
import de.topobyte.swing.layout.GridBagHelper;

public class PolygonalChainPanel extends JPanel
{

	private static final long serialVersionUID = 5640771403274002420L;

	private Chain chain;
	private JLabel label;
	private JToggleButton closedButton;
	private JButton toPolygon;

	public PolygonalChainPanel(GeometryEditPane editPane, Chain chain)
	{
		this.chain = chain;
		setLayout(new GridBagLayout());
		label = new JLabel();

		closedButton = new JToggleButton("closed");
		closedButton.setSelected(chain.isClosed());
		closedButton.setAction(new OpenCloseRingAction(editPane, chain));

		toPolygon = new JButton("to polygon");
		toPolygon.setAction(new ToPolygonAction(editPane, chain));

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;

		GridBagHelper.setGxGy(c, 0, 0);
		add(label, c);
		GridBagHelper.setGxGy(c, 0, 1);
		add(closedButton, c);
		GridBagHelper.setGxGy(c, 0, 2);
		add(toPolygon, c);

		GridBagHelper.setGxGy(c, 0, 3);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(new JPanel(), c);

		update();
	}

	public void update()
	{
		label.setText(getLabelText());
		closedButton.setSelected(chain.isClosed());
		closedButton.setEnabled(chain.getNumberOfNodes() > 2);

		toPolygon.setEnabled(chain.isClosed());
	}

	private String getLabelText()
	{
		return "Object: polygonal chain with " + chain.getNumberOfNodes() + " nodes";
	}

}
