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

import java.awt.event.MouseEvent;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.geometryeditor.geometryeditor.Content;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class MouseListenerRectangularSelection extends EditPaneMouseListener
{

	public MouseListenerRectangularSelection(GeometryEditPane editPane)
	{
		super(editPane);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) {
			Rectangle rectangle = new Rectangle(getX(e), getY(e), getX(e),
					getY(e));
			editPane.setSelectionRectangle(rectangle);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) {
			Rectangle rectangle = editPane.getSelectionRectangle();
			boolean shift = e.isShiftDown();
			boolean alt = e.isAltDown();
			selectObjects(rectangle, shift, alt);

			editPane.setSelectionRectangle(null);
			editPane.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (editPane.getSelectionRectangle() != null) {
			editPane.getSelectionRectangle().setX2(getX(e));
			editPane.getSelectionRectangle().setY2(getY(e));
			editPane.repaint();
		}
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
}
