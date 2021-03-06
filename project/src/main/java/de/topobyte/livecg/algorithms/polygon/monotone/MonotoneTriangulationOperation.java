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
package de.topobyte.livecg.algorithms.polygon.monotone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.Stack;

public class MonotoneTriangulationOperation
{

	final static Logger logger = LoggerFactory
			.getLogger(MonotoneTriangulationOperation.class);

	private Polygon polygon;

	private Info info;
	private Stack<Node> stack = new Stack<>();
	private List<Diagonal> diagonals = new ArrayList<>();

	public MonotoneTriangulationOperation(Polygon polygon)
	{
		this.polygon = polygon;

		info = new Info(polygon);
		info.prepare();

		// Triangulate
		triangulate();
	}

	public List<Diagonal> getDiagonals()
	{
		return diagonals;
	}

	private void triangulate()
	{
		List<Node> nodes = info.nodes;
		Map<Node, Side> side = info.side;

		logger.debug("Triangulating");
		Node u1 = nodes.get(0);
		Node u2 = nodes.get(1);
		stack.push(u1);
		stack.push(u2);

		for (int j = 2; j < nodes.size() - 1; j++) {
			Node uj = nodes.get(j);
			if (side.get(stack.top()) != side.get(uj)) {
				Node first = stack.pop();
				addDiagonal(uj, first);
				while (stack.size() > 1) {
					Node popped = stack.pop();
					addDiagonal(uj, popped);
				}
				stack.pop();
				stack.push(first);
				stack.push(uj);
			} else {
				Node last = stack.pop();
				while (stack.size() > 0) {
					Node top = stack.top();
					if (!Util.canAdd(side.get(uj), uj, top, last)) {
						break;
					} else {
						stack.pop();
						addDiagonal(uj, top);
						last = top;
					}
				}
				stack.push(last);
				stack.push(uj);
			}
		}

		if (stack.size() > 2) {
			stack.pop();
			Node un = nodes.get(nodes.size() - 1);
			while (stack.size() > 1) {
				Node popped = stack.pop();
				addDiagonal(un, popped);
			}
		}
	}

	private void addDiagonal(Node a, Node b)
	{
		logger.debug("Adding diagonal: " + (Util.findIndex(polygon, a) + 1)
				+ ", " + (Util.findIndex(polygon, b) + 1));
		diagonals.add(new Diagonal(a, b));
	}

}
