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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import de.topobyte.livecg.geometry.ui.geom.CloseabilityException;
import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geom.Line;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geom.Polygon;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.util.ListUtil;

public class EditorMouseListener extends MouseAdapter
{
	private final GeometryEditPane editPane;

	public EditorMouseListener(GeometryEditPane editPane)
	{
		this.editPane = editPane;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);

		Coordinate coord = new Coordinate(e.getX(), e.getY());

		if (editPane.getMouseMode() == MouseMode.EDIT) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (!e.isControlDown()) {
					addCoordinateOrCreateNewLine(coord);
				} else {
					try {
						closeCurrentLine();
					} catch (CloseabilityException ex) {
						System.out.println("unable to close line");
					}
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				selectNothing();
				editPane.repaint();
			}
		}
		if (editPane.getMouseMode() == MouseMode.DELETE) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				deleteNearestPoint(coord);

				editPane.setMouseHighlight((Node) null);
				editPane.setMouseHighlight((Editable) null);
				editPane.setMouseHighlight((Polygon) null);
				editPane.repaint();
			}
		}
	}

	private void addCoordinateOrCreateNewLine(Coordinate coord)
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Editable> lines = editPane.getCurrentChains();

		if (lines.size() > 1 || nodes.size() > 1) {
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
			Editable line = new Editable();
			editPane.getContent().addChain(line);
			line.appendPoint(coord);
			editPane.addCurrentNode(line.getLastNode());
			editPane.addCurrentChain(line);
			break;
		case NEW_WITH_SELECTED:
			line = new Editable();
			editPane.getContent().addChain(line);
			line.appendNode(addPointResult.node);
			line.appendPoint(coord);
			editPane.addCurrentNode(line.getLastNode());
			editPane.addCurrentChain(line);
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

	private AddPointResult selectAddPointMode()
	{
		AddPointResult result = new AddPointResult();

		List<Node> nodes = editPane.getCurrentNodes();
		List<Editable> lines = editPane.getCurrentChains();

		if (lines.size() == 0 && nodes.size() == 0) {
			// If nothing is selected, create a new chain
			result.addPointMode = AddPointMode.NEW;
		} else if (lines.size() == 1 && nodes.size() == 1) {
			// If one chain and one node is selected, extend the selected chain
			Node node = nodes.iterator().next();
			result.chain = lines.iterator().next();
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

	private boolean selectNothing()
	{
		boolean changed = editPane.getCurrentChains().size() > 0
				|| editPane.getCurrentNodes().size() > 0;
		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.clearCurrentPolygons();
		return changed;
	}

	private void closeCurrentLine() throws CloseabilityException
	{
		if (editPane.getCurrentChains().size() > 1) {
			return;
		}
		Editable line = editPane.getCurrentChains().iterator().next();
		line.setClosed(true);
		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.getContent().fireContentChanged();
	}

	private void selectObject(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		Editable editable = editPane.getContent().getNearestChain(coord);
		Polygon polygon = editPane.getContent().getNearestPolygon(coord);
		double dNode = Double.MAX_VALUE, dChain = Double.MAX_VALUE, dPolygon = Double.MAX_VALUE;
		if (node != null) {
			dNode = node.getCoordinate().distance(coord);
		}
		if (editable != null) {
			dChain = editable.distance(coord);
		}
		if (polygon != null) {
			dPolygon = polygon.getShell().distance(coord);
		}
		List<Editable> chains = editPane.getCurrentChains();
		boolean changed = false;
		if (dNode < 5) {
			if (chains.size() == 1) {
				Editable chain = chains.iterator().next();
				if (chain.getFirstNode() != node && chain.getLastNode() != node) {
					editPane.clearCurrentChains();
				}
			}
			changed |= editPane.clearCurrentNodes();
			changed |= editPane.clearCurrentPolygons();
			changed |= editPane.addCurrentNode(node);
		} else if (dChain < 5) {
			changed |= editPane.clearCurrentNodes();
			changed |= editPane.clearCurrentChains();
			changed |= editPane.clearCurrentPolygons();
			changed |= editPane.addCurrentChain(editable);
		} else if (dPolygon < 5) {
			changed |= editPane.clearCurrentNodes();
			changed |= editPane.clearCurrentChains();
			changed |= editPane.clearCurrentPolygons();
			changed |= editPane.addCurrentPolygon(polygon);
		} else {
			changed = selectNothing();
		}
		if (changed) {
			editPane.repaint();
		}
	}

	private void deleteNearestPoint(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		Editable chain = editPane.getContent().getNearestChain(coord);
		Polygon polygon = editPane.getContent().getNearestPolygon(coord);
		double dNode = Double.MAX_VALUE, dChain = Double.MAX_VALUE, dPolygon = Double.MAX_VALUE;
		if (node != null) {
			dNode = node.getCoordinate().distance(coord);
		}
		if (chain != null) {
			dChain = chain.distance(coord);
		}
		if (polygon != null) {
			dPolygon = polygon.getShell().distance(coord);
		}
		boolean changed = false;
		if (dNode < 5) {
			List<Editable> selectedChains = editPane.getCurrentChains();
			List<Polygon> selectedPolygons = editPane.getCurrentPolygons();
			if (selectedChains.size() == 0 && selectedPolygons.size() == 0) {
				// Delete node from all contained elements
				for (Editable c : node.getChains()) {
					deleteNodeFromChain(c, node, false);
				}
				if (editPane.getCurrentNodes().contains(node)) {
					editPane.removeCurrentNode(node);
				}
			} else {
				// Delete node only from selected elements
				for (Editable c : ListUtil.copy(selectedChains)) {
					deleteNodeFromChain(c, node, false);
				}
				for (Polygon p : ListUtil.copy(selectedPolygons)) {
					deleteFromPolygon(p, node);
				}
			}
			changed = true;
		} else if (dChain < 5) {
			// TODO: delete complete chain
		} else if (dPolygon < 5) {
			// TODO: delete complete polygon
		}
		if (changed) {
			editPane.repaint();
		}
	}

	private void deleteNodeFromChain(Editable chain, Node node,
			boolean moveSelectedNodes)
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
		editPane.getContent().fireContentChanged();
	}

	private void deleteFromPolygon(Polygon polygon, Node node)
	{
		Editable shell = polygon.getShell();
		deleteNodeFromChain(shell, node, false);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);

		Coordinate coord = new Coordinate(e.getX(), e.getY());

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				selectObject(coord);
				activateNodeForMove(coord);
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				selectNothing();
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.EDIT) {
			editPane.setProspectNode(null);
			editPane.setProspectLine(null);
			// TODO: only if changed
			editPane.repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
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
	}

	private void meld(Node n1, Node n2)
	{
		for (Editable chain : n2.getChains()) {
			chain.replaceNode(n2, n1);
			n1.addChain(chain);
		}
		for (Editable chain : n2.getEndpointChains()) {
			n1.addEndpointChain(chain);
		}
		editPane.removeCurrentNode(n2);
		editPane.addCurrentNode(n1);
		editPane.repaint();
	}

	/*
	 * movement of nodes
	 */

	private Node currentMoveNode = null;

	private void activateNodeForMove(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		if (node.getCoordinate().distance(coord) < 5) {
			currentMoveNode = node;
		}
		editPane.getContent().fireContentChanged();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Coordinate coord = new Coordinate(e.getX(), e.getY());

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE
				|| editPane.getMouseMode() == MouseMode.DELETE) {
			Node node = editPane.getContent().getNearestNode(coord);
			Editable editable = editPane.getContent().getNearestChain(coord);
			Polygon polygon = editPane.getContent().getNearestPolygon(coord);
			double dNode = Double.MAX_VALUE, dChain = Double.MAX_VALUE, dPolygon = Double.MAX_VALUE;
			if (node != null) {
				dNode = node.getCoordinate().distance(coord);
			}
			if (editable != null) {
				dChain = editable.distance(coord);
			}
			if (polygon != null) {
				dPolygon = polygon.getShell().distance(coord);
			}
			boolean changed = false;
			if (dNode < 5) {
				changed = editPane.setMouseHighlight(node);
			} else if (dChain < 5) {
				changed = editPane.setMouseHighlight(editable);
			} else if (dPolygon < 5) {
				changed = editPane.setMouseHighlight(polygon);
			} else {
				changed |= editPane.setMouseHighlight((Node) null);
				changed |= editPane.setMouseHighlight((Editable) null);
				changed |= editPane.setMouseHighlight((Polygon) null);
			}
			if (changed) {
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.EDIT) {
			AddPointResult addPointResult = selectAddPointMode();
			switch (addPointResult.addPointMode) {
			default:
			case NONE:
				break;
			case NEW:
				editPane.setProspectNode(new Node(coord));
				editPane.repaint();
				break;
			case PREPEND:
				Coordinate start = addPointResult.chain.getFirstNode()
						.getCoordinate();
				editPane.setProspectNode(new Node(coord));
				editPane.setProspectLine(new Line(start, coord));
				editPane.repaint();
				break;
			case APPEND:
				start = addPointResult.chain.getLastNode().getCoordinate();
				editPane.setProspectNode(new Node(coord));
				editPane.setProspectLine(new Line(start, coord));
				editPane.repaint();
				break;
			case NEW_WITH_SELECTED:
				editPane.setProspectNode(new Node(coord));
				start = addPointResult.node.getCoordinate();
				editPane.setProspectLine(new Line(start, coord));
				editPane.repaint();
				break;
			}
		}
	}

	private Node snapNode = null;

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Coordinate coord = new Coordinate(e.getX(), e.getY());

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			if (currentMoveNode != null) {
				currentMoveNode.setCoordinate(coord);
				editPane.getContent().fireContentChanged();

				boolean update = false;
				if (e.isControlDown()) {
					Node nearest = editPane.getContent()
							.getNearestDifferentNode(coord, currentMoveNode);
					if (nearest.getCoordinate().distance(coord) < 4) {
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
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if (editPane.getMouseMode() == MouseMode.EDIT) {
			editPane.setProspectNode(null);
			editPane.setProspectLine(null);
			// TODO: only if changed
			editPane.repaint();
		}
	}
}
