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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.JPanel;

import de.topobyte.frechet.ui.freespace.calc.Vector;
import de.topobyte.livecg.geometry.geom.AwtHelper;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.ChainHelper;
import de.topobyte.livecg.geometry.geom.CloseabilityException;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.util.SwingUtil;

public class MonotonePiecesPanel extends JPanel
{

	private static final long serialVersionUID = 2129465700417909129L;

	private Polygon polygon;
	private Map<Node, VertexType> types = new HashMap<Node, VertexType>();

	private Map<Node, Double> dets = new HashMap<Node, Double>();
	private Map<Node, Double> angles = new HashMap<Node, Double>();

	private Map<Node, Integer> index = new HashMap<Node, Integer>();
	private Map<Integer, Node> helpers = new HashMap<Integer, Node>();
	
	private List<Diagonal> diagonals = new ArrayList<Diagonal>();

	public MonotonePiecesPanel(Polygon polygon)
	{
		this.polygon = polygon;
		Chain shell = polygon.getShell();

		/*
		 * Find interior side
		 */

		double sum1 = 0, sum2 = 0;

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

			double angle = angle(c, cPre, cSuc);
			double det = determinant(c, cPre, cSuc);
			dets.put(node, det);
			angles.put(node, angle);

			sum1 += angle;
			sum2 += Math.PI * 2 - angle;
		}

		if (sum1 > sum2) {
			try {
				System.out.println("inverting ");
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

		ring.reset();
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
				double interiorAngle = angle(c, cPre, cSuc);
				if (interiorAngle > Math.PI) {
					types.put(node, VertexType.SPLIT);
				}
			} else if (c.getY() > cPre.getY() && c.getY() > cSuc.getY()) {
				types.put(node, VertexType.END);
				double interiorAngle = angle(c, cPre, cSuc);
				if (interiorAngle > Math.PI) {
					types.put(node, VertexType.MERGE);
				}
			}
		}

		/*
		 * Build node index lookup
		 */

		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			index.put(node, i);
		}

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
	}

	private double angle(Coordinate c, Coordinate cPre, Coordinate cSuc)
	{
		Vector v1 = new Vector(cPre.getX() - c.getX(), cPre.getY() - c.getY());
		Vector v2 = new Vector(cSuc.getX() - c.getX(), cSuc.getY() - c.getY());
		double dotProduct = v1.dotProduct(v2);
		double cosAngle = dotProduct / (v1.norm() * v2.norm());
		double angle = Math.acos(cosAngle);
		double det = determinant(c, cPre, cSuc);
		if (det > 0) {
			angle = Math.PI * 2 - angle;
		}
		return angle;
	}

	private double determinant(Coordinate c, Coordinate cPre, Coordinate cSuc)
	{
		double det = (c.getX() - cPre.getX()) * (cSuc.getY() - cPre.getY())
				- (c.getY() - cPre.getY()) * (cSuc.getX() - cPre.getX());
		return det;
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
			// Only edges to the left of node
			if (x > c.getX()) {
				continue;
			}
			if (c1.getX() - x < dx) {
				dx = c1.getX() - x;
			}
			edge = i;
		}
		System.out.println("found for " + idx + ": " + edge);
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
		return helpers.get(i);
	}

	private void insertDiagonal(Node node, Node helper)
	{
		diagonals.add(new Diagonal(node, helper));
	}

	@Override
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		Area shape = AwtHelper.toShape(polygon);
		g.setColor(new Color(0x66ff0000, true));
		g.fill(shape);

		g.setColor(Color.BLACK);
		Chain shell = polygon.getShell();
		IntRing ring = new IntRing(shell.getNumberOfNodes());
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			int j = ring.next().value();
			Coordinate c1 = shell.getCoordinate(i);
			Coordinate c2 = shell.getCoordinate(j);
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}
		
		for (Diagonal diagonal : diagonals) {
			Coordinate c1 = diagonal.getA().getCoordinate();
			Coordinate c2 = diagonal.getB().getCoordinate();
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		g.setColor(Color.BLACK);
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			VertexType type = types.get(node);
			Coordinate c = node.getCoordinate();

			double arcSize = 6;
			double rectSize = 6;
			double triangleSize = 8;

			Arc2D arc = new Arc2D.Double(c.getX() - arcSize / 2, c.getY()
					- arcSize / 2, arcSize, arcSize, 0, 360, Arc2D.CHORD);
			Rectangle2D rect = new Rectangle2D.Double(c.getX() - rectSize / 2,
					c.getY() - rectSize / 2, rectSize, rectSize);
			Path2D triangle = new Path2D.Double();

			switch (type) {
			case REGULAR:
				g.fill(arc);
				break;
			case START:
				g.draw(rect);
				break;
			case END:
				g.fill(rect);
				break;
			case SPLIT:
				triangle.moveTo(c.getX(), c.getY() - triangleSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			case MERGE:
				triangle.moveTo(c.getX(), c.getY() + arcSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			default:
				break;
			}

			g.drawString(String.format("%d", i), (float) c.getX() + 10,
					(float) c.getY());
		}
	}
}
