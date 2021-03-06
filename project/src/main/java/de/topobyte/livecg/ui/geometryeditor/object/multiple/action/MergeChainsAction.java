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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.util.action.SimpleAction;

public class MergeChainsAction extends SimpleAction
{

	private static final long serialVersionUID = 7889373981786617466L;

	final static Logger logger = LoggerFactory
			.getLogger(MergeChainsAction.class);

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
		List<Node> nodes = new ArrayList<>();
		List<Chain> chains = editPane.getCurrentChains();
		for (Chain chain : chains) {
			for (int i = 0; i < chain.getNumberOfNodes(); i++) {
				Node node = chain.getNode(i);
				List<Chain> cs = node.getEndpointChains();
				List<Chain> nonClosed = onlyNonClosed(cs);
				if (nonClosed.size() == 2 && chains.contains(nonClosed.get(0))
						&& chains.contains(nonClosed.get(1))) {
					if (!nodes.contains(node)) {
						nodes.add(node);
					}
				}
			}
		}
		printDebugInfo(nodes);
		for (Node node : nodes) {
			logger.debug("node: " + node);
			List<Chain> cs = node.getEndpointChains();
			List<Chain> nonClosed = onlyNonClosed(cs);
			logger.debug("chains: " + nonClosed);
			if (nonClosed.size() == 1) {
				Chain c = nonClosed.get(0);
				merge(c);
			} else {
				Chain c1 = nonClosed.get(0);
				Chain c2 = nonClosed.get(1);
				if (c1 == c2) {
					merge(c1);
				} else {
					merge(node, c1, c2, nodes);
				}
			}
		}
		editPane.getContent().fireContentChanged();
	}

	private List<Chain> onlyNonClosed(List<Chain> cs)
	{
		List<Chain> nonClosed = new ArrayList<>();
		for (Chain c : cs) {
			if (!c.isClosed()) {
				nonClosed.add(c);
			}
		}
		return nonClosed;
	}

	private void merge(Chain c)
	{
		logger.debug("merge: " + c);
		c.removeLastPoint();
		try {
			c.setClosed(true);
		} catch (CloseabilityException e1) {
			// TODO: can this happen?
		}
	}

	private void merge(Node node, Chain c1, Chain c2, List<Node> nodes)
	{
		logger.debug("merge: " + c1 + " + " + c2);
		printDebugInfo(nodes);
		Node c2first = c2.getFirstNode();
		Node c2last = c2.getLastNode();
		if (node == c1.getFirstNode() && c1.getFirstNode() == c2.getFirstNode()) {
			for (int i = 1; i < c2.getNumberOfNodes(); i++) {
				Node n = c2.getNode(i);
				c1.prependNode(n);
				n.removeChain(c2);
			}
		} else if (node == c1.getFirstNode()
				&& c1.getFirstNode() == c2.getLastNode()) {
			for (int i = c2.getNumberOfNodes() - 1; i >= 0; i--) {
				Node n = c2.getNode(i);
				c1.prependNode(n);
				n.removeChain(c2);
			}
		} else if (node == c1.getLastNode()
				&& c1.getLastNode() == c2.getFirstNode()) {
			for (int i = 1; i < c2.getNumberOfNodes(); i++) {
				Node n = c2.getNode(i);
				c1.appendNode(n);
				n.removeChain(c2);
			}
		} else if (node == c1.getLastNode()
				&& c1.getLastNode() == c2.getLastNode()) {
			for (int i = c2.getNumberOfNodes() - 1; i >= 0; i--) {
				Node n = c2.getNode(i);
				c1.appendNode(n);
				n.removeChain(c2);
			}
		}
		printDebugInfo(nodes);
		c2last.removeEndpointChain(c2);
		c2first.removeEndpointChain(c2);
		c2last.removeChain(c2);
		c2first.removeChain(c2);
		printDebugInfo(nodes);
		editPane.removeChain(c2);
	}

	private void printDebugInfo(List<Node> nodes)
	{
		logger.debug("******** STATUS");
		for (Node node : nodes) {
			List<Chain> cs = node.getEndpointChains();
			List<Chain> nonClosed = onlyNonClosed(cs);
			logger.debug("chains: " + nonClosed);
		}
	}
}
