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

package de.topobyte.livecg.geometryeditor.geometryeditor.mouse;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Line;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina2.Vector;
import de.topobyte.livecg.geometryeditor.geometryeditor.Content;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometryeditor.geometryeditor.rectangle.EightHandles;
import de.topobyte.livecg.geometryeditor.geometryeditor.rectangle.Position;
import de.topobyte.livecg.util.ListUtil;

public class EditorMouseListener extends ViewportMouseListener
{
	final static Logger logger = LoggerFactory
			.getLogger(EditorMouseListener.class);

	private final GeometryEditPane editPane;

	public EditorMouseListener(GeometryEditPane editPane)
	{
		super(editPane);
		this.editPane = editPane;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		super.mouseClicked(e);

		Coordinate coord = getCoordinate(e);

		if (editPane.getMouseMode() == MouseMode.EDIT) {
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
		} else if (editPane.getMouseMode() == MouseMode.DELETE) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				deleteNearestObject(coord);

				editPane.setMouseHighlight((Node) null);
				editPane.setMouseHighlight((Chain) null);
				editPane.setMouseHighlight((Polygon) null);
				editPane.repaint();
			}
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

	private boolean selectNothing()
	{
		boolean changed = editPane.getCurrentChains().size() > 0
				|| editPane.getCurrentNodes().size() > 0;
		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.clearCurrentPolygons();
		return changed;
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

	private void selectObjects(Rectangle rectangle, boolean shift, boolean alt)
	{
		boolean changed = false;
		if (!shift) {
			changed |= editPane.clearCurrentNodes();
			changed |= editPane.clearCurrentChains();
			changed |= editPane.clearCurrentPolygons();
		}

		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();

		Content content = editPane.getContent();
		for (Chain chain : content.getChains()) {
			boolean complete = true;
			for (int i = 0; i < chain.getNumberOfNodes(); i++) {
				Node node = chain.getNode(i);
				Coordinate c = node.getCoordinate();
				if (GeomMath.contains(rectangle, c)) {
					if (shift && alt) {
						editPane.removeCurrentNode(node);
					} else {
						if (!nodes.contains(node)) {
							editPane.addCurrentNode(node);
						}
					}
				} else {
					complete = false;
				}
			}
			if (complete && chain.getNumberOfNodes() > 1) {
				if (shift && alt) {
					editPane.removeCurrentChain(chain);
				} else {
					if (!chains.contains(chain)) {
						editPane.addCurrentChain(chain);
					}
				}
			}
		}
		for (Polygon polygon : content.getPolygons()) {
			boolean complete = true;
			Chain shell = polygon.getShell();
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				Node node = shell.getNode(i);
				Coordinate c = node.getCoordinate();
				if (GeomMath.contains(rectangle, c)) {
					if (shift && alt) {
						editPane.removeCurrentNode(node);
					} else {
						if (!nodes.contains(node)) {
							editPane.addCurrentNode(node);
						}
					}
				} else {
					complete = false;
				}
			}
			for (Chain hole : polygon.getHoles()) {
				for (int i = 0; i < hole.getNumberOfNodes(); i++) {
					Node node = hole.getNode(i);
					Coordinate c = node.getCoordinate();
					if (GeomMath.contains(rectangle, c)) {
						if (shift && alt) {
							editPane.removeCurrentNode(node);
						} else {
							if (!nodes.contains(node)) {
								editPane.addCurrentNode(node);
							}
						}
					} else {
						complete = false;
					}
				}
			}
			if (complete) {
				if (shift && alt) {
					editPane.removeCurrentPolygon(polygon);
				} else {
					if (!polygons.contains(polygon)) {
						editPane.addCurrentPolygon(polygon);
					}
				}
			}
		}

		if (changed) {
			editPane.repaint();
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

	private void deleteNearestObject(Coordinate coord)
	{
		SelectResult nearest = nearestObject(coord);
		boolean changed = false;

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
					deleteNodeFromChain(c, node, false);
				}
			} else {
				// Delete node only from selected elements
				for (Chain c : ListUtil.copy(selectedChains)) {
					deleteNodeFromChain(c, node, false);
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

	private SelectResult nearestObject(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		Chain chain = editPane.getContent().getNearestChain(coord);
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
		SelectResult selectResult = new SelectResult();
		selectResult.node = node;
		selectResult.chain = chain;
		selectResult.polygon = polygon;
		if (dNode < 5) {
			selectResult.mode = SelectMode.NODE;
		} else if (dChain < 5) {
			selectResult.mode = SelectMode.CHAIN;
		} else if (dPolygon < 5) {
			selectResult.mode = SelectMode.POLYGON;
		} else {
			selectResult.mode = SelectMode.NONE;
		}
		return selectResult;
	}

	private void deleteNodeFromChain(Chain chain, Node node,
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
		Chain shell = polygon.getShell();
		deleteNodeFromChain(shell, node, false);
	}

	private DragInfo dragInfo = null;
	private RotateInfo rotateInfo = null;
	private ScaleInfo scaleInfo = null;

	private Map<Node, Coordinate> originalPositions = new HashMap<Node, Coordinate>();

	@Override
	public void mousePressed(MouseEvent e)
	{
		super.mousePressed(e);

		Coordinate coord = getCoordinate(e);

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				boolean shift = e.isShiftDown();
				selectObject(coord, shift);
				activateNodeForMove(coord);
				dragInfo = new DragInfo(getX(e), getY(e));
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				selectNothing();
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.EDIT) {
			boolean changed = editPane.setProspectNode(null);
			changed |= editPane.setProspectLine(null);
			if (changed) {
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.SELECT_RECTANGULAR) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Rectangle rectangle = new Rectangle(getX(e), getY(e), getX(e),
						getY(e));
				editPane.setSelectionRectangle(rectangle);
			}
		} else if (editPane.getMouseMode() == MouseMode.ROTATE) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Coordinate center = centerOfSelectedObjects();
				rotateInfo = new RotateInfo(getX(e), getY(e), center.getX(),
						center.getY());
			}
		} else if (editPane.getMouseMode() == MouseMode.SCALE) {
			if (e.getButton() == MouseEvent.BUTTON1) {

				Rectangle r = editPane.getSelectedObjectsRectangle();
				EightHandles eightHandles = new EightHandles(r);
				Position position = eightHandles
						.get(coord.getX(), coord.getY());
				if (position != null) {
					scaleInfo = new ScaleInfo(getX(e), getY(e), position, r);
					for (Node node : editPane.getSelectedNodes()) {
						originalPositions.put(node, node.getCoordinate());
					}
				}
			}
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
		} else if (editPane.getMouseMode() == MouseMode.SELECT_RECTANGULAR) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Rectangle rectangle = editPane.getSelectionRectangle();
				boolean shift = e.isShiftDown();
				boolean alt = e.isAltDown();
				selectObjects(rectangle, shift, alt);

