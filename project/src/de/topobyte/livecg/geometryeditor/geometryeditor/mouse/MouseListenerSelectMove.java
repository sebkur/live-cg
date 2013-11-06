/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.geometryeditor.geometryeditor.mouse;

import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class MouseListenerSelectMove extends EditPaneMouseListener
{

	private static final double SNAP_TOLERANCE = 4;

	private DragInfo dragInfo = null;
	private Node snapNode = null;

	public MouseListenerSelectMove(GeometryEditPane editpane)
	{
		super(editpane);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			boolean shift = e.isShiftDown();
			selectObject(coord, shift);
			activateNodeForMove(coord);
			dragInfo = new DragInfo(getX(e), getY(e));
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			selectNothing();
			editPane.repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (snapNode != null) {
			meld(snapNode, currentMoveNode);
		}
		currentMoveNode = null;
		boolean update = editPane.setSnapHighlight(null);
		if (update) {
			editPane.repaint();
		}
		snapNode = null;
	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		updateHighlights(getCoordinate(e));
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);
		if (editPane.onlyOneNodeSelected() && currentMoveNode != null) {
			currentMoveNode.setCoordinate(coord);
			editPane.getContent().fireContentChanged();

			boolean update = false;
			if (e.isControlDown()) {
				Node nearest = editPane.getContent().getNearestDifferentNode(
						coord, currentMoveNode);
				if (nearest.getCoordinate().distance(coord) < SNAP_TOLERANCE
						/ editPane.getZoom()) {
					update |= editPane.setSnapHighlight(nearest);
					snapNode = nearest;
				} else {
					update |= editPane.setSnapHighlight(null);
					snapNode = null;
				}
			} else {
				update |= editPane.setSnapHighlight(null);
				snapNode = null;
			}
			if (update) {
				editPane.getContent().fireContentChanged();
			}
		} else {
			dragInfo.update(getX(e), getY(e));
			Coordinate delta = dragInfo.getDeltaToLast();
			translateSelectedObjects(delta);
			editPane.getContent().fireContentChanged();
		}
	}

	private void selectObject(Coordinate coord, boolean shift)
	{
		SelectResult nearest = nearestObject(coord);
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();
		boolean changed = false;

		switch (nearest.mode) {
		default:
		case NONE:
			if (!shift) {
				changed = selectNothing();
			}
			break;
		case NODE:
			Node node = nearest.node;
			if (!shift) {
				if (!editPane.getCurrentNodes().contains(node)) {
					if (polygons.size() == 0 && chains.size() == 1) {
						Chain chain = chains.iterator().next();
						if (chain.getFirstNode() != node
								&& chain.getLastNode() != node) {
							editPane.clearCurrentChains();
						}
						changed |= editPane.removeCurrentNode(chain
								.getFirstNode());
						changed |= editPane.removeCurrentNode(chain
								.getLastNode());
						changed |= editPane.addCurrentNode(node);
					} else {
						changed |= editPane.clearCurrentNodes();
						changed |= editPane.clearCurrentChains();
						changed |= editPane.clearCurrentPolygons();
						changed |= editPane.addCurrentNode(node);
					}
				}
			} else {
				if (editPane.getCurrentNodes().contains(node)) {
					changed |= editPane.removeCurrentNode(node);
				} else {
					changed |= editPane.addCurrentNode(node);
				}
			}
			break;
		case CHAIN:
			Chain chain = nearest.chain;
			if (!shift) {
				if (!editPane.getCurrentChains().contains(chain)) {
					changed |= editPane.clearCurrentNodes();
					changed |= editPane.clearCurrentChains();
					changed |= editPane.clearCurrentPolygons();
					changed |= editPane.addCurrentChain(chain);
				}
			} else {
				if (editPane.getCurrentChains().contains(chain)) {
					changed |= editPane.removeCurrentChain(chain);
				} else {
					changed |= editPane.addCurrentChain(chain);
				}
			}
			break;
		case POLYGON:
			Polygon polygon = nearest.polygon;
			if (!shift) {
				if (!editPane.getCurrentPolygons().contains(polygon)) {
					changed |= editPane.clearCurrentNodes();
					changed |= editPane.clearCurrentChains();
					changed |= editPane.clearCurrentPolygons();
					changed |= editPane.addCurrentPolygon(polygon);
				}
			} else {
				if (editPane.getCurrentPolygons().contains(polygon)) {
					changed |= editPane.removeCurrentPolygon(polygon);
				} else {
					changed |= editPane.addCurrentPolygon(polygon);
				}
			}
			break;
		}

		if (changed) {
			editPane.repaint();
		}
	}

	/*
	 * movement of nodes
	 */

	private Node currentMoveNode = null;

	private void activateNodeForMove(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		if (node != null) {
			if (node.getCoordinate().distance(coord) < MOUSE_TOLERANCE_SELECT
					/ editPane.getZoom()) {
				currentMoveNode = node;
			}
		}
		editPane.getContent().fireContentChanged();
	}

	private void meld(Node n1, Node n2)
	{
		for (Chain chain : n2.getChains()) {
			chain.replaceNode(n2, n1);
			n1.addChain(chain);
		}
		for (Chain chain : n2.getEndpointChains()) {
			n1.addEndpointChain(chain);
		}
		editPane.removeCurrentNode(n2);
		editPane.addCurrentNode(n1);
		if (editPane.getMouseHighlightNode() == n2) {
			editPane.setMouseHighlight((Node) null);
		}
		editPane.repaint();
	}

	private void translateSelectedObjects(Coordinate delta)
	{
		Set<Node> toTranslate = editPane.getSelectedNodes();

		for (Node node : toTranslate) {
			Coordinate old = node.getCoordinate();
			Coordinate c = new Coordinate(old.getX() + delta.getX(), old.getY()
					+ delta.getY());
			node.setCoordinate(c);
		}
	}

}
