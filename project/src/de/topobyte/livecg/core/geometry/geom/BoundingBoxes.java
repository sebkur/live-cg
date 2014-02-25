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

import java.util.Collection;
import java.util.Iterator;

import de.topobyte.livecg.ui.geometryeditor.SetOfGeometries;

public class BoundingBoxes
{

	public static Rectangle get(SetOfGeometries geoms)
	{
		return Rectangles.union(getForChains(geoms.getChains()),
				getForPolygons(geoms.getPolygons()));
	}

	public static Rectangle get(Chain chain)
	{
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Coordinate c = chain.getCoordinate(i);
			if (c.getX() < xmin) {
				xmin = c.getX();
			}
			if (c.getX() > xmax) {
				xmax = c.getX();
			}
			if (c.getY() < ymin) {
				ymin = c.getY();
			}
			if (c.getY() > ymax) {
				ymax = c.getY();
			}
		}
		return new Rectangle(xmin, ymin, xmax, ymax);
	}

	public static Rectangle get(Polygon polygon)
	{
		return get(polygon.getShell());
	}

	public static Rectangle getForChains(Collection<Chain> chains)
	{
		if (chains.isEmpty()) {
			return null;
		}
		Iterator<Chain> iterator = chains.iterator();
		Rectangle r = get(iterator.next());
		while (iterator.hasNext()) {
			r = Rectangles.union(r, get(iterator.next()));
		}
		return r;
	}

	public static Rectangle getForPolygons(Collection<Polygon> polygons)
	{
		if (polygons.isEmpty()) {
			return null;
		}
		Iterator<Polygon> iterator = polygons.iterator();
		Rectangle r = get(iterator.next());
		while (iterator.hasNext()) {
			r = Rectangles.union(r, get(iterator.next()));
		}
		return r;
	}
}
