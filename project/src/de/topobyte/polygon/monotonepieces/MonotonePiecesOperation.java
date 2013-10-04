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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.ChainHelper;
import de.topobyte.livecg.geometry.geom.CloseabilityException;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.GeomMath;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.geom.PolygonHelper;

public class MonotonePiecesOperation
{

	final static Logger logger = LoggerFactory
			.getLogger(MonotonePiecesOperation.class);

	private Polygon polygon;
	private Map<Node, VertexType> types = new HashMap<Node, VertexType>();

	private Map<Node, Integer> index = new HashMap<Node, Integer>();
	private Map<Integer, Node> helpers = new HashMap<Integer, Node>();

	private List<Diagonal> diagonals = new ArrayList<Diagonal>();

	private Set<Node> connected = new HashSet<Node>();

	public MonotonePiecesOperation(Polygon polygon)
	{
		this.polygon = polygon;
		Chain shell = polygon.getShell();

		/*
		 * Find interior side
		 */

		if (!PolygonHelper.isCounterClockwiseOriented(polygon)) {
			try {
				shell = ChainHelper.invert(shell);
			} catch (CloseabilityException e) {
				// TODO: what to do here
			}
			this.polygon = new Polygon(shell, null);
		}

		/*
		 * Classify vertices
		 */

		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			types.put(node, VertexType.REGULAR);
		}

		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			int pre = ring.prevValue();
			int suc = ring.next().value();
			Node nodePre = shell.getNode(pre);
			Node nodeSuc = shell.getNode(suc);

			Coordinate c = node.getCoordinate();
			Coordinate cPre = nodePre.getCoordinate();
			Coordinate cSuc = nodeSuc.getCoordinate();

