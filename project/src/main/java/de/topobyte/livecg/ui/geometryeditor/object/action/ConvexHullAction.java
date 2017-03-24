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
package de.topobyte.livecg.ui.geometryeditor.object.action;

import java.awt.event.ActionEvent;
import java.util.List;

import de.topobyte.livecg.algorithms.convexhull.ConvexHullOperation;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.util.action.SimpleAction;

public class ConvexHullAction extends SimpleAction
{

	private static final long serialVersionUID = -848141353251525764L;

	private GeometryEditPane editPane;

	public ConvexHullAction(GeometryEditPane editPane)
	{
		super("Convex Hull",
				"Add convex hull of selected objects to the scene",
				"res/images/24x24/convexhull.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();

		Polygon polygon = ConvexHullOperation.compute(nodes, chains, polygons);

		editPane.getContent().addPolygon(polygon);
		editPane.getContent().fireContentChanged();
	}

}
