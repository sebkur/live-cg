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

package de.topobyte.livecg.geometry.ui.geom;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class Editable {

	private List<Coordinate> coordinates = new ArrayList<Coordinate>();

	public void addPoint(Coordinate coordinate) {
		coordinates.add(coordinate);
	}

	public int getNumberOfCoordinates() {
		return coordinates.size();
	}

	public Coordinate getCoordinate(int i) {
		return coordinates.get(i);
	}

	public void setCoordinate(int i, Coordinate coordinate) {
		coordinates.set(i, coordinate);
	}

	public Coordinate getFirstCoordinate() {
		return coordinates.get(0);
	}

	public Coordinate getLastCoordinate() {
		return coordinates.get(coordinates.size() - 1);
	}

	public void removeLastPoint() {
		coordinates.remove(coordinates.size() - 1);
	}

	public void changeCoordinate(int index, Coordinate c) {
		coordinates.set(index, c);
	}

	public Geometry createGeometry() {
		int n = coordinates.size();

		GeometryFactory factory = new GeometryFactory();
		if (n == 0) {
			Point point = factory.createPoint(coordinates.get(0)
					.createCoordinate());
			return point;
		}
		com.vividsolutions.jts.geom.Coordinate[] coords = new com.vividsolutions.jts.geom.Coordinate[n];
		for (int i = 0; i < n; i++) {
			coords[i] = coordinates.get(i).createCoordinate();
		}
		LineString line = factory.createLineString(coords);
		return line;
	}

	public boolean hasPointWithinThreshold(Coordinate coordinate,
			double threshold) {
		for (Coordinate c : coordinates) {
			if (coordinate.distance(c) < threshold) {
				return true;
			}
		}
		return false;
	}

	public int getNearestPointWithinThreshold(Coordinate coordinate,
			double threshold) {
		int p = -1;
		double distance = 0;
		for (int i = 0; i < coordinates.size(); i++) {
			Coordinate c = coordinates.get(i);
			double cdist = coordinate.distance(c);
			if (cdist < threshold) {
				if (p == -1 || cdist < distance) {
					p = i;
					distance = cdist;
				}
			}
		}
		return p;
	}

	public static Editable fromLineString(Geometry geometry) {
		if (!(geometry instanceof LineString)) {
			return null;
		}
		Editable editable = new Editable();
		LineString string = (LineString) geometry;
		for (int i = 0; i < string.getNumPoints(); i++) {
			com.vividsolutions.jts.geom.Coordinate cn = string
					.getCoordinateN(i);
			editable.addPoint(new Coordinate(cn.x, cn.y));
		}
		return editable;
	}
}
