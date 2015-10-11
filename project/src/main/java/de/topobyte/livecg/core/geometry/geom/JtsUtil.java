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
package de.topobyte.livecg.core.geometry.geom;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

import de.topobyte.livecg.ui.geometryeditor.SetOfGeometries;

public class JtsUtil
{

	private GeometryFactory factory = new GeometryFactory();

	public com.vividsolutions.jts.geom.Coordinate toJts(Coordinate c)
	{
		return new com.vividsolutions.jts.geom.Coordinate(c.getX(), c.getY());
	}

	public Polygon fromJts(com.vividsolutions.jts.geom.Polygon polygon)
	{
		LineString jShell = polygon.getExteriorRing();
		Chain shell = fromJtsString(jShell);

		List<Chain> holes = new ArrayList<Chain>();
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			holes.add(fromJtsString(polygon.getInteriorRingN(i)));
		}

		return new Polygon(shell, holes);
	}

	public com.vividsolutions.jts.geom.Polygon toJts(Polygon polygon)
	{
		Chain shell = polygon.getShell();
		LinearRing jShell = toJtsRing(shell);

		LinearRing[] jHoles = new LinearRing[polygon.getHoles().size()];
		for (int i = 0; i < polygon.getHoles().size(); i++) {
			Chain hole = polygon.getHoles().get(i);
			jHoles[i] = toJtsRing(hole);
		}

		return factory.createPolygon(jShell, jHoles);
	}

	public Chain fromJtsString(LineString string)
	{
		Chain chain = new Chain();
		int num = string.getNumPoints();
		boolean isRing = string instanceof LinearRing;
		if (isRing) {
			// Ignore last coordinate which is the same as the first
			// for rings in JTS
			num -= 1;
		}
		for (int i = 0; i < num; i++) {
			com.vividsolutions.jts.geom.Coordinate c = string.getCoordinateN(i);
			chain.appendPoint(new Coordinate(c.x, c.y));
		}
		if (isRing) {
			try {
				chain.setClosed(true);
			} catch (CloseabilityException e) {
				// Impossible if the input is a valid ring
			}
		}
		return chain;
	}

	public LineString toJtsString(Chain chain)
	{
		int n = chain.getNumberOfNodes();
		if (chain.isClosed()) {
			// Repeat first coordinate if we create a ring
			n += 1;
		}
		com.vividsolutions.jts.geom.Coordinate[] coords = new com.vividsolutions.jts.geom.Coordinate[n];
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			coords[i] = new com.vividsolutions.jts.geom.Coordinate(c.getX(),
					c.getY());
		}
		if (chain.isClosed()) {
			Coordinate c = chain.getCoordinate(0);
			coords[chain.getNumberOfNodes()] = new com.vividsolutions.jts.geom.Coordinate(
					c.getX(), c.getY());
		}
		if (chain.isClosed()) {
			return factory.createLinearRing(coords);
		}
		return factory.createLineString(coords);
	}

	public LinearRing toJtsRing(Chain chain)
	{
		com.vividsolutions.jts.geom.Coordinate[] coords = new com.vividsolutions.jts.geom.Coordinate[chain
				.getNumberOfNodes() + 1];
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			coords[i] = new com.vividsolutions.jts.geom.Coordinate(c.getX(),
					c.getY());
		}
		Coordinate c = chain.getCoordinate(0);
		coords[chain.getNumberOfNodes()] = new com.vividsolutions.jts.geom.Coordinate(
				c.getX(), c.getY());
		return factory.createLinearRing(coords);
	}

	public GeometryCollection toJts(SetOfGeometries geometries)
	{
		List<Geometry> geoms = new ArrayList<Geometry>();
		for (Polygon polygon : geometries.getPolygons()) {
			geoms.add(toJts(polygon));
		}
		for (Chain chain : geometries.getChains()) {
			if (chain.getNumberOfNodes() == 1) {
				geoms.add(factory.createPoint(toJts(chain.getCoordinate(0))));
			} else if (chain.getNumberOfNodes() > 1) {
				geoms.add(toJtsString(chain));
			}
		}
		return factory.createGeometryCollection(geoms.toArray(new Geometry[0]));
	}
}
