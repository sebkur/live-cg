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

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.util.Segment;
import de.topobyte.livecg.core.geometry.util.SegmentIterable;
import de.topobyte.livecg.core.ui.geometryeditor.Content;

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
	private Map<Node, Vertex> nodeToVertex = new HashMap<Node, Vertex>();

	private DcelConverter(Content content)
	{
		this.content = content;

	}

	private void convert()
	{
		List<Chain> chains = content.getChains();
		for (Chain chain : chains) {
			createVertices(chain);
		}
		for (Chain chain : chains) {
			createHalfEdges(chain);
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
		for (Segment segment : new SegmentIterable(chain)) {
			Vertex v1 = nodeToVertex.get(segment.getNode1());
			Vertex v2 = nodeToVertex.get(segment.getNode2());
			HalfEdge a = new HalfEdge(v1, null, null, null, null);
			HalfEdge b = new HalfEdge(v2, null, null, null, null);
			a.setTwin(b);
			b.setTwin(a);
			dcel.halfedges.add(a);
			dcel.halfedges.add(b);
		}
	}

}
