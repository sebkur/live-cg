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
package de.topobyte.livecg.ui.geometryeditor.mouse;

import java.awt.event.MouseEvent;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.util.ListUtil;

public class MouseListenerDelete extends EditPaneMouseListener
{

	public MouseListenerDelete(GeometryEditPane editpane)
	{
		super(editpane);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		updateHighlights(getCoordinate(e));
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			deleteNearestObject(coord, e);

			editPane.setMouseHighlight((Node) null);
			editPane.setMouseHighlight((Chain) null);
			editPane.setMouseHighlight((Polygon) null);
			editPane.repaint();
		}
	}

	private void deleteNearestObject(Coordinate coord, MouseEvent e)
	{
		SelectResult nearest = nearestObject(coord);
		boolean changed = false;

		boolean ctrl = e.isControlDown();

		switch (nearest.mode) {
		default:
		case NONE:
			break;
		case NODE:
			Node node = nearest.node;
			List<Chain> selectedChains = editPane.getCurrentChains();
			List<Polygon> selectedPolygons = editPane.getCurrentPolygons();
			if (selectedChains.size() == 0 && selectedPolygons.size() == 0) {
				// Delete node from all contained elements
				for (Chain c : node.getChains()) {
					deleteNodeFromChain(c, node, false, ctrl);
				}
			} else {
				// Delete node only from selected elements
				for (Chain c : ListUtil.copy(selectedChains)) {
					deleteNodeFromChain(c, node, false, ctrl);
				}
				for (Polygon p : ListUtil.copy(selectedPolygons)) {
					deleteFromPolygon(p, node);
				}
			}
			if (editPane.getCurrentNodes().contains(node)) {
				editPane.removeCurrentNode(node);
			}
			changed = true;
			break;
		case CHAIN:
			Chain chain = nearest.chain;
			removeChainFromNodesOfChain(chain);
			editPane.removeChain(chain);
			changed = true;
			break;
		case POLYGON:
			Polygon polygon = nearest.polygon;
			removeChainFromNodesOfChain(polygon.getShell());
			for (Chain hole : polygon.getHoles()) {
				removeChainFromNodesOfChain(hole);
			}
			editPane.removePolygon(polygon);
			changed = true;
			break;
		}
		if (changed) {
			editPane.getContent().fireContentChanged();
		}
	}

	private void removeChainFromNodesOfChain(Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node n = chain.getNode(i);
			if (n.getChains().size() > 1) {
				n.removeChain(chain);
				n.removeEndpointChain(chain);
			}
		}
	}

	private void deleteFromPolygon(Polygon polygon, Node node)
	{
		Chain shell = polygon.getShell();
		deleteNodeFromChain(shell, node, false, false);
	}

	private void deleteNodeFromChain(Chain chain, Node node,
			boolean moveSelectedNodes, boolean split)
	{
		if (moveSelectedNodes) {
			if (editPane.getCurrentNodes().contains(node)) {
				if (chain.getNumberOfNodes() > 1) {
					if (chain.getFirstNode() == node) {
						editPane.addCurrentNode(chain.getNode(1));
					} else if (chain.getLastNode() == node) {
						editPane.addCurrentNode(chain.getNode(chain
								.getNumberOfNodes() - 2));
					}
				}
			}
		}
		if (!split || chain.getNumberOfNodes() == 1) {
			chain.remove(node);
			if (chain.getNumberOfNodes() < 3 && chain.isClosed()) {
				chain.setOpen();
				for (Polygon polygon : ListUtil.copy(chain.getPolygons())) {
					editPane.removePolygon(polygon);
					editPane.getContent().addChain(chain);
					chain.removePolygon(polygon);
				}
			}
			if (chain.getNumberOfNodes() == 0) {
				editPane.removeChain(chain);
				for (Polygon p : chain.getPolygons()) {
					if (chain == p.getShell()) {
						editPane.removePolygon(p);
					}
				}
			}
		} else {
			Chain result = chain.splitAtNode(node);
			if (result != null) {
				editPane.getContent().addChain(result);
			}
		}
		editPane.getContent().fireContentChanged();
	}

}
