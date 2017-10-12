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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.util.Segment;
import de.topobyte.livecg.core.geometry.util.SegmentIterable;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.util.circular.IntRing;
import de.topobyte.livecg.util.datasorting.ObjectWithDouble;
import de.topobyte.viewports.geometry.Coordinate;

public class DcelConverter
{

	public static DCEL convert(Content content)
	{
		DcelConverter converter = new DcelConverter(content);

		converter.convert();

		return converter.dcel;
	}

	private Content content;
	private DCEL dcel = new DCEL();
	private Map<Node, Vertex> nodeToVertex = new HashMap<>();
	private Map<Vertex, List<HalfEdge>> vertexToOutgoingHalfedges = new HashMap<>();

	private DcelConverter(Content content)
	{
		this.content = content;

	}

	private void convert()
	{
		List<Chain> chains = content.getChains();
		List<Polygon> polygons = content.getPolygons();

		// Create vertices for chains and polygons

		for (Chain chain : chains) {
			createVertices(chain);
		}
		for (Polygon polygon : polygons) {
			Chain shell = polygon.getShell();
			createVertices(shell);
			for (int i = 0; i < polygon.getHoles().size(); i++) {
				Chain hole = polygon.getHoles().get(i);
				createVertices(hole);
			}
		}

		// Create halfedges for chains and polygons

		for (Chain chain : chains) {
			createHalfEdges(chain);
		}
		for (Polygon polygon : polygons) {
			Chain shell = polygon.getShell();
			createHalfEdges(shell);
			for (int i = 0; i < polygon.getHoles().size(); i++) {
				Chain hole = polygon.getHoles().get(i);
				createHalfEdges(hole);
			}
		}

		// Link halfedges (create next / previous pointers)

		for (Vertex v : vertexToOutgoingHalfedges.keySet()) {
			List<HalfEdge> halfEdges = vertexToOutgoingHalfedges.get(v);
			linkHalfedges(halfEdges);
		}
	}

	private void createVertices(Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			if (nodeToVertex.containsKey(node)) {
				continue;
			}
			Vertex vertex = new Vertex(node.getCoordinate(), null);
			nodeToVertex.put(node, vertex);
			dcel.vertices.add(vertex);
		}
	}

	private void createHalfEdges(Chain chain)
	{
		segments: for (Segment segment : new SegmentIterable(chain)) {
			Vertex v1 = nodeToVertex.get(segment.getNode1());
			Vertex v2 = nodeToVertex.get(segment.getNode2());

			// Check for duplicate connections between nodes to avoid creation
			// of duplicate halfedges
			List<HalfEdge> halfEdges = vertexToOutgoingHalfedges.get(v1);
			if (halfEdges != null) {
				for (HalfEdge e : halfEdges) {
					if (e.getTwin().getOrigin() == v2) {
						continue segments;
					}
				}
			}

			HalfEdge a = new HalfEdge(v1, null, null, null, null);
			HalfEdge b = new HalfEdge(v2, null, null, null, null);
			a.setTwin(b);
			b.setTwin(a);
			dcel.halfedges.add(a);
			dcel.halfedges.add(b);
			put(v1, a);
			put(v2, b);
		}
	}

	private void put(Vertex v, HalfEdge he)
	{
		List<HalfEdge> halfEdges = vertexToOutgoingHalfedges.get(v);
		if (halfEdges == null) {
			halfEdges = new ArrayList<>();
			vertexToOutgoingHalfedges.put(v, halfEdges);
		}
		halfEdges.add(he);
	}

	/*
	 * This method looks at one vertex of the DCEL and its outgoing halfedges.
	 * It sorts those halfedges according to their angle to a straight line and
	 * thereby iterates the halfedges in circular order afterwards. When
	 * traversed in this order we can add the next() and previous() pointers of
	 * the halfedges.
	 */
	private void linkHalfedges(List<HalfEdge> halfEdges)
	{
		if (halfEdges.size() == 0) {
			return;
		} else if (halfEdges.size() == 1) {
			HalfEdge e = halfEdges.get(0);
			e.setPrev(e.getTwin());
			e.getTwin().setNext(e);
		} else if (halfEdges.size() == 2) {
			HalfEdge e1 = halfEdges.get(0);
			HalfEdge e2 = halfEdges.get(1);
			e1.getTwin().setNext(e2);
			e2.getTwin().setNext(e1);
			e2.setPrev(e1.getTwin());
			e1.setPrev(e2.getTwin());
		} else {
			List<ObjectWithDouble<HalfEdge>> objects = new ArrayList<>();
			for (HalfEdge e : halfEdges) {
				Coordinate c = e.getOrigin().getCoordinate();
				Coordinate cSuc = e.getTwin().getOrigin().getCoordinate();
				Coordinate cPre = new Coordinate(c.getX(), c.getY() + 100);
				double angle = GeomMath.angle(c, cPre, cSuc);
				ObjectWithDouble<HalfEdge> object = new ObjectWithDouble<>(e,
						angle);
				objects.add(object);
			}
			Collections.sort(objects);
			IntRing ring = new IntRing(objects.size());
			for (int i = 0; i < objects.size(); i++) {
				int j = ring.next().value();
				ObjectWithDouble<HalfEdge> oi = objects.get(i);
				ObjectWithDouble<HalfEdge> oj = objects.get(j);
				HalfEdge e1 = oi.getObject();
				HalfEdge e2 = oj.getObject();
				e1.getTwin().setNext(e2);
				e2.setPrev(e1.getTwin());
			}
		}
	}
}