				editPane.setSelectionRectangle(null);
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.SCALE) {
			scaleInfo = null;
			originalPositions.clear();
		}
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

	/*
	 * movement of nodes
	 */

	private Node currentMoveNode = null;

	private void activateNodeForMove(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		if (node != null) {
			if (node.getCoordinate().distance(coord) < 5) {
				currentMoveNode = node;
			}
		}
		editPane.getContent().fireContentChanged();
	}

	private static Map<Position, Integer> resizeCursors = new HashMap<Position, Integer>();
	static {
		resizeCursors.put(Position.N, Cursor.N_RESIZE_CURSOR);
		resizeCursors.put(Position.NE, Cursor.NE_RESIZE_CURSOR);
		resizeCursors.put(Position.E, Cursor.E_RESIZE_CURSOR);
		resizeCursors.put(Position.SE, Cursor.SE_RESIZE_CURSOR);
		resizeCursors.put(Position.S, Cursor.S_RESIZE_CURSOR);
		resizeCursors.put(Position.SW, Cursor.SW_RESIZE_CURSOR);
		resizeCursors.put(Position.W, Cursor.W_RESIZE_CURSOR);
		resizeCursors.put(Position.NW, Cursor.NW_RESIZE_CURSOR);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE
				|| editPane.getMouseMode() == MouseMode.DELETE) {
			Node node = editPane.getContent().getNearestNode(coord);
			Chain chain = editPane.getContent().getNearestChain(coord);
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
				changed = editPane.setMouseHighlight(node);
			} else if (dChain < 5) {
				changed = editPane.setMouseHighlight(chain);
			} else if (dPolygon < 5) {
				changed = editPane.setMouseHighlight(polygon);
			} else {
				changed |= editPane.setMouseHighlight((Node) null);
				changed |= editPane.setMouseHighlight((Chain) null);
				changed |= editPane.setMouseHighlight((Polygon) null);
			}
			if (changed) {
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.EDIT) {
			AddPointResult addPointResult = selectAddPointMode();
			boolean changed = false;
			switch (addPointResult.addPointMode) {
			default:
			case NONE:
				changed |= editPane.setProspectNode(null);
				changed |= editPane.setProspectLine(null);
				break;
			case NEW:
				editPane.setProspectNode(new Node(coord));
				changed = true;
				break;
			case PREPEND:
				Coordinate start = addPointResult.chain.getFirstNode()
						.getCoordinate();
				editPane.setProspectNode(new Node(coord));
				editPane.setProspectLine(new Line(start, coord));
				changed = true;
				break;
			case APPEND:
				start = addPointResult.chain.getLastNode().getCoordinate();
				editPane.setProspectNode(new Node(coord));
				editPane.setProspectLine(new Line(start, coord));
				changed = true;
				break;
			case NEW_WITH_SELECTED:
				editPane.setProspectNode(new Node(coord));
				start = addPointResult.node.getCoordinate();
				editPane.setProspectLine(new Line(start, coord));
				changed = true;
				break;
			}
			if (changed) {
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.SCALE) {
			Rectangle r = editPane.getSelectedObjectsRectangle();
			EightHandles eightHandles = new EightHandles(r);
			Position position = eightHandles.get(coord.getX(), coord.getY());
			Cursor cursor = null;
			if (position == null) {
				cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
			} else {
				cursor = Cursor
						.getPredefinedCursor(resizeCursors.get(position));
			}
			editPane.setCursor(cursor);
		}
	}

	private Node snapNode = null;

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			if (editPane.onlyOneNodeSelected() && currentMoveNode != null) {
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
			} else {
				dragInfo.update(getX(e), getY(e));
				Coordinate delta = dragInfo.getDeltaToLast();
				translateSelectedObjects(delta);
				editPane.getContent().fireContentChanged();
			}
		} else if (editPane.getMouseMode() == MouseMode.SELECT_RECTANGULAR) {
			if (editPane.getSelectionRectangle() != null) {
				editPane.getSelectionRectangle().setX2(getX(e));
				editPane.getSelectionRectangle().setY2(getY(e));
				editPane.repaint();
			}
		} else if (editPane.getMouseMode() == MouseMode.ROTATE) {
			if (editPane.somethingSelected()) {
				rotateInfo.update(getX(e), getY(e));
				double alpha = rotateInfo.getAngleToLast();
				logger.debug("rotate by : " + alpha);
				logger.debug("rotate around  : " + rotateInfo.getCenter());
				rotateSelectedObjects(rotateInfo.getCenter(), alpha);
				editPane.getContent().fireContentChanged();
			}
		} else if (editPane.getMouseMode() == MouseMode.SCALE) {
			if (scaleInfo != null) {
				scaleInfo.update(getX(e), getY(e));
				Coordinate delta = scaleInfo.getDeltaToStart();
				scaleSelectedObjects(delta, scaleInfo.getPosition());
				editPane.getContent().fireContentChanged();
			}
		}
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

	private void rotateSelectedObjects(Coordinate center, double alpha)
	{
		Set<Node> toRotate = editPane.getSelectedNodes();

		double sin = Math.sin(alpha);
		double cos = Math.cos(alpha);

		Vector vc = new Vector(center);
		for (Node node : toRotate) {
			Coordinate old = node.getCoordinate();
			Vector v = new Vector(old);
			Vector t = v.sub(vc);
			double x = cos * t.getX() - sin * t.getY();
			double y = sin * t.getX() + cos * t.getY();
			Vector rotated = new Vector(x, y);
			Vector r = rotated.add(vc);
			node.setCoordinate(new Coordinate(r.getX(), r.getY()));
		}
	}

	private void scaleSelectedObjects(Coordinate delta, Position position)
	{
		Set<Node> nodes = editPane.getSelectedNodes();
		Rectangle rect = scaleInfo.getRectangle();
		double top = rect.getY1();
		double btm = rect.getY2();
		double lft = rect.getX1();
		double rgt = rect.getX2();
		for (Node node : nodes) {
			Coordinate old = originalPositions.get(node);
			Vector v = new Vector(old);
			Vector r = new Vector(old);
			switch (position) {
			default:
				break;
			case N:
			case NW:
			case NE: {
				double scaleY = (top + delta.getY() - btm) / (top - btm);
				double ry = btm + (v.getY() - btm) * scaleY;
				r = new Vector(r.getX(), ry);
				break;
			}
			case S:
			case SW:
			case SE: {
				double scaleY = (btm + delta.getY() - top) / (btm - top);
				double ry = top + (v.getY() - top) * scaleY;
				r = new Vector(r.getX(), ry);
				break;
			}
			}
			switch (position) {
			default:
				break;
			case W:
			case NW:
			case SW: {
				double scaleX = (lft + delta.getX() - rgt) / (lft - rgt);
				double rx = rgt + (v.getX() - rgt) * scaleX;
				r = new Vector(rx, r.getY());
				break;
			}
			case E:
			case NE:
			case SE: {
				double scaleX = (rgt + delta.getX() - lft) / (rgt - lft);
				double rx = lft + (v.getX() - lft) * scaleX;
				r = new Vector(rx, r.getY());
				break;
			}
			}
			if (r != null) {
				node.setCoordinate(new Coordinate(r.getX(), r.getY()));
			}
		}
	}

	private Coordinate centerOfSelectedObjects()
	{
		Set<Node> nodes = editPane.getSelectedNodes();

		double x = 0, y = 0;
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			x += c.getX();
			y += c.getY();
		}
		x /= nodes.size();
		y /= nodes.size();
		return new Coordinate(x, y);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if (editPane.getMouseMode() == MouseMode.EDIT) {
			boolean changed = editPane.setProspectNode(null);
			changed |= editPane.setProspectLine(null);
			if (changed) {
				editPane.repaint();
			}
		}
	}
}
