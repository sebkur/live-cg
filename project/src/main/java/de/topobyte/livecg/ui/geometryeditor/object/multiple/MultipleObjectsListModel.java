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
package de.topobyte.livecg.ui.geometryeditor.object.multiple;

import java.util.List;
import java.util.Locale;

import javax.swing.AbstractListModel;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.viewports.geometry.Coordinate;

public class MultipleObjectsListModel extends AbstractListModel<String>
{

	private static final long serialVersionUID = 2246583987524119020L;

	private GeometryEditPane editPane;

	public MultipleObjectsListModel(GeometryEditPane editPane)
	{
		this.editPane = editPane;
	}

	@Override
	public int getSize()
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();
		return nodes.size() + chains.size() + polygons.size();
	}

	@Override
	public String getElementAt(int index)
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();
		if (index < nodes.size()) {
			Node node = nodes.get(index);
			Coordinate c = node.getCoordinate();
			return String.format(Locale.US, "node (%.2f, %.2f)", c.getX(),
					c.getY());
		} else if (index < nodes.size() + chains.size()) {
			Chain chain = chains.get(index - nodes.size());
			return String.format("chain (%d nodes)", chain.getNumberOfNodes());
		} else if (index < nodes.size() + chains.size() + polygons.size()) {
			Polygon polygon = polygons
					.get(index - nodes.size() - chains.size());
			return String.format("polygon (%d nodes)", polygon.getShell()
					.getNumberOfNodes());
		}
		return "what";
	}

	public void update()
	{
		fireContentsChanged(this, 0, getSize() - 1);
	}

}
