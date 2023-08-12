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
package de.topobyte.livecg.algorithms.convexhull;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.viewports.geometry.Coordinate;

public class ConvexHullOperation
{

	public static Polygon compute(List<Node> nodes, List<Chain> chains,
			List<Polygon> polygons)
	{
		List<org.locationtech.jts.geom.Coordinate> coordinates = new ArrayList<>();
		if (nodes != null) {
			for (Node node : nodes) {
				org.locationtech.jts.geom.Coordinate c = convert(
						node.getCoordinate());
				add(coordinates, c);
			}
		}
		if (chains != null) {
			for (Chain chain : chains) {
				add(chain, coordinates);
			}
		}
		if (polygons != null) {
			for (Polygon polygon : polygons) {
				add(polygon.getShell(), coordinates);
				for (Chain hole : polygon.getHoles()) {
					add(hole, coordinates);
				}
			}
		}

		org.locationtech.jts.geom.Coordinate[] coords = coordinates
				.toArray(new org.locationtech.jts.geom.Coordinate[0]);
		ConvexHull convexHull = new ConvexHull(coords, new GeometryFactory());
		Geometry hull = convexHull.getConvexHull();

		if (!(hull instanceof org.locationtech.jts.geom.Polygon)) {
			System.out.println("not a polygon");
			System.out.println(hull);
			return null;
		}

		org.locationtech.jts.geom.Polygon hullPolygon = (org.locationtech.jts.geom.Polygon) hull;
		org.locationtech.jts.geom.Coordinate[] hullPoints = hullPolygon
				.getCoordinates();

		Chain chain = new Chain();
		for (int i = 0; i < hullPoints.length - 1; i++) {
			org.locationtech.jts.geom.Coordinate c = hullPoints[i];
			chain.appendPoint(new Coordinate(c.x, c.y));
		}

		try {
			chain.setClosed(true);
		} catch (CloseabilityException e1) {
			System.out.println("unable to close");
		}

		Polygon polygon = new Polygon(chain, null);
		return polygon;
	}

	private static org.locationtech.jts.geom.Coordinate convert(Coordinate c)
	{
		return new org.locationtech.jts.geom.Coordinate(c.getX(), c.getY());
	}

	private static void add(Chain chain,
			List<org.locationtech.jts.geom.Coordinate> coordinates)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			add(coordinates, convert(chain.getCoordinate(i)));
		}
	}

	private static void add(
			List<org.locationtech.jts.geom.Coordinate> coordinates,
			org.locationtech.jts.geom.Coordinate c)
	{
		// Do not add equal coordinates twice because JTS gets a hiccup in that
		// case
		for (org.locationtech.jts.geom.Coordinate o : coordinates) {
			if (o.x == c.x && o.y == c.y) {
				return;
			}
		}
		coordinates.add(c);
	}

}
