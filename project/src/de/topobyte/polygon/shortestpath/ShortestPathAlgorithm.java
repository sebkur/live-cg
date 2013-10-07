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
		this.status = status;
		computeUpTo(status);
	}

	private Path leftPath, rightPath;

	public Path getLeftPath()
	{
		return leftPath;
	}

	public Path getRightPath()
	{
		return rightPath;
	}

	private void computeUpTo(int diagonal)
	{
		if (diagonal == 0) {
			leftPath = null;
			rightPath = null;
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
		leftPath = new Path(nodeStart, left);
		rightPath = new Path(nodeStart, right);

		List<Diagonal> diagonals = sleeve.getDiagonals();
		for (int i = 1; i < diagonals.size() && i < diagonal; i++) {
			// TODO: steps
		}

		if (diagonal == diagonals.size() + 1) {
			// TODO: last step
		}
	}
}
