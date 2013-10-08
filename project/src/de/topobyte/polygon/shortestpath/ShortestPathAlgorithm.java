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
import de.topobyte.livecg.geometry.geom.CrossingsTest;
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
	private SplitResult splitResult;
	private Graph<Polygon, Diagonal> graph;

	private Node start;
	private Node target;

	private Polygon triangleStart;
	private Polygon triangleTarget;

	private boolean nodeHitStart = false;
	private boolean nodeHitTarget = false;

	private Sleeve sleeve;
	private int status;
	private int numberOfSteps;

	private Node left;
	private Node right;

	// TODO: do not allow start / target out of polygons bounds

	public ShortestPathAlgorithm(Polygon polygon, Node start, Node target)
	{
		this.polygon = polygon;
		this.start = start;
		this.target = target;
		triangulationOperation = new TriangulationOperation(polygon);
		triangulationDiagonals = triangulationOperation.getDiagonals();

		splitResult = DiagonalUtil.split(polygon, triangulationDiagonals);
		graph = splitResult.getGraph();

		setup();
	}

	public void setStart(Node start)
	{
		this.start = start;
		nodeHitStart = false;
		triangleStart = null;
		setup();
	}

	public void setTarget(Node target)
	{
		this.target = target;
		nodeHitTarget = false;
		triangleTarget = null;
		setup();
	}

	public void setStartTarget(Node start, Node target)
	{
		this.start = start;
		this.target = target;
		nodeHitStart = false;
		nodeHitTarget = false;
		triangleStart = null;
		triangleTarget = null;
		setup();
	}

	private void setup()
	{
		List<Polygon> triangulation = splitResult.getPolygons();
		for (Polygon triangle : triangulation) {
			Chain shell = triangle.getShell();
			for (int i = 0; i < shell.getNumberOfNodes(); i++) {
				if (shell.getNode(i) == start) {
					nodeHitStart = true;
					triangleStart = triangle;
				}
				if (shell.getNode(i) == target) {
					nodeHitTarget = true;
					triangleTarget = triangle;
				}
			}
		}
		if (triangleStart == null || triangleTarget == null) {
			for (Polygon triangle : triangulation) {
				CrossingsTest test = new CrossingsTest(triangle.getShell());
				if (triangleStart == null && test.covers(start.getCoordinate())) {
					triangleStart = triangle;
				}
				if (triangleTarget == null
						&& test.covers(target.getCoordinate())) {
					triangleTarget = triangle;
				}
			}
		}

		if (triangleStart == null || triangleTarget == null) {
			if (triangleStart == null) {
				logger.error("unable to locate start triangle");
			}
			if (triangleTarget == null) {
				logger.error("unable to locate target triangle");
			}
			return;
		}

		sleeve = GraphFinder.find(graph, triangleStart, triangleTarget);
		SleeveUtil.optimizePath(sleeve, start, target);

		List<Polygon> triangles = sleeve.getPolygons();
		triangleStart = triangles.get(0);
		triangleTarget = triangles.get(triangles.size() - 1);

		if (triangleStart == triangleTarget) {
			numberOfSteps = 1;
			return;
		}

		numberOfSteps = sleeve.getDiagonals().size() + 2;

		// Get the first triangle
		Polygon p0 = sleeve.getPolygons().get(0);
		Node n0 = p0.getShell().getNode(0);
		Node n1 = p0.getShell().getNode(1);
		Node n2 = p0.getShell().getNode(2);
		// Get the first diagonal
		Diagonal d0 = sleeve.getDiagonals().get(0);
		Node d0a = d0.getA();
		Node d0b = d0.getB();
		// Two of the first triangle's nodes must fit the triangle
		if (n0 == d0a && n2 == d0b || n1 == d0a && n0 == d0b || n2 == d0a
				&& n1 == d0b) {
			left = d0a;
			right = d0b;
		} else if (n0 == d0b && n2 == d0a || n1 == d0b && n0 == d0a
				|| n2 == d0b && n1 == d0a) {
			left = d0b;
			right = d0a;
		} else {
			logger.error("Could not match first triangle with first diagonal");
		}

		if (!nodeHitStart) {
			Chain shell = new Chain();
			shell.appendNode(right);
			shell.appendNode(left);
			shell.appendNode(start);
			p0 = new Polygon(shell, null);
			sleeve.getPolygons().set(0, p0);
		}
		if (!nodeHitTarget) {
			Diagonal dN = sleeve.getDiagonals().get(
					sleeve.getDiagonals().size() - 1);
			Chain shell = new Chain();
			if (GeomMath.isLeftOf(dN.getA().getCoordinate(), dN.getB()
					.getCoordinate(), target.getCoordinate())) {
				shell.appendNode(dN.getA());
				shell.appendNode(dN.getB());
				shell.appendNode(target);
			} else {
				shell.appendNode(dN.getB());
				shell.appendNode(dN.getA());
				shell.appendNode(target);
			}
			Polygon pN = new Polygon(shell, null);
			sleeve.getPolygons().set(sleeve.getPolygons().size() - 1, pN);
		}
	}

	public Polygon getPolygon()
	{
		return polygon;
	}

	public Node getNodeStart()
	{
		return start;
	}

	public Node getNodeTarget()
	{
		return target;
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

	public int getNumberOfSteps()
	{
		return numberOfSteps;
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

		// Initialize data structures
		data = new Data(start, left, right);

		// Handle the case with start and target lying in the same triangle
		if (triangleStart == triangleTarget) {
			data.appendCommon(target);
			data.clear(Side.LEFT);
			data.clear(Side.RIGHT);
			return;
		}

		// Main algorithm loop
		List<Diagonal> diagonals = sleeve.getDiagonals();
		for (int i = 2; i <= diagonals.size() + 1 && i <= diagonal; i++) {
			logger.debug("Diagonal " + i);
			Diagonal d;
			if (i <= diagonals.size()) {
				d = diagonals.get(i - 1);
			} else {
				Diagonal last = diagonals.get(diagonals.size() - 1);
				d = new Diagonal(last.getA(), target);
			}
			// Find node of diagonal that is not node of d_(i-1)
			Node left = data.getLast(Side.LEFT);
			Node right = data.getLast(Side.RIGHT);
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
				updateFunnel(notOnChain, Side.LEFT, Side.RIGHT);
			} else {
				logger.error("next node could not be found on any chain");
			}
			logger.debug("left path length: " + data.getFunnelLength(Side.LEFT));
			logger.debug("right path length: "
					+ data.getFunnelLength(Side.RIGHT));
		}

		// Make the left path the overall shortest path
		if (diagonal >= diagonals.size() + 2) {
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
