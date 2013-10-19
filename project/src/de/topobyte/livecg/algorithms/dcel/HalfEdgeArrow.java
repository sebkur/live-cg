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
import de.topobyte.livecg.core.lina2.Vector;

public class HalfEdgeArrow
{

	private Vector ao, ad, am;

	public HalfEdgeArrow(HalfEdge halfedge)
	{
		HalfEdge twin = halfedge.getTwin();
		Vertex origin = halfedge.getOrigin();
		Vertex destination = twin.getOrigin();
		Coordinate co = origin.getCoordinate();
		Coordinate cd = destination.getCoordinate();

		double gap = 5;
		double shorten = 10;
		double length = 15;
		double alpha = Math.PI / 8;
		double lsa = length * Math.sin(alpha);
		double lca = length * Math.cos(alpha);

		Vector vo = new Vector(co);
		Vector vd = new Vector(cd);
		Vector he = new Vector(co, cd).normalized();
		Vector ppd = he.perpendicular().normalized();

		ao = vo.add(ppd.mult(gap)).add(he.mult(shorten));
		ad = vd.add(ppd.mult(gap)).sub(he.mult(shorten));

		am = ad.add(ppd.mult(lsa)).sub(he.mult(lca));
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
