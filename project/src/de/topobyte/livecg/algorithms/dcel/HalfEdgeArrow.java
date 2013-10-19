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
package de.topobyte.livecg.algorithms.dcel;

import de.topobyte.livecg.core.geometry.dcel.HalfEdge;
import de.topobyte.livecg.core.geometry.dcel.Vertex;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.lina2.Vector;

public class HalfEdgeArrow
{
	private double gap = 6;
	private double shorten = 5;
	private double length = 12;
	private double alpha = Math.PI / 8;
	private double lsa = length * Math.sin(alpha);
	private double lca = length * Math.cos(alpha);

	private Vector ao, ad, am;

	public HalfEdgeArrow(HalfEdge halfedge)
	{
		Vertex origin = halfedge.getOrigin();
		Vertex destination = halfedge.getTwin().getOrigin();
		Coordinate co = origin.getCoordinate();
		Coordinate cd = destination.getCoordinate();

		HalfEdge prev = halfedge.getPrev();
		Vertex prevOrigin = prev.getOrigin();
		Coordinate cpo = prevOrigin.getCoordinate();

		HalfEdge next = halfedge.getNext();
		Vertex nextDestination = next.getTwin().getOrigin();
		Coordinate cnd = nextDestination.getCoordinate();

		ao = findPoint(cpo, co, cd, co, cd, true);
		ad = findPoint(co, cd, cnd, co, cd, false);

		Vector e = new Vector(co, cd).normalized();
		Vector ppd = e.perpendicular().normalized();
		am = ad.add(ppd.mult(lsa)).sub(e.mult(lca));
	}

	/**
	 * Coordinates c1, c2, c3 form the chain to find a point for.
	 * 
	 * Coordinates co, cd are the coordinates of the segment that the current
	 * arrow is parallel to.
	 * 
	 * @param isOrigin
	 *            tells whether the method call is for a start- or endpoint of
	 *            the arrow
	 */
	private Vector findPoint(Coordinate c1, Coordinate c2, Coordinate c3,
			Coordinate co, Coordinate cd, boolean isOrigin)
	{
		Vector v2 = new Vector(c2);
		Vector e1 = new Vector(c1, c2).normalized();
		Vector e2 = new Vector(c2, c3).normalized();
		Vector e = new Vector(co, cd).normalized();

		double angle = GeomMath.angle(c2, c1, c3);
		if (angle <= Math.PI) { // Acute angle
			// d is the direction in which to shift the point from v2
			Vector d = e1.mult(-1).add(e2).normalized();
			// Find lambda such that lambda * d shifts the target point p from
			// v2 with the property that p has distance 'gap' to e1 and e2
			double dx2 = d.getX() * d.getX();
			double ex2 = e2.getX() * e2.getX();
			double dy2 = d.getY() * d.getY();
			double ey2 = e2.getY() * e2.getY();
			double remaining = 2 * d.getX() * e2.getX() * d.getY() * e2.getY();
			double lambda = gap
					/ Math.sqrt(1 - (dx2 * ex2 + dy2 * ey2 + remaining));
			Vector p = v2.add(d.mult(lambda));
			if (isOrigin) {
				return p.add(e.mult(shorten));
			} else {
				return p.sub(e.mult(shorten));
			}
		} else { // Obtuse angle
			// Just move the target point p an amount 'gap' in the direction
			// perpendicular to the edge
			Vector ppd = e.perpendicular().normalized();
			Vector p = v2.add(ppd.mult(gap));
			if (isOrigin) {
				return p.add(e.mult(shorten));
			} else {
				return p.sub(e.mult(shorten));
			}
		}
	}

	public Vector getOrigin()
	{
		return ao;
	}

	public Vector getDestination()
	{
		return ad;
	}

	public Vector getMarker()
	{
		return am;
	}

}
