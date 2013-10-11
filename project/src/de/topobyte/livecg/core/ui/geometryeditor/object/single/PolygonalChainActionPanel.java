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
package de.topobyte.livecg.core.ui.geometryeditor.object.single;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.core.ui.geometryeditor.object.action.ConvexHullAction;
import de.topobyte.livecg.core.ui.geometryeditor.object.action.OpenCloseRingAction;
import de.topobyte.livecg.core.ui.geometryeditor.object.action.ToPolygonAction;
import de.topobyte.swing.layout.GridBagHelper;

public class PolygonalChainActionPanel extends JPanel
{

	private static final long serialVersionUID = -4039532101373210952L;

	private Chain chain;

	private JToggleButton closedButton;
	private JButton toPolygon;

	public PolygonalChainActionPanel(GeometryEditPane editPane, Chain chain)
	{
		this.chain = chain;
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		OpenCloseRingAction openCloseRingAction = new OpenCloseRingAction(
				editPane, chain);
		closedButton = new JToggleButton(openCloseRingAction);
		closedButton.setSelected(chain.isClosed());
		closedButton.setMargin(new Insets(0, 0, 0, 0));
		closedButton.setText(null);

		ToPolygonAction toPolygonAction = new ToPolygonAction(editPane, chain);
		toPolygon = new JButton(toPolygonAction);
		toPolygon.setMargin(new Insets(0, 0, 0, 0));
		toPolygon.setText(null);

		ConvexHullAction chAction = new ConvexHullAction(editPane);
		JButton convexHull = new JButton(chAction);
		convexHull.setMargin(new Insets(0, 0, 0, 0));
		convexHull.setText(null);

		c.fill = GridBagConstraints.BOTH;
		GridBagHelper.setGxGy(c, GridBagConstraints.RELATIVE, 0);
		add(closedButton, c);
		add(toPolygon, c);
		add(convexHull);
	}

	public void update()
	{
		closedButton.setSelected(chain.isClosed());
		closedButton.setEnabled(chain.getNumberOfNodes() > 2);

		toPolygon.setEnabled(chain.isClosed());
	}

}
