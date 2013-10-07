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
package de.topobyte.polygon.shortestpath;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.GeomMath;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.polygon.monotonepieces.Diagonal;
import de.topobyte.polygon.monotonepieces.DiagonalUtil;
import de.topobyte.polygon.monotonepieces.SplitResult;
import de.topobyte.polygon.monotonepieces.TriangulationOperation;
import de.topobyte.util.graph.Graph;

public class ShortestPathAlgorithm
{
	final static Logger logger = LoggerFactory
			.getLogger(ShortestPathAlgorithm.class);

	private Polygon polygon;
	private TriangulationOperation triangulationOperation;
	private List<Diagonal> triangulationDiagonals;
	private Graph<Polygon, Diagonal> graph;

	private Node nodeStart;
	private Node nodeTarget;

	private Polygon triangleStart;
	private Polygon triangleTarget;

	private Sleeve sleeve;
	private int status;

	public ShortestPathAlgorithm(Polygon polygon, Node nodeStart,
			Node nodeTarget)
	{
		this.polygon = polygon;
		this.nodeStart = nodeStart;
		this.nodeTarget = nodeTarget;
		triangulationOperation = new TriangulationOperation(polygon);
		triangulationDiagonals = triangulationOperation.getDiagonals();

		SplitResult splitResult = DiagonalUtil.split(polygon,
				triangulationDiagonals);
		graph = splitResult.getGraph();

		List<Polygon> triangulation = splitResult.getPolygons();
		for (Polygon triangle : triangulation) {
			Chain shell = triangle.getShell();
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				if (shell.getNode(i) == nodeStart) {
					triangleStart = triangle;
				}
				if (shell.getNode(i) == nodeTarget) {
					triangleTarget = triangle;
				}
			}
		}

		sleeve = GraphFinder.find(graph, triangleStart, triangleTarget);
		SleeveUtil.optimizePath(sleeve, nodeStart, nodeTarget);