			if (c.getY() < cPre.getY() && c.getY() < cSuc.getY()) {
				types.put(node, VertexType.START);
				double interiorAngle = GeomMath.angle(c, cPre, cSuc);
				if (interiorAngle > Math.PI) {
					types.put(node, VertexType.SPLIT);
				}
			} else if (c.getY() > cPre.getY() && c.getY() > cSuc.getY()) {
				types.put(node, VertexType.END);
				double interiorAngle = GeomMath.angle(c, cPre, cSuc);
				if (interiorAngle > Math.PI) {
					types.put(node, VertexType.MERGE);
				}
			}
		}

		/*
		 * Build node index lookup
		 */

		index = ChainHelper.buildNodeIndexLookup(shell);

		/*
		 * Create priority queue
		 */

		PriorityQueue<Node> queue = new PriorityQueue<Node>(11,
				new Comparator<Node>() {

					@Override
					public int compare(Node o1, Node o2)
					{
						Coordinate c1 = o1.getCoordinate();
						Coordinate c2 = o2.getCoordinate();
						if (c1.getY() == c2.getY()) {
							if (c1.getX() == c2.getX()) {
								return 0;
							} else if (c1.getX() < c2.getX()) {
								return -1;
							} else {
								return 1;
							}
						}
						if (c1.getY() < c2.getY()) {
							return -1;
						} else {
							return 1;
						}
					}
				});

		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			queue.add(node);
		}

		/*
		 * Remove elements from queue as long as available
		 */

		while (!queue.isEmpty()) {
			Node node = queue.poll();

			VertexType type = types.get(node);
			logger.debug("Handle node: " + (index.get(node) + 1) + ", type: "
					+ type);
			switch (type) {
			case START:
				handleStart(node);
				break;
			case END:
				handleEnd(node);
				break;
			case SPLIT:
				handleSplit(node);
				break;
			case MERGE:
				handleMerge(node);
				break;
			case REGULAR:
				handleRegular(node);
				break;
			}
		}

		/*
		 * Look for helper-merge-vertices that have not been handled
		 */

		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node helper = helpers.get(i);
			if (helper == null) {
				continue;
			}
			logger.debug("Remaining helper of edge " + (i + 1) + " type: "
					+ types.get(helper));
			if (types.get(helper) == VertexType.MERGE) {
				if (!connected.contains(helper)) {
					Node end = shell.getNode(i);
					insertDiagonal(helper, end);
				}
			}
		}
	}

	private int prev(int i)
	{
		int k = i - 1;
		if (k == -1) {
			return polygon.getShell().getNumberOfNodes() - 1;
		}
		return k;
	}

	private int next(int i)
	{
		int k = i + 1;
		if (k == polygon.getShell().getNumberOfNodes()) {
			return 0;
		}
		return k;
	}

	/*
	 * Node handle methods
	 */

	private void handleStart(Node node)
	{
		// Insert e_(i) in T
		int i = index(node);
		setHelper(i, node);
	}

	private void handleEnd(Node node)
	{
		// Remove e_(i-1) from T
		int i = index(node);
		Node helper = getHelper(prev(i));
		if (types.get(helper) == VertexType.MERGE) {
			insertDiagonal(node, helper);
		}
	}

	private void handleSplit(Node node)
	{
		int i = index(node);
		int j = findEdgeDirectlyToTheLeftOf(node);
		Node helper = getHelper(j);
		insertDiagonal(node, helper);
		setHelper(j, node);
		// Insert e_(i) in T
		setHelper(i, node);
	}

	private void handleMerge(Node node)
	{
		int i = index(node);
		Node helper = getHelper(prev(i));
		logger.debug("Helper is: " + (index.get(helper)));
		if (types.get(helper) == VertexType.MERGE) {
			insertDiagonal(node, helper);
		}
		// Delete e_(i-1) from T
		int j = findEdgeDirectlyToTheLeftOf(node);
		helper = getHelper(j);
		if (types.get(helper) == VertexType.MERGE) {
			insertDiagonal(node, helper);
		}
		setHelper(j, node);
	}

	private void handleRegular(Node node)
	{
		int i = index(node);
		Node next = polygon.getShell().getNode(next(i));
		boolean interiorToTheRightOfNode = false;
		if (next.getCoordinate().getY() == node.getCoordinate().getY()) {
			// TODO: Degenerate case
			logger.error("Degenerate case not implemented");
		} else if (next.getCoordinate().getY() > node.getCoordinate().getY()) {
			interiorToTheRightOfNode = true;
		} else {
			interiorToTheRightOfNode = false;
		}
		if (interiorToTheRightOfNode) {
			Node helper = getHelper(prev(i));
			if (types.get(helper) == VertexType.MERGE) {
				insertDiagonal(node, helper);
			}
			// Delete e_(i-1) from T
			// Insert e_(i) in T
			setHelper(i, node);
		} else {
			int j = findEdgeDirectlyToTheLeftOf(node);
			Node helper = getHelper(j);
			if (types.get(helper) == VertexType.MERGE) {
				insertDiagonal(node, helper);
			}
			setHelper(j, node);
		}
	}

	private int findEdgeDirectlyToTheLeftOf(Node node)
	{
		int idx = index(node);

		int edge = -1;
		double dx = Double.MAX_VALUE;
		Coordinate c = node.getCoordinate();
		Chain shell = polygon.getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			// Do not allow edges adjacent to the node
			if (idx == j || idx == i) {
				continue;
			}
			// logger.debug("checking " + i + ", " + j);
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			// Make sure c1.y <= c2.y
			if (c1.getY() > c2.getY()) {
				Coordinate tmp = c1;
				c1 = c2;
				c2 = tmp;
			}
			// Only edges where node.y is within the edges y-range
			if (c1.getY() > c.getY() || c2.getY() < c.getY()) {
				continue;
			}
			// X-coordinate where horizontal ray from node meets edge
			double x = c1.getX() + (c2.getX() - c1.getX())
					* (c.getY() - c1.getY()) / (c2.getY() - c1.getY());
			// logger.debug("x: " + x);
			// Only edges to the left of node
			if (x > c.getX()) {
				continue;
			}
			if (c.getX() - x < dx) {
				logger.debug("replacing nearest edge, dx: " + dx);
				dx = c.getX() - x;
				edge = i;
			}
		}
		logger.debug("Found edge: " + edge);
		return edge;
	}

	private int index(Node node)
	{
		return index.get(node);
	}

	private void setHelper(int i, Node node)
	{
		helpers.put(i, node);
	}

	private Node getHelper(int i)
	{
		if (types.get(helpers.get(i)) == VertexType.MERGE) {
			logger.debug("getHelper: MERGE");
		}
		return helpers.get(i);
	}

	private void insertDiagonal(Node from, Node to)
	{
		if (from == null) {
			logger.debug("from is null");
		}
		if (to == null) {
			logger.debug("to is null");
		}
		logger.debug("Inserting diagonal: " + (index.get(from) + 1) + " -> "
				+ (index.get(to) + 1));
		diagonals.add(new Diagonal(from, to));
		connected.add(from);
		connected.add(to);
	}

	public VertexType getType(Node node)
	{
		return types.get(node);
	}

	public List<Diagonal> getDiagonals()
	{
		return diagonals;
	}

	public List<Polygon> getMonotonePieces()
	{
		List<Polygon> pieces = DiagonalUtil.split(polygon, diagonals);
		return pieces;
	}

}
