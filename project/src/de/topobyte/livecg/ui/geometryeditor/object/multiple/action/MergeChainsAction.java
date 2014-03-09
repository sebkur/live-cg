/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.ui.geometryeditor.object.multiple.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class MergeChainsAction extends BasicAction
{

	private static final long serialVersionUID = 7889373981786617466L;

	private GeometryEditPane editPane;

	public MergeChainsAction(GeometryEditPane editPane)
	{
		super("merge chains", "Merge connected chains when possible",
				"res/images/24x24/way.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Node> nodes = new ArrayList<Node>();
		List<Chain> chains = editPane.getCurrentChains();
		for (Chain chain : chains) {
			for (int i = 0; i < chain.getNumberOfNodes(); i++) {
				Node node = chain.getNode(i);
				// TODO: do not count closed chains
				if (node.getEndpointChains().size() == 2) {
					if (!nodes.contains(node)) {
						nodes.add(node);
					}
				}
			}
		}
		for (Node node : nodes) {
			List<Chain> endpointChains = node.getEndpointChains();
			if (endpointChains.size() == 1) {
				Chain c = endpointChains.get(0);
				merge(c);
			} else {
				Chain c1 = endpointChains.get(0);
				Chain c2 = endpointChains.get(1);
				if (c1 == c2) {
					merge(c1);
				} else {
					merge(c1, c2);
				}
			}
		}
		editPane.getContent().fireContentChanged();
	}

	private void merge(Chain c)
	{
		c.removeLastPoint();
		try {
			c.setClosed(true);
		} catch (CloseabilityException e1) {
			// TODO: can this happen?
		}
	}

	private void merge(Chain c1, Chain c2)
	{
		Node c2first = c2.getFirstNode();
		Node c2last = c2.getLastNode();
		if (c1.getFirstNode() == c2.getFirstNode()) {
			for (int i = 1; i < c2.getNumberOfNodes(); i++) {
				Node n = c2.getNode(i);
				c1.prependNode(n);
				n.removeChain(c2);
			}
		} else if (c1.getFirstNode() == c2.getLastNode()) {
			for (int i = c2.getNumberOfNodes() - 1; i >= 0; i--) {
				Node n = c2.getNode(i);
				c1.prependNode(n);
				n.removeChain(c2);
			}
		} else if (c1.getLastNode() == c2.getFirstNode()) {
			for (int i = 1; i < c2.getNumberOfNodes(); i++) {
				Node n = c2.getNode(i);
				c1.appendNode(n);
				n.removeChain(c2);
			}
		} else if (c1.getLastNode() == c2.getLastNode()) {
			for (int i = c2.getNumberOfNodes() - 1; i >= 0; i--) {
				Node n = c2.getNode(i);
				c1.appendNode(n);
				n.removeChain(c2);
			}
		}
		c2last.removeEndpointChain(c2);
		c2first.removeEndpointChain(c2);
		editPane.removeChain(c2);
	}
}
