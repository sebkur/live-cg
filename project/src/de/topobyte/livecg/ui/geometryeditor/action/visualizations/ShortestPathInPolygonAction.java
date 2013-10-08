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

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.ConvexHullOperation;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.geom.farthest.FarthestPairResult;
import de.topobyte.livecg.geometry.geom.farthest.ShamosFarthestPairOperation;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.action.BasicAction;
import de.topobyte.polygon.shortestpath.ShortestPathAlgorithm;
import de.topobyte.polygon.shortestpath.ShortestPathDialog;

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
		Chain shell = polygon.getShell();

		List<Polygon> list = new ArrayList<Polygon>();
		list.add(polygon);
		Polygon convexHull = ConvexHullOperation.compute(null, null, list);
		FarthestPairResult farthestPair = ShamosFarthestPairOperation
				.compute(convexHull.getShell());
		Coordinate c0 = convexHull.getShell()
				.getCoordinate(farthestPair.getI());
		Coordinate c1 = convexHull.getShell()
				.getCoordinate(farthestPair.getJ());
		Node start = findNearest(shell, c0);
		Node target = findNearest(shell, c1);

		ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm(polygon,
				start, target);
		new ShortestPathDialog(algorithm);
	}

	private Node findNearest(Chain shell, Coordinate c)
	{
		Node nearest = null;
		double min = Double.MAX_VALUE;
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			double d = node.getCoordinate().distance(c);
			if (d < min) {
				min = d;
				nearest = node;
			}
		}
		return nearest;
	}
}
