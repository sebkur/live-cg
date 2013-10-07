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
package de.topobyte.polygon.monotonepieces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.GeomMath;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.util.Stack;

public class MonotoneTriangulationOperation
{

	final static Logger logger = LoggerFactory
			.getLogger(MonotoneTriangulationOperation.class);

	private enum Side {
		LEFT, RIGHT
	}

	private Polygon polygon;

	private int minIndex = -1;
	private int maxIndex = -1;

	private List<Node> nodes = new ArrayList<Node>();
	private Map<Node, Side> side = new HashMap<Node, Side>();
	private Stack<Node> stack = new Stack<Node>();
	private List<Diagonal> diagonals = new ArrayList<Diagonal>();

	public MonotoneTriangulationOperation(Polygon polygon)
	{
		this.polygon = polygon;

		// Determine top and bottom nodes
		determineTopBottom();

		// Merge chains by traversing left and right chain
		mergeChains();

		// Store left / right chain information
		storeSideInfo();

		// Triangulate
		triangulate(nodes);
	}

	public List<Diagonal> getDiagonals()
	{
		return diagonals;
	}

	private void storeSideInfo()
	{
		Chain shell = polygon.getShell();
		IntRing left = new IntRing(shell.getNumberOfNodes(), minIndex).next();
		while (left.value() != maxIndex) {
			logger.debug("Left chain: " + (left.value() + 1));
			side.put(shell.getNode(left.value()), Side.LEFT);
			left.next();
		}
		IntRing right = new IntRing(shell.getNumberOfNodes(), minIndex).prev();
		while (right.value() != maxIndex) {
			logger.debug("Right chain: " + (right.value() + 1));
			side.put(shell.getNode(right.value()), Side.RIGHT);
			right.prev();
		}
	}

	private void determineTopBottom()
	{
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			Coordinate c = node.getCoordinate();
			if (c.getY() < minY) {
				minY = c.getY();
				minIndex = i;
			}
			if (c.getY() > maxY) {
				maxY = c.getY();
				maxIndex = i;
			}
		}
		logger.debug("Top node: #" + (minIndex + 1));
		logger.debug("Bottom node: #" + (maxIndex + 1));
	}

	private void mergeChains()
	{
		Chain shell = polygon.getShell();
		logger.debug("Add: " + (minIndex + 1) + ": "
				+ shell.getNode(minIndex).getCoordinate().getY());
		nodes.add(shell.getNode(minIndex));
		IntRing left = new IntRing(shell.getNumberOfNodes(), minIndex).next();
		IntRing right = new IntRing(shell.getNumberOfNodes(), minIndex).prev();
		while (left.value() != maxIndex || right.value() != maxIndex) {
			if (left.value() == maxIndex) {
				logger.debug("Left chain fished");
				Coordinate c = shell.getCoordinate(right.value());
				logger.debug("Add: " + (right.value() + 1) + ": " + c.getY());
				nodes.add(shell.getNode(right.value()));
				right.prev();
				continue;
			} else if (right.value() == maxIndex) {
				logger.debug("Right chain fished");
				Coordinate c = shell.getCoordinate(left.value());
				logger.debug("Add: " + (left.value() + 1) + ": " + c.getY());
				nodes.add(shell.getNode(left.value()));
				left.next();
				continue;
			}
			int l = left.value();
			int r = right.value();
			Node nl = shell.getNode(l);
			Node nr = shell.getNode(r);
			Coordinate cl = nl.getCoordinate();
			Coordinate cr = nr.getCoordinate();
			if (cl.getY() <= cr.getY()) {
				Coordinate c = shell.getCoordinate(left.value());
				logger.debug("Add: " + (left.value() + 1) + ": " + c.getY());
				nodes.add(shell.getNode(left.value()));
				left.next();
			} else {
				Coordinate c = shell.getCoordinate(right.value());
				logger.debug("Add: " + (right.value() + 1) + ": " + c.getY());
				nodes.add(shell.getNode(right.value()));
				right.prev();
			}
		}
		logger.debug("Add: " + (maxIndex + 1) + ": "
				+ shell.getNode(maxIndex).getCoordinate().getY());
		nodes.add(shell.getNode(maxIndex));
	}

	private void triangulate(List<Node> nodes)
	{
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
					if (!canAdd(uj, top, last)) {
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

	private boolean canAdd(Node from, Node to, Node check)
	{
		if (side.get(from) == Side.RIGHT) {
			return GeomMath.isRightOf(from.getCoordinate(), to.getCoordinate(),
					check.getCoordinate());
		} else {
			return GeomMath.isLeftOf(from.getCoordinate(), to.getCoordinate(),
					check.getCoordinate());
		}
	}

	private void addDiagonal(Node a, Node b)
	{
		logger.debug("Adding diagonal: " + (findIndex(a) + 1) + ", "
				+ (findIndex(b) + 1));
		diagonals.add(new Diagonal(a, b));
	}

	private int findIndex(Node n)
	{
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			if (n == shell.getNode(i)) {
				return i;
			}
		}
		return -1;
	}

}
