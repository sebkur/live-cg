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
package de.topobyte.livecg.geometryeditor.geometryeditor.clipboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.geometryeditor.geometryeditor.SetOfGeometries;

public class GeometryTransfer
{

	private SetOfGeometries from;
	private SetOfGeometries to;

	private Map<Node, Node> nl = new HashMap<Node, Node>();

	private GeometryTransfer(SetOfGeometries from, SetOfGeometries to)
	{
		this.from = from;
		this.to = to;
	}

	public static void transfer(SetOfGeometries from, SetOfGeometries to)
	{
		GeometryTransfer transfer = new GeometryTransfer(from, to);
		transfer.transfer();
	}

	private void transfer()
	{
		List<Chain> chains = from.getChains();
		for (Chain chain : chains) {
			copyNodes(chain);
		}

		List<Polygon> polygons = from.getPolygons();
		for (Polygon polygon : polygons) {
			copyNodes(polygon);
		}

		for (Chain chain : chains) {
			to.addChain(copyChain(chain));
		}

		for (Polygon polygon : polygons) {
			to.addPolygon(copyPolygon(polygon));
		}
	}

	private void copyNodes(Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			if (nl.containsKey(node)) {
				continue;
			}
			Node copy = new Node(new Coordinate(node.getCoordinate()));
			nl.put(node, copy);
		}
	}

	private void copyNodes(Polygon polygon)
	{
		copyNodes(polygon.getShell());
		for (Chain hole : polygon.getHoles()) {
			copyNodes(hole);
		}
	}

	private Chain copyChain(Chain chain)
	{
		Chain c = new Chain();
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			Node copy = nl.get(node);
			c.appendNode(copy);
		}
		try {
			c.setClosed(chain.isClosed());
		} catch (CloseabilityException e) {
			// Should not happen
		}
		return c;
	}

	private Polygon copyPolygon(Polygon polygon)
	{
		Chain shell = copyChain(polygon.getShell());
		List<Chain> holes = new ArrayList<Chain>();
		for (Chain hole : polygon.getHoles()) {
			holes.add(copyChain(hole));
		}
		return new Polygon(shell, holes);
	}
}
