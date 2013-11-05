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
import java.util.Map;
import java.util.Set;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometryeditor.geometryeditor.rectangle.EightHandles;
import de.topobyte.livecg.geometryeditor.geometryeditor.rectangle.Position;

public class MouseListenerScale extends EditPaneMouseListener
{

	private static final double MOUSE_TOLERANCE_HANDLES = 12;

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

	private ScaleInfo scaleInfo = null;

	private Map<Node, Coordinate> originalPositions = new HashMap<Node, Coordinate>();

	public MouseListenerScale(GeometryEditPane editPane)
	{
		super(editPane);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);
		if (e.getButton() == MouseEvent.BUTTON1) {
			Rectangle r = editPane.getSelectedObjectsRectangle();
			EightHandles eightHandles = new EightHandles(r,
					MOUSE_TOLERANCE_HANDLES / editPane.getZoom());
			Position position = eightHandles.get(coord.getX(), coord.getY());
			if (position != null) {
				scaleInfo = new ScaleInfo(getX(e), getY(e), position, r);
				for (Node node : editPane.getSelectedNodes()) {
					originalPositions.put(node, node.getCoordinate());
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		scaleInfo = null;
		originalPositions.clear();
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		Coordinate coord = getCoordinate(e);
		Rectangle r = editPane.getSelectedObjectsRectangle();
		EightHandles eightHandles = new EightHandles(r, MOUSE_TOLERANCE_HANDLES
				/ editPane.getZoom());
		Position position = eightHandles.get(coord.getX(), coord.getY());
		Cursor cursor = null;
		if (position == null) {
			cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		} else {
			cursor = Cursor.getPredefinedCursor(resizeCursors.get(position));
		}
		editPane.setCursor(cursor);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (scaleInfo != null) {
			scaleInfo.update(getX(e), getY(e));
			Coordinate delta = scaleInfo.getDeltaToStart();
			scaleSelectedObjects(delta, scaleInfo.getPosition());
			editPane.getContent().fireContentChanged();
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
			Vector2 v = new Vector2(old);
			Vector2 r = new Vector2(old);
			switch (position) {
			default:
				break;
			case N:
			case NW:
			case NE: {
				double scaleY = (top + delta.getY() - btm) / (top - btm);
				double ry = btm + (v.getY() - btm) * scaleY;
				r = new Vector2(r.getX(), ry);
				break;
			}
			case S:
			case SW:
			case SE: {
				double scaleY = (btm + delta.getY() - top) / (btm - top);
				double ry = top + (v.getY() - top) * scaleY;
				r = new Vector2(r.getX(), ry);
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
				r = new Vector2(rx, r.getY());
				break;
			}
			case E:
			case NE:
			case SE: {
				double scaleX = (rgt + delta.getX() - lft) / (rgt - lft);
				double rx = lft + (v.getX() - lft) * scaleX;
				r = new Vector2(rx, r.getY());
				break;
			}
			}
			if (r != null) {
				node.setCoordinate(new Coordinate(r.getX(), r.getY()));
			}
		}
	}

}
