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

package de.topobyte.livecg.core.ui.geometryeditor.action;

import java.awt.event.ActionEvent;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.ui.geometryeditor.Content;
import de.topobyte.livecg.core.ui.geometryeditor.GeometryEditPane;

public class SelectAllAction extends BasicAction
{

	private static final long serialVersionUID = 1880883173731363076L;

	private final GeometryEditPane editPane;

	public SelectAllAction(GeometryEditPane editPane)
	{
		super("Select everything", "Select all object on the scene",
				"res/images/24x24/empty.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Content content = editPane.getContent();

		editPane.clearCurrentNodes();
		editPane.clearCurrentChains();
		editPane.clearCurrentPolygons();

		List<Chain> chains = content.getChains();
		List<Polygon> polygons = content.getPolygons();

		for (Chain chain : chains) {
			if (chain.getNumberOfNodes() == 1) {
				Node node = chain.getFirstNode();
				editPane.addCurrentNode(node);
			} else {
				editPane.addCurrentChain(chain);
			}
		}

		for (Polygon polygon : polygons) {
			editPane.addCurrentPolygon(polygon);
		}

		editPane.repaint();
	}

}
