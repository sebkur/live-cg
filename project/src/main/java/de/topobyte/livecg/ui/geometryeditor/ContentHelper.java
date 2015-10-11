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
package de.topobyte.livecg.ui.geometryeditor;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class ContentHelper
{
	public static List<Node> collectNodes(Content content)
	{
		List<Node> nodes = new ArrayList<Node>();

		for (Chain chain : content.getChains()) {
			collectNodes(nodes, chain);
		}
		for (Polygon polygon : content.getPolygons()) {
			collectNodes(nodes, polygon.getShell());
			for (Chain hole : polygon.getHoles()) {
				collectNodes(nodes, hole);
			}
		}

		return nodes;
	}

	private static void collectNodes(List<Node> nodes, Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			if (!nodes.contains(node)) {
				nodes.add(node);
			}
		}
	}
}
