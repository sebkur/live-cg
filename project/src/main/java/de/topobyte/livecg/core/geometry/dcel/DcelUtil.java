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
package de.topobyte.livecg.core.geometry.dcel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.GeometryTransformer;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina.Matrix;

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

	public static Rectangle getBoundingBox(DCEL dcel)
	{
		double xmin = Double.POSITIVE_INFINITY;
		double xmax = Double.NEGATIVE_INFINITY;
		double ymin = Double.POSITIVE_INFINITY;
		double ymax = Double.NEGATIVE_INFINITY;
		List<Vertex> vertices = dcel.getVertices();
		for (Vertex v : vertices) {
			Coordinate c = v.getCoordinate();
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

	public static DCEL clone(DCEL dcel)
	{
		DCEL clone = new DCEL();

		Map<Vertex, Vertex> vs = new HashMap<>();
		Map<HalfEdge, HalfEdge> es = new HashMap<>();
		Map<Face, Face> fs = new HashMap<>();

		// Copy vertices
		for (Vertex v : dcel.getVertices()) {
			Vertex copy = new Vertex(new Coordinate(v.getCoordinate()), null);
			clone.getVertices().add(copy);
			vs.put(v, copy);
		}

		// Copy halfedges
		for (HalfEdge e : dcel.getHalfedges()) {
			HalfEdge copy = new HalfEdge(vs.get(e.getOrigin()), null, null,
					null, null);
			clone.getHalfedges().add(copy);
			es.put(e, copy);
		}

		// Update vertices' incidentEdge pointers
		for (Vertex v : dcel.getVertices()) {
			if (v.getIncidentEdge() != null) {
				vs.get(v).setIncidentEdge(es.get(v.getIncidentEdge()));
			}
		}

		// Copy faces
		for (Face f : dcel.getFaces()) {
			HalfEdge outer = null;
			if (f.getOuterComponent() != null) {
				outer = es.get(f.getOuterComponent());
			}
			Face copy = new Face(outer);
			List<HalfEdge> inner = f.getInnerComponents();
			for (HalfEdge e : inner) {
				copy.getInnerComponents().add(es.get(e));
			}
			clone.getFaces().add(copy);
			fs.put(f, copy);
		}

		// Update halfedges' twin/next/prev/face pointers
		for (HalfEdge e : dcel.getHalfedges()) {
			HalfEdge copy = es.get(e);
			if (e.getTwin() != null) {
				copy.setTwin(es.get(e.getTwin()));
			}
			if (e.getPrev() != null) {
				copy.setPrev(es.get(e.getPrev()));
			}
			if (e.getNext() != null) {
				copy.setNext(es.get(e.getNext()));
			}
			if (e.getFace() != null) {
				copy.setFace(fs.get(e.getFace()));
			}
		}

		return clone;
	}

	public static void transform(DCEL dcel, Matrix matrix)
	{
		GeometryTransformer transformer = new GeometryTransformer(matrix);
		List<Vertex> vertices = dcel.getVertices();
		for (Vertex v : vertices) {
			v.setCoordinate(transformer.transform(v.getCoordinate()));
		}
	}
}
