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
import javax.swing.JTextField;

import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.layout.GridBagHelper;

public class NodePanel extends JPanel
{

	private static final long serialVersionUID = 5640771403274002420L;

	private Node node;
	private JLabel label;
	private JTextField inputX, inputY;

	public NodePanel(GeometryEditPane editPane, Node node)
	{
		this.node = node;
		setLayout(new GridBagLayout());
		label = new JLabel();

		inputX = new JTextField();
		inputY = new JTextField();

		GridBagConstraints c = new GridBagConstraints();

		GridBagHelper.setGxGyGwGh(c, 0, 0, 2, 1);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);

		GridBagHelper.setGxGyGwGh(c, 0, 1, 1, 1);
		GridBagHelper.setWxWyF(c, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

		c.gridx = 0;
		add(inputX, c);
		c.gridx = 1;
		add(inputY, c);

		GridBagHelper.setGxGyGwGh(c, 0, 2, 2, 1);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(new JPanel(), c);

		update();
	}

	public void update()
	{
		Coordinate c = node.getCoordinate();
		label.setText(getLabelText());
		inputX.setText("" + c.getX());
		inputY.setText("" + c.getY());
	}

	private String getLabelText()
	{
		return "Object: node";
	}

}
