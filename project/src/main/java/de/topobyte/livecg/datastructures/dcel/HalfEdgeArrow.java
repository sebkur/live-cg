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
package de.topobyte.livecg.datastructures.dcel;

import de.topobyte.livecg.core.geometry.dcel.HalfEdge;
import de.topobyte.livecg.core.geometry.dcel.Vertex;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.viewports.geometry.Coordinate;

public class HalfEdgeArrow
{
	private double gap;

	private boolean valid;
	private Vector2 ao, ad, am;
	private double length;

	public HalfEdgeArrow(HalfEdge halfedge, double gap, double shorten,
			double markerLen, double alpha)
	{
		this.gap = gap;
		double lsa = markerLen * Math.sin(alpha);
		double lca = markerLen * Math.cos(alpha);

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

		Vector2 e = new Vector2(co, cd).normalized();

		Vector2 arrow = ad.sub(ao);
		length = arrow.norm();
		valid = length >= shorten * 2;

		if (valid) {
			ao = ao.add(e.mult(shorten));
			ad = ad.sub(e.mult(shorten));
			length -= shorten * 2;
		}

		Vector2 ppd = e.perpendicularRight().normalized();
		am = ad.add(ppd.mult(lsa)).sub(e.mult(lca));
	}

	/**
	 * Coordinates c1, c2, c3 form the chain to find a point for.
	 * 
	 * Coordinates co, cd are the coordinates of the segment that the current
	 * arrow is parallel to.
	 * 
	 * @param origin
	 */
	private Vector2 findPoint(Coordinate c1, Coordinate c2, Coordinate c3,
			Coordinate co, Coordinate cd, boolean origin)
	{
		Vector2 v2 = new Vector2(c2);
		Vector2 e1 = new Vector2(c1, c2).normalized();
		Vector2 e2 = new Vector2(c2, c3).normalized();
		Vector2 e = new Vector2(co, cd).normalized();

		double angle = GeomMath.angle(c2, c1, c3);
		if (Math.abs(angle) <= 0.00001) {
			Vector2 d = e1.perpendicularRight().normalized();
			Vector2 p;
			if (origin) {
				p = v2.sub(d.mult(gap));
			} else {
				p = v2.add(d.mult(gap));
			}
			return p;
		} else if (angle <= Math.PI - 0.00001) { // Acute angle
			// d is the direction in which to shift the point from v2
			Vector2 d = e1.mult(-1).add(e2).normalized();
			// Find lambda such that lambda * d shifts the target point p from
			// v2 with the property that p has distance 'gap' to e1 and e2
			double dx2 = d.getX() * d.getX();
			double ex2 = e2.getX() * e2.getX();
			double dy2 = d.getY() * d.getY();
			double ey2 = e2.getY() * e2.getY();
			double dxexdyey = 2 * d.getX() * e2.getX() * d.getY() * e2.getY();
			double sub = (dx2 * ex2 + dy2 * ey2 + dxexdyey);
			double lambda = gap / Math.sqrt(1 - sub);
			Vector2 p = v2.add(d.mult(lambda));
			return p;
		} else { // Obtuse angle
			// Just move the target point p an amount 'gap' in the direction
			// perpendicular to the edge
			Vector2 ppd = e.perpendicularRight().normalized();
			Vector2 p = v2.add(ppd.mult(gap));
			return p;
		}
	}

	public boolean isValid()
	{
		return valid;
	}

	public Vector2 getOrigin()
	{
		return ao;
	}

	public Vector2 getDestination()
	{
		return ad;
	}

	public Vector2 getMarker()
	{
		return am;
	}

	public double getLength()
	{
		return length;
	}

}