		List<Polygon> triangles = sleeve.getPolygons();
		triangleStart = triangles.get(0);
		triangleTarget = triangles.get(triangles.size() - 1);
	}

	public Polygon getPolygon()
	{
		return polygon;
	}

	public Node getNodeStart()
	{
		return nodeStart;
	}

	public Node getNodeTarget()
	{
		return nodeTarget;
	}

	public Sleeve getSleeve()
	{
		return sleeve;
	}

	public Polygon getTriangleStart()
	{
		return triangleStart;
	}

	public Polygon getTriangleTarget()
	{
		return triangleTarget;
	}

	public Graph<Polygon, Diagonal> getGraph()
	{
		return graph;
	}

	public List<Diagonal> getTriangulationDiagonals()
	{
		return triangulationDiagonals;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		if (this.status != status) {
			this.status = status;
			computeUpTo(status);
		}
	}

	private Data data;
	private Side currentChain;

	public Data getData()
	{
		return data;
	}
	
	private void computeUpTo(int diagonal)
	{
		if (diagonal == 0) {
			data = null;
			return;
		}
		List<Polygon> polygons = sleeve.getPolygons();
		// Get the first triangle
		Polygon p0 = polygons.get(0);
		// One of the first triangle's nodes must be the start node
		Node n0 = p0.getShell().getNode(0);
		Node n1 = p0.getShell().getNode(1);
		Node n2 = p0.getShell().getNode(2);
		if (n0 == nodeStart) {
			n0 = n1;
			n1 = n2;
		} else if (n1 == nodeStart) {
			n1 = n2;
		} else if (n2 != nodeStart) {
			logger.error("None of the first triangle's nodes is the start node");
		}
		// Triangle is in CCW order, so this is true:
		Node right = n0;
		Node left = n1;
		
		// Initialize data structures
		data = new Data(nodeStart, left, right);

		// Main algorithm loop
		List<Diagonal> diagonals = sleeve.getDiagonals();
		for (int i = 2; i <= diagonals.size() + 1 && i <= diagonal; i++) {
			logger.debug("Diagonal " + i);
			Diagonal d;
			if (i <= diagonals.size()) {
				d = diagonals.get(i - 1);
			} else {
				Diagonal last = diagonals.get(diagonals.size() - 1);
				d = new Diagonal(last.getA(), nodeTarget);
			}
			// Find node of diagonal that is not node of d_(i-1)
			left = data.getLast(Side.LEFT);
			right = data.getLast(Side.RIGHT);
			Node notOnChain = d.getA();
			Node alreadyOnChain = d.getB();
			if (d.getA() == left || d.getA() == right) {
				notOnChain = d.getB();
				alreadyOnChain = d.getA();
			}
			if (alreadyOnChain == left) {
				// Next node is on right chain
				logger.debug("next node is on right chain");
				currentChain = Side.RIGHT;
				updateFunnel(notOnChain, Side.RIGHT, Side.LEFT);
			} else if (alreadyOnChain == right) {
				// Next node is on left chain
				logger.debug("next node is on left chain");
				currentChain = Side.LEFT;
				updateFunnel(notOnChain,  Side.LEFT, Side.RIGHT);
			} else {
				logger.error("next node could not be found on any chain");
			}
			logger.debug("left path length: " + data.getFunnelLength(Side.LEFT));
			logger.debug("right path length: " + data.getFunnelLength(Side.RIGHT));
		}
		
		// Make the left path the overall shortest path
		if (diagonal == diagonals.size() + 2) {
			for (int i = 0; i < data.getFunnelLength(currentChain); i++) {
				data.appendCommon(data.removeFirst(currentChain));
			}
			data.clear(Side.other(currentChain));
		}
	}

	private void updateFunnel(Node notOnChain, Side on, Side other)
	{
		boolean found = false;
		if (data.getFunnelLength(on) == 0) {
			logger.debug("case1: path1 has length 1");
			data.append(on, notOnChain);
			found = true;
		}
		if (!found) {
			logger.debug("case2: walking backwards on path1");
			for (int k = data.getFunnelLength(on) - 1; k >= 0; k--) {
				Node pn1 = k == 0 ? data.getApex() : data.get(on, k - 1);
				Node pn2 = data.get(on, k);
				boolean turnOk = turnOk(pn1, pn2, notOnChain, on);
				if (!turnOk) {
					data.removeLast(on);
				} else {
					found = true;
					data.append(on, notOnChain);
					break;
				}
			}
		}
		if (!found) {
			logger.debug("case3: walking forward on path2");
			for (int k = -1; k < data.getFunnelLength(other) - 1; k++) {
				Node pn1 = k == -1 ? data.getApex() : data.get(other, k);
				Node pn2 = data.get(other, k + 1);
				boolean turnOk = turnOk(pn1, pn2, notOnChain, on);
				if (turnOk) {
					logger.debug("turn is ok with k=" + k);
					found = true;
					Node w = pn1;
					if (k == -1) {
						data.append(on, notOnChain);
					} else {
						data.clear(on);
						data.append(on, notOnChain);
						for (int l = 0; l <= k; l++) {
							data.appendCommon(data.removeFirst(other));
						}
						data.appendCommon(w);
					}
					break;
				}
			}
		}
		if (!found) {
			logger.debug("case4: moving apex to last node of path2");
			data.clear(on);
			data.append(on, notOnChain);
			for (int k = 0; k < data.getFunnelLength(other);) {
				data.appendCommon(data.removeFirst(other));
			}
		}
	}

	private boolean turnOk(Node pn1, Node pn2, Node notOnChain, Side side)
	{
		if (side == Side.LEFT) {
			return GeomMath.isLeftOf(pn1.getCoordinate(), pn2.getCoordinate(),
					notOnChain.getCoordinate());
		} else {
			return GeomMath.isRightOf(pn1.getCoordinate(), pn2.getCoordinate(),
					notOnChain.getCoordinate());
		}
	}
}
