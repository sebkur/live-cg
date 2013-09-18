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
import java.util.Set;

import de.topobyte.livecg.geometry.ui.geom.CloseabilityException;
import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geom.Node;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;

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
			}
		}
	}

	private void addCoordinateOrCreateNewLine(Coordinate coord)
	{
		Editable line = editPane.getCurrentChain();
		if (line == null) {
			line = new Editable();
			editPane.setCurrentChain(line);
			editPane.getContent().addLine(line);
		}
		line.addPoint(coord);
		editPane.getContent().fireContentChanged();
	}

	private boolean selectNothing()
	{
		boolean changed = editPane.setCurrentNode(null);
		changed |= editPane.setCurrentChain(null);
		return changed;
	}

	private void closeCurrentLine() throws CloseabilityException
	{
		Editable line = editPane.getCurrentChain();
		if (line == null) {
			return;
		}
		line.setClosed(true);
		editPane.getContent().fireContentChanged();
	}

	private void selectObject(Coordinate coord)
	{
		Node node = editPane.getContent().getNearestNode(coord);
		Editable editable = editPane.getContent().getNearestChain(coord);
		double dChain = editable.distance(coord);
		double dNode = node.getCoordinate().distance(coord);
		boolean changed = false;
		if (dNode < 5) {
			changed = editPane.setCurrentChain(null);
			changed |= editPane.setCurrentNode(node);
		} else if (dChain < 5) {
			changed = editPane.setCurrentNode(null);
			changed |= editPane.setCurrentChain(editable);
		} else {
			changed = selectNothing();
		}
		if (changed) {
			editPane.repaint();
		}
	}

	private void deleteNearestPoint(Coordinate coord)
	{
		Set<Editable> near = editPane.getContent().getEditablesNear(coord);
		if (near.size() > 0) {
			Editable editable = near.iterator().next();
			int n = editable.getNearestPointWithinThreshold(coord, 4);
			editable.remove(n);
			if (editable.getNumberOfCoordinates() == 0) {
				editPane.getContent().removeLine(editable);
			}
			editPane.getContent().fireContentChanged();
		}
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
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		super.mouseReleased(e);

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			currentMoveNode = null;
		}
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

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			Node node = editPane.getContent().getNearestNode(coord);
			Editable editable = editPane.getContent().getNearestChain(coord);
			double dChain = editable.distance(coord);
			double dNode = node.getCoordinate().distance(coord);
			boolean changed = false;
			if (dNode < 5) {
				changed = editPane.setHighlight(node);
			} else if (dChain < 5) {
				changed = editPane.setHighlight(editable);
			} else {
				changed |= editPane.setHighlight((Node) null);
				changed |= editPane.setHighlight((Editable) null);
			}
			if (changed) {
				editPane.repaint();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		Coordinate coord = new Coordinate(e.getX(), e.getY());

		if (editPane.getMouseMode() == MouseMode.SELECT_MOVE) {
			if (currentMoveNode != null) {
				currentMoveNode.setCoordinate(coord);
				editPane.getContent().fireContentChanged();
			}
		}
	}

}
