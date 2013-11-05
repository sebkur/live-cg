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

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class EditPaneMouseListener extends ViewportMouseListener
{

	protected static final double MOUSE_TOLERANCE_SELECT = 5;

	protected GeometryEditPane editPane;

	public EditPaneMouseListener(GeometryEditPane editPane)
	{
		super(editPane);
		this.editPane = editPane;
	}

	protected SelectResult nearestObject(Coordinate coord)
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
		if (dNode < MOUSE_TOLERANCE_SELECT / editPane.getZoom()) {
			selectResult.mode = SelectMode.NODE;
		} else if (dChain < MOUSE_TOLERANCE_SELECT / editPane.getZoom()) {
			selectResult.mode = SelectMode.CHAIN;
		} else if (dPolygon < MOUSE_TOLERANCE_SELECT / editPane.getZoom()) {
			selectResult.mode = SelectMode.POLYGON;
		} else {
			selectResult.mode = SelectMode.NONE;
		}
		return selectResult;
	}

	protected boolean selectNothing()
	{
		boolean changed = editPane.getCurrentChains().size() > 0
				|| editPane.getCurrentNodes().size() > 0;
		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.clearCurrentPolygons();
		return changed;
	}

	protected void updateHighlights(Coordinate coord)
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
		boolean changed = false;
		if (dNode < MOUSE_TOLERANCE_SELECT / editPane.getZoom()) {
			changed = editPane.setMouseHighlight(node);
		} else if (dChain < MOUSE_TOLERANCE_SELECT / editPane.getZoom()) {
			changed = editPane.setMouseHighlight(chain);
		} else if (dPolygon < MOUSE_TOLERANCE_SELECT / editPane.getZoom()) {
			changed = editPane.setMouseHighlight(polygon);
		} else {
			changed |= editPane.setMouseHighlight((Node) null);
			changed |= editPane.setMouseHighlight((Chain) null);
			changed |= editPane.setMouseHighlight((Polygon) null);
		}
		if (changed) {
			editPane.repaint();
		}
	}
}
