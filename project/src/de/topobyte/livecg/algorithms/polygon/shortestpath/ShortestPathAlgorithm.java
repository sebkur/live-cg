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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.polygon.monotonepieces.Diagonal;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.DiagonalUtil;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.SplitResult;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.TriangulationOperation;
import de.topobyte.livecg.core.AlgorithmWatcher;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CrossingsTest;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.graph.Graph;

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

	private Data data;
	private Side currentChain;

	/*
	 * Watchers that need to be notified once the algorithm moved to a new
	 * state.
	 */
	private List<AlgorithmWatcher> watchers = new ArrayList<AlgorithmWatcher>();

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

	public void addWatcher(AlgorithmWatcher watcher)
	{
		watchers.add(watcher);
	}

	public void removeWatcher(AlgorithmWatcher watcher)
	{
		watchers.remove(watcher);
	}

	private void notifyWatchers()
	{
		for (AlgorithmWatcher watcher : watchers) {
			watcher.updateAlgorithmStatus();
		}
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

	public Data getData()
	{
		return data;
	}

	public void setStatus(int status)
	{
		if (status == this.status) {
			return;
		}
		if (status == 0) {
			data = null;
			this.status = 0;
		} else if (status > this.status) {
			computeUpTo(status);
		} else if (status < this.status) {
			data = null;
			this.status = 0;
			computeUpTo(status);
		}
		notifyWatchers();
	}

	private void computeUpTo(int diagonal)
	{
		if (status == 0) {
			// Initialize data structures
			data = new Data(start, left, right);
			status = 1;

			// Handle the case with start and target lying in the same triangle
			if (triangleStart == triangleTarget) {
				data.appendCommon(target);
				data.clear(Side.LEFT);
				data.clear(Side.RIGHT);
				return;
			}
		}

		// Main algorithm loop
		List<Diagonal> diagonals = sleeve.getDiagonals();
		while (status <= diagonals.size() && status < diagonal) {
			logger.debug("Diagonal " + status);
			Diagonal next = nextDiagonal();
			currentChain = sideOfNextNode(next);
			logger.debug("next node is on " + currentChain + " chain");
			// Find node of diagonal that is not node of d_(i-1)
			Node notYetOnChain = notYetOnChain(next);
			updateFunnel(notYetOnChain, currentChain);
			logger.debug("left path length: " + data.getFunnelLength(Side.LEFT));
			logger.debug("right path length: "
					+ data.getFunnelLength(Side.RIGHT));
			status += 1;
		}

		// Make the current path the overall shortest path
		if (diagonal >= diagonals.size() + 2) {
			status = diagonal;
			for (int i = 0; i < data.getFunnelLength(currentChain);) {
				data.appendCommon(data.removeFirst(currentChain));
			}
			data.clear(Side.other(currentChain));
		}
	}

	private Diagonal nextDiagonal()
	{
		List<Diagonal> diagonals = sleeve.getDiagonals();
		if (status < diagonals.size()) {
			return diagonals.get(status);
		} else {
			Diagonal last = diagonals.get(diagonals.size() - 1);
			// Add a final diagonal that extends the right chain
			if (last.getA() == data.getLast(Side.LEFT)) {
				return new Diagonal(last.getA(), target);
			} else {
				return new Diagonal(last.getB(), target);
			}
		}
	}

	private Side other(Side side)
	{
		if (side == Side.LEFT) {
			return Side.RIGHT;
		}
		return Side.LEFT;
	}

	private Side sideOfNextNode(Diagonal d)
	{
		Node left = data.getLast(Side.LEFT);
		Node right = data.getLast(Side.RIGHT);
		if (d.getA() == left || d.getB() == left) {
			return Side.RIGHT;
		} else if (d.getA() == right || d.getB() == right) {
			return Side.LEFT;
		} else {
			return null;
		}
	}

	// Find node of diagonal that is not node of d_(i-1)
	private Node notYetOnChain(Diagonal diagonal)
	{
		Node endOfLeftPath = data.getLast(Side.LEFT);
		Node endOfRightPath = data.getLast(Side.RIGHT);
		if (diagonal.getA() == endOfLeftPath
				|| diagonal.getA() == endOfRightPath) {
			return diagonal.getB();
		}
		return diagonal.getA();
	}

	private void updateFunnel(Node notOnChain, Side on)
	{
		Side other = other(on);
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
			data.append(on, notOnChain);
			for (int k = 0; k < data.getFunnelLength(other);) {
				data.appendCommon(data.removeFirst(other));
			}
		}
	}

	private void numberOfStepsToUpdateFunnel(Node notOnChain, Side on)
	{
		Side other = other(on);
		boolean found = false;
		if (data.getFunnelLength(on) == 0) {
			logger.debug("case1: path1 has length 1");
			// data.append(on, notOnChain);
			found = true;
		}
		if (!found) {
			logger.debug("case2: walking backwards on path1");
			for (int k = data.getFunnelLength(on) - 1; k >= 0; k--) {
				Node pn1 = k == 0 ? data.getApex() : data.get(on, k - 1);
				Node pn2 = data.get(on, k);
				boolean turnOk = turnOk(pn1, pn2, notOnChain, on);
				if (!turnOk) {
					// data.removeLast(on);
				} else {
					found = true;
					// data.append(on, notOnChain);
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
					// Node w = pn1;
					if (k == -1) {
						// data.append(on, notOnChain);
					} else {
						// data.append(on, notOnChain);
						// for (int l = 0; l <= k; l++) {
						// data.appendCommon(data.removeFirst(other));
						// }
						// data.appendCommon(w);
					}
					break;
				}
			}
		}
		if (!found) {
			logger.debug("case4: moving apex to last node of path2");
			// data.append(on, notOnChain);
			// for (int k = 0; k < data.getFunnelLength(other);) {
			// data.appendCommon(data.removeFirst(other));
			// }
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
