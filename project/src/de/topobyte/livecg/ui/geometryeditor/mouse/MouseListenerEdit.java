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
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.LineSegment;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class MouseListenerEdit extends EditPaneMouseListener
{

	public MouseListenerEdit(GeometryEditPane editPane)
	{
		super(editPane);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!e.isControlDown()) {
				addCoordinateOrCreateNewChain(coord);
			} else {
				try {
					closeCurrentChain();
				} catch (CloseabilityException ex) {
					System.out.println("unable to close chain");
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			selectNothing();
			editPane.repaint();
		}
	}

	private void addCoordinateOrCreateNewChain(Coordinate coord)
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();

		if (chains.size() > 1 || nodes.size() > 1) {
			return;
		}

		AddPointResult addPointResult = selectAddPointMode();
		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.clearCurrentPolygons();

		switch (addPointResult.addPointMode) {
		default:
		case NONE:
			break;
		case NEW:
			Chain chain = new Chain();
			editPane.getContent().addChain(chain);
			chain.appendPoint(coord);
			editPane.addCurrentNode(chain.getLastNode());
			editPane.addCurrentChain(chain);
			break;
		case NEW_WITH_SELECTED:
			chain = new Chain();
			editPane.getContent().addChain(chain);
			chain.appendNode(addPointResult.node);
			chain.appendPoint(coord);
			editPane.addCurrentNode(chain.getLastNode());
			editPane.addCurrentChain(chain);
			break;
		case PREPEND:
			addPointResult.chain.prependPoint(coord);
			editPane.addCurrentNode(addPointResult.chain.getFirstNode());
			editPane.addCurrentChain(addPointResult.chain);
			break;
		case APPEND:
			addPointResult.chain.appendPoint(coord);
			editPane.addCurrentNode(addPointResult.chain.getLastNode());
			editPane.addCurrentChain(addPointResult.chain);
			break;
		}
		editPane.getContent().fireContentChanged();
	}

	private void closeCurrentChain() throws CloseabilityException
	{
		if (editPane.getCurrentChains().size() > 1) {
			return;
		}
		Chain chain = editPane.getCurrentChains().iterator().next();
		chain.setClosed(true);
		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.getContent().fireContentChanged();
	}

	private AddPointResult selectAddPointMode()
	{
		AddPointResult result = new AddPointResult();

		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();

		if (chains.size() == 0 && nodes.size() == 0) {
			// If nothing is selected, create a new chain
			result.addPointMode = AddPointMode.NEW;
		} else if (chains.size() == 1 && nodes.size() == 1) {
			// If one chain and one node is selected, extend the selected chain
			// TODO: only if the selected node is an endpoint of the selected
			// chain
			Node node = nodes.iterator().next();
			result.chain = chains.iterator().next();
			result.addPointMode = AddPointMode.APPEND;
			if (result.chain.getFirstNode() == node) {
				if (result.chain.getNumberOfNodes() != 1) {
					result.addPointMode = AddPointMode.PREPEND;
				}
			}
		} else if (nodes.size() == 1) {
			// If only one node is selected (no chain selected)
			Node node = nodes.iterator().next();
			if (node.getEndpointChains().size() == 1
					&& node.getEndpointChains().get(0).getNumberOfNodes() == 1) {
				// If the node is a single point
				result.chain = node.getEndpointChains().get(0);
				result.addPointMode = AddPointMode.APPEND;
			} else {
				// If the node is part of some chain
				result.addPointMode = AddPointMode.NEW_WITH_SELECTED;
				result.node = node;
			}
		} else {
			// Otherwise do nothing
			result.addPointMode = AddPointMode.NONE;
		}

		return result;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		boolean changed = editPane.setProspectNode(null);
		changed |= editPane.setProspectSegment(null);
		if (changed) {
			editPane.repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		boolean changed = editPane.setProspectNode(null);
		changed |= editPane.setProspectSegment(null);
		if (changed) {
			editPane.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);

		AddPointResult addPointResult = selectAddPointMode();
		boolean changed = false;
		switch (addPointResult.addPointMode) {
		default:
		case NONE:
			changed |= editPane.setProspectNode(null);
			changed |= editPane.setProspectSegment(null);
			break;
		case NEW:
			editPane.setProspectNode(new Node(coord));
			changed = true;
			break;
		case PREPEND:
			Coordinate start = addPointResult.chain.getFirstNode()
					.getCoordinate();
			editPane.setProspectNode(new Node(coord));
			editPane.setProspectSegment(new LineSegment(start, coord));
			changed = true;
			break;
		case APPEND:
			start = addPointResult.chain.getLastNode().getCoordinate();
			editPane.setProspectNode(new Node(coord));
			editPane.setProspectSegment(new LineSegment(start, coord));
			changed = true;
			break;
		case NEW_WITH_SELECTED:
			editPane.setProspectNode(new Node(coord));
			start = addPointResult.node.getCoordinate();
			editPane.setProspectSegment(new LineSegment(start, coord));
			changed = true;
			break;
		}
		if (changed) {
			editPane.repaint();
		}
	}

}
