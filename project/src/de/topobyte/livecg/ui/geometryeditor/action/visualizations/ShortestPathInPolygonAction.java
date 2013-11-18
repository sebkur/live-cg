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
package de.topobyte.livecg.ui.geometryeditor.action.visualizations;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.algorithms.polygon.shortestpath.PairOfNodes;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathAlgorithm;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathDialog;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathHelper;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class ShortestPathInPolygonAction extends BasicAction
{

	private static final long serialVersionUID = 8237600362605952257L;

	private GeometryEditPane editPane;

	public ShortestPathInPolygonAction(GeometryEditPane editPane)
	{
		super("Shortest Path in Polygon",
				"Visualize the Shortest Path in Polygon Algorithm",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Content content = editPane.getContent();
		List<Polygon> polygons = content.getPolygons();
		List<Polygon> viable = new ArrayList<Polygon>();
		for (Polygon polygon : polygons) {
			if (polygon.getHoles().size() == 0) {
				viable.add(polygon);
			}
		}
		if (viable.size() < 1) {
			System.out.println("no viable polygon");
			return;
		}
		Polygon polygon = viable.get(0);
		polygon = CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING);
		if (!PolygonHelper.isCounterClockwiseOriented(polygon)) {
			Chain shell = polygon.getShell();
			try {
				polygon = new Polygon(ChainHelper.invert(shell), null);
			} catch (CloseabilityException e) {
				// Should not happen
			}
		}

		PairOfNodes nodes = ShortestPathHelper.determineGoodNodes(polygon);
		Node start = nodes.getA();
		Node target = nodes.getB();

		ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm(polygon,
				start, target);
		new ShortestPathDialog(algorithm);
	}

}
