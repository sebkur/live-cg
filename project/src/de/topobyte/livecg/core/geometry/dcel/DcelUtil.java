/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.core.geometry.dcel;

public class DcelUtil
{
	public static HalfEdge createEdge(DCEL dcel, Vertex v1, Vertex v2,
			boolean connectNextPrev, boolean addVerticesToDcel)
	{
		HalfEdge a = new HalfEdge(v1, null, null, null, null);
		HalfEdge b = new HalfEdge(v2, null, null, null, null);

		a.setTwin(b);
		b.setTwin(a);
		if (connectNextPrev) {
			a.setNext(b);
			a.setPrev(b);
			b.setNext(a);
			b.setPrev(a);
		}
		if (addVerticesToDcel) {
			dcel.getVertices().add(v1);
			dcel.getVertices().add(v2);
		}
		dcel.getHalfedges().add(a);
		dcel.getHalfedges().add(b);

		return a;
	}
}
