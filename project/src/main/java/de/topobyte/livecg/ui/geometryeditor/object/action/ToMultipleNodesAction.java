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
package de.topobyte.livecg.ui.geometryeditor.object.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class ToMultipleNodesAction extends BasicAction
{

	private static final long serialVersionUID = 7889373981786617466L;

	private GeometryEditPane editPane;
	private Node node;

	public ToMultipleNodesAction(GeometryEditPane editPane, Node node)
	{
		super("to multiple nodes", "Convert to multiple nodes",
				"res/images/24x24/unglue.png");
		this.editPane = editPane;
		this.node = node;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Chain> chains = node.getChains();
		List<Chain> endpointChains = node.getEndpointChains();
		for (Chain chain : chains) {
			List<Integer> indices = getNodePositions(chain);
			for (int i : indices) {
				Node r = new Node(new Coordinate(node.getCoordinate()));
				chain.setNode(i, r);
				r.addChain(chain);
				if (endpointChains.contains(chain)) {
					r.addEndpointChain(chain);
				}
			}
		}
		editPane.removeCurrentNode(node);
		editPane.repaint();
	}

	private List<Integer> getNodePositions(Chain chain)
	{
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			if (node == this.node) {
				indices.add(i);
			}
		}
		return indices;
	}

}
