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
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.CloseabilityException;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.action.BasicAction;

public class ConvexHullAction extends BasicAction
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

		List<com.vividsolutions.jts.geom.Coordinate> coordinates = new ArrayList<com.vividsolutions.jts.geom.Coordinate>();
		for (Node node : nodes) {
			com.vividsolutions.jts.geom.Coordinate c = convert(node
					.getCoordinate());
			add(coordinates, c);
		}
		for (Chain chain : chains) {
			add(chain, coordinates);
		}
		for (Polygon polygon : polygons) {
			add(polygon.getShell(), coordinates);
			for (Chain hole : polygon.getHoles()) {
				add(hole, coordinates);
			}
		}

		com.vividsolutions.jts.geom.Coordinate[] coords = coordinates
				.toArray(new com.vividsolutions.jts.geom.Coordinate[0]);
		ConvexHull convexHull = new ConvexHull(coords, new GeometryFactory());
		Geometry hull = convexHull.getConvexHull();

		if (!(hull instanceof com.vividsolutions.jts.geom.Polygon)) {
			System.out.println("not a polygon");
			System.out.println(hull);
			return;
		}

		com.vividsolutions.jts.geom.Polygon hullPolygon = (com.vividsolutions.jts.geom.Polygon) hull;
		com.vividsolutions.jts.geom.Coordinate[] hullPoints = hullPolygon
				.getCoordinates();

		Chain chain = new Chain();
		for (int i = 0; i < hullPoints.length - 1; i++) {
			com.vividsolutions.jts.geom.Coordinate c = hullPoints[i];
			chain.appendPoint(new Coordinate(c.x, c.y));
		}

		try {
			chain.setClosed(true);
		} catch (CloseabilityException e1) {
			System.out.println("unable to close");
		}

		Polygon polygon = new Polygon(chain, null);
		editPane.getContent().addPolygon(polygon);
		editPane.getContent().fireContentChanged();
	}

	private com.vividsolutions.jts.geom.Coordinate convert(Coordinate c)
	{
		return new com.vividsolutions.jts.geom.Coordinate(c.getX(), c.getY());
	}

	private void add(Chain chain,
			List<com.vividsolutions.jts.geom.Coordinate> coordinates)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			add(coordinates, convert(chain.getCoordinate(i)));
		}
	}

	private void add(List<com.vividsolutions.jts.geom.Coordinate> coordinates,
			com.vividsolutions.jts.geom.Coordinate c)
	{
		// Do not add equal coordinates twice because JTS gets a hiccup in that
		// case
		for (com.vividsolutions.jts.geom.Coordinate o : coordinates) {
			if (o.x == c.x && o.y == c.y) {
				return;
			}
		}
		coordinates.add(c);
	}

}
