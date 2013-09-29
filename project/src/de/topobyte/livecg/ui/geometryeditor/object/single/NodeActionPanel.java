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
package de.topobyte.livecg.ui.geometryeditor.object.single;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.object.action.ToMultipleNodesAction;
import de.topobyte.swing.layout.GridBagHelper;

public class NodeActionPanel extends JPanel
{

	private static final long serialVersionUID = 6408336797693213234L;

	private JButton toMultipleNodes;

	public NodeActionPanel(GeometryEditPane editPane, Node node)
	{
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		ToMultipleNodesAction toMultipleNodesAction = new ToMultipleNodesAction(
				editPane, node);
		toMultipleNodes = new JButton(toMultipleNodesAction);
		toMultipleNodes.setMargin(new Insets(0, 0, 0, 0));
		toMultipleNodes.setText(null);

		toMultipleNodesAction.setEnabled(node.getChains().size() > 1);

		c.fill = GridBagConstraints.BOTH;
		GridBagHelper.setGxGy(c, GridBagConstraints.RELATIVE, 0);
		add(toMultipleNodes, c);
	}

}
