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
import de.topobyte.livecg.core.algorithm.DefaultSceneAlgorithm;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.util.Stack;

public class MonotoneTriangulationAlgorithm extends DefaultSceneAlgorithm
{

	final static Logger logger = LoggerFactory
			.getLogger(MonotoneTriangulationAlgorithm.class);

	private Polygon polygon;

	private Info info;
	private Stack<Node> stack = new Stack<>();
	private List<Diagonal> diagonals = new ArrayList<>();
	private List<Diagonal> temporaryDiagonals = new ArrayList<>();

	private int status = 0;
	private int subStatus = 0;

	public MonotoneTriangulationAlgorithm(Polygon polygon)
	{
		this.polygon = polygon;

		info = new Info(polygon);
		info.prepare();

		// Triangulate
		computeUpToStatus();
	}

	private void computeUpToStatus()
	{
		diagonals.clear();
		temporaryDiagonals.clear();
		stack.clear();
		triangulate();
	}

	public Polygon getPolygon()
	{
		return polygon;
	}

	public List<Node> getNodes()
	{
		return info.nodes;
	}

	public List<Node> getStack()
	{
		return stack;
	}

	public List<Diagonal> getDiagonals()
	{
		return diagonals;
	}

	private void triangulate()
	{
		List<Node> nodes = info.nodes;
		Map<Node, Side> side = info.side;

		if (status > 0) {
			logger.debug("Triangulating");
			Node u1 = nodes.get(0);
			Node u2 = nodes.get(1);
			stack.push(u1);
			stack.push(u2);
		}

		for (int j = 2; j < nodes.size() - 1 && j <= status; j++) {
			Node uj = nodes.get(j);
			if (side.get(stack.top()) != side.get(uj)) {
				Node first = stack.pop();
				addDiagonalReverse(uj, first);
				while (stack.size() > 1) {
					Node popped = stack.pop();
					addDiagonalReverse(uj, popped);
				}
				commitDiagonals();
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

		if (status < nodes.size() - 1) {
			return;
		}

		if (stack.size() > 2) {
			stack.pop();
			Node un = nodes.get(nodes.size() - 1);
			while (stack.size() > 1) {
				Node popped = stack.pop();
				addDiagonalReverse(un, popped);
			}
			commitDiagonals();
		}
	}

	private void addDiagonal(Node a, Node b)
	{
		logger.debug("Adding diagonal: " + (Util.findIndex(polygon, a) + 1)
				+ ", " + (Util.findIndex(polygon, b) + 1));
		diagonals.add(new Diagonal(a, b));
	}

	private void addDiagonalReverse(Node a, Node b)
	{
		logger.debug("Adding diagonal: " + (Util.findIndex(polygon, a) + 1)
				+ ", " + (Util.findIndex(polygon, b) + 1));
		temporaryDiagonals.add(new Diagonal(a, b));
	}

	private void commitDiagonals()
	{
		int x = temporaryDiagonals.size() - 1;
		for (int i = x; i >= 0; i--) {
			diagonals.add(temporaryDiagonals.get(i));
		}
		temporaryDiagonals.clear();
	}

	@Override
	public Rectangle getScene()
	{
		Rectangle scene = BoundingBoxes.get(polygon);
		return Rectangles.extend(scene, 15);
	}

	public int getStatus()
	{
		return status;
	}

	public int getSubStatus()
	{
		return subStatus;
	}

	public void setStatus(int major, int minor)
	{
		status = major;
		computeUpToStatus();
		if (minor == -1) {
			this.subStatus = numberOfMinorSteps();
		} else {
			this.subStatus = minor;
		}
		fireAlgorithmStatusChanged();
	}

	public void setSubStatus(int value)
	{
		subStatus = value;
		fireAlgorithmStatusChanged();
	}

	public int getNumberOfSteps()
	{
		return polygon.getShell().getNumberOfNodes() - 1;
	}

	public int numberOfMinorSteps()
	{
		if (status == 0) {
			return 0;
		}

		List<Node> nodes = info.nodes;
		Map<Node, Side> side = info.side;

		if (status < nodes.size() - 2) {
			Node uj = nodes.get(status + 1);
			if (side.get(stack.top()) != side.get(uj)) {
				return stack.size();
			} else {
				int n = 1;
				Node last = stack.top();
				for (int i = 0; i < stack.size() - 1; i++) {
					Node top = stack.top(i + 1);
					if (!Util.canAdd(side.get(uj), uj, top, last)) {
						break;
					} else {
						n++;
						last = top;
					}
				}
				return n;
			}
		}

		return 1;
	}

	public List<Diagonal> getMinorDiagonals()
	{
		List<Diagonal> diagonals = new ArrayList<>();

		List<Node> nodes = info.nodes;
		Map<Node, Side> side = info.side;

		if (status > 0 && status < nodes.size() - 1) {
			Node uj = nodes.get(status + 1);
			if (side.get(stack.top()) != side.get(uj)) {
				if (subStatus > 1) {
					Node first = stack.top();
					diagonals.add(new Diagonal(uj, first));
				}
				for (int i = 0; i < subStatus - 2; i++) {
					Node popped = stack.top(i + 1);
					diagonals.add(new Diagonal(uj, popped));
				}
			} else {
				Node last = stack.top();
				for (int i = 0; i < subStatus - 1; i++) {
					Node top = stack.top(i + 1);
					if (!Util.canAdd(side.get(uj), uj, top, last)) {
						break;
					} else {
						diagonals.add(new Diagonal(uj, top));
						last = top;
					}
				}

			}
		}

		return diagonals;
	}
}
