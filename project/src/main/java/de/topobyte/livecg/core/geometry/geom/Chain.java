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

package de.topobyte.livecg.core.geometry.geom;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import de.topobyte.livecg.util.circular.IntRing;

public class Chain
{
	static Logger logger = LoggerFactory.getLogger(Chain.class);

	private boolean closed = false;

	private List<Node> nodes = new ArrayList<>();
	private List<Polygon> polygons = new ArrayList<>();

	public void prependPoint(Coordinate coordinate)
	{
		Node node = new Node(coordinate);
		prependNode(node);
	}

	public void prependNode(Node node)
	{
		Node first = null;
		if (nodes.size() > 1) {
			first = nodes.get(0);
		}
		node.addChain(this);
		node.addEndpointChain(this);
		nodes.add(0, node);
		if (first != null) {
			first.removeEndpointChain(this);
		}
	}

	public void appendPoint(Coordinate coordinate)
	{
		Node node = new Node(coordinate);
		appendNode(node);
	}

	public void appendNode(Node node)
	{
		Node last = null;
		if (nodes.size() > 1) {
			last = nodes.get(nodes.size() - 1);
		}
		node.addChain(this);
		node.addEndpointChain(this);
		nodes.add(node);
		if (last != null) {
			last.removeEndpointChain(this);
		}
	}

	public int getNumberOfNodes()
	{
		return nodes.size();
	}

	public Coordinate getCoordinate(int i)
	{
		return nodes.get(i).getCoordinate();
	}

	public Node getNode(int i)
	{
		return nodes.get(i);
	}

	public void setNode(int i, Node node)
	{
		nodes.set(i, node);
	}

	public void setCoordinate(int i, Coordinate coordinate)
	{
		nodes.get(i).setCoordinate(coordinate);
	}

	public Node getFirstNode()
	{
		return nodes.get(0);
	}

	public Node getLastNode()
	{
		return nodes.get(nodes.size() - 1);
	}

	public Coordinate getFirstCoordinate()
	{
		return nodes.get(0).getCoordinate();
	}

	public Coordinate getLastCoordinate()
	{
		return nodes.get(nodes.size() - 1).getCoordinate();
	}

	public void remove(int index)
	{
		Node node = nodes.get(index);

		// If the removed node is a start- or endpoint, update the
		// endpointChain-list of the node
		if (getFirstNode() == node) {
			if (getNumberOfNodes() > 1) {
				node.removeEndpointChain(this);
			}
			if (getNumberOfNodes() > 2) {
				getNode(1).addEndpointChain(this);
			}
		} else if (getLastNode() == node) {
			if (getNumberOfNodes() > 1) {
				node.removeEndpointChain(this);
			}
			if (getNumberOfNodes() > 2) {
				getNode(getNumberOfNodes() - 2).addEndpointChain(this);
			}
		}

		nodes.remove(index);

		if (nodes.size() < 3 && closed) {
			closed = false;
		}
	}

	public void remove(Node node)
	{
		while (nodes.contains(node)) {
			int index = nodes.indexOf(node);
			nodes.remove(index);
		}
	}

	public void removeFirstPoint()
	{
		remove(0);
	}

	public void removeLastPoint()
	{
		remove(nodes.size() - 1);
	}

	public List<Polygon> getPolygons()
	{
		return polygons;
	}

	public void addPolygon(Polygon polygon)
	{
		polygons.add(polygon);
	}

	public void removePolygon(Polygon polygon)
	{
		polygons.remove(polygon);
	}

	public Geometry createGeometry()
	{
		int n = nodes.size();

		GeometryFactory factory = new GeometryFactory();
		JtsUtil jts = new JtsUtil();
		if (n == 0) {
			Point point = factory
					.createPoint(jts.toJts(nodes.get(0).getCoordinate()));
			return point;
		}
		com.vividsolutions.jts.geom.Coordinate[] coords = new com.vividsolutions.jts.geom.Coordinate[n];
		for (int i = 0; i < n; i++) {
			coords[i] = jts.toJts(nodes.get(i).getCoordinate());
		}
		LineString line = factory.createLineString(coords);
		return line;
	}

	public boolean hasPointWithinThreshold(Coordinate coordinate,
			double threshold)
	{
		for (Node n : nodes) {
			if (coordinate.distance(n.getCoordinate()) < threshold) {
				return true;
			}
		}
		return false;
	}

	public int getNearestPointWithinThreshold(Coordinate coordinate,
			double threshold)
	{
		int p = -1;
		double distance = 0;
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			double cdist = coordinate.distance(n.getCoordinate());
			if (cdist < threshold) {
				if (p == -1 || cdist < distance) {
					p = i;
					distance = cdist;
				}
			}
		}
		return p;
	}

	public static Chain fromLineString(Geometry geometry)
	{
		if (!(geometry instanceof LineString)) {
			return null;
		}
		Chain chain = new Chain();
		LineString string = (LineString) geometry;
		for (int i = 0; i < string.getNumPoints(); i++) {
			com.vividsolutions.jts.geom.Coordinate cn = string
					.getCoordinateN(i);
			chain.appendPoint(new Coordinate(cn.x, cn.y));
		}
		return chain;
	}

	public void setClosed(boolean closed) throws CloseabilityException
	{
		if (nodes.size() < 3 && closed) {
			throw new CloseabilityException(
					"invalid number of coordinates: need at least 3 coordinates to close a geometry");
		}
		this.closed = closed;
	}

	public void setOpen()
	{
		this.closed = false;
	}

	public boolean isClosed()
	{
		return closed;
	}

	public double distance(Coordinate c)
	{
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			double d = node.getCoordinate().distance(c);
			double d2 = d * d;
			if (d2 < distance) {
				distance = d2;
			}
		}
		for (int i = 0; i < nodes.size() - 1; i++) {
			Node n1 = nodes.get(i);
			Node n2 = nodes.get(i + 1);
			double d2 = GeomMath.squaredDistance(c, n1.getCoordinate(),
					n2.getCoordinate());
			if (d2 < distance) {
				distance = d2;
			}
		}
		if (isClosed()) {
			Node n1 = nodes.get(nodes.size() - 1);
			Node n2 = nodes.get(0);
			double d2 = GeomMath.squaredDistance(c, n1.getCoordinate(),
					n2.getCoordinate());
			if (d2 < distance) {
				distance = d2;
			}
		}
		return Math.sqrt(distance);
	}

	public Node getNearestPoint(Coordinate coordinate)
	{
		Node nearest = null;
		double distance = Double.MAX_VALUE;
		for (Node node : nodes) {
			double d = node.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearest = node;
			}
		}
		return nearest;
	}

	public Node getNearestDifferentNode(Node n)
	{
		Coordinate coordinate = n.getCoordinate();
		Node nearest = null;
		double distance = Double.MAX_VALUE;
		for (Node node : nodes) {
			if (node == n) {
				continue;
			}
			double d = node.getCoordinate().distance(coordinate);
			if (d < distance) {
				distance = d;
				nearest = node;
			}
		}
		return nearest;
	}

	public void replaceNode(Node old, Node n)
	{
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) == old) {
				nodes.set(i, n);
				break;
			}
		}
	}

	public Chain splitAtNode(Node node)
	{
		// TODO: what if it is contained twice?
		// TODO: what if the node is part of another chain?
		if (closed) {
			if (polygons.size() > 0) {
				logger.error("unable to split ring used in a polygon");
				return null;
			}
			if (!nodes.contains(node)) {
				return null;
			}
			// It won't be closed afterwards
			closed = false;
			// Catch some special cases. The node to delete is the first or the
			// last one
			if (getFirstNode() == node) {
				nodes.remove(0);
				if (nodes.size() > 1) {
					nodes.get(0).addEndpointChain(this);
				}
				return null;
			} else if (getLastNode() == node) {
				nodes.remove(nodes.size() - 1);
				if (nodes.size() > 1) {
					nodes.get(nodes.size() - 1).addEndpointChain(this);
				}
				return null;
			}
			// Now handle the normal case where it is somewhere in between
			nodes.get(0).removeEndpointChain(this);
			nodes.get(nodes.size() - 1).removeEndpointChain(this);
			List<Node> oldNodes = new ArrayList<>(nodes);
			int n = nodes.size();
			int index = nodes.indexOf(node);
			nodes.clear();
			IntRing ring = new IntRing(n, index);
			ring.next();
			for (int i = 0; i < n - 1; i++) {
				nodes.add(oldNodes.get(ring.value()));
				ring.next();
			}
			nodes.get(0).addEndpointChain(this);
			nodes.get(nodes.size() - 1).addEndpointChain(this);
			return null;
		} else {
			if (!nodes.contains(node)) {
				return null;
			}
			// Catch some special cases. The node to delete is the first or the
			// last one
			if (getFirstNode() == node) {
				nodes.remove(0);
				if (nodes.size() > 1) {
					nodes.get(1).addEndpointChain(this);
				}
				return null;
			} else if (getLastNode() == node) {
				nodes.remove(nodes.size() - 1);
				if (nodes.size() > 1) {
					nodes.get(nodes.size() - 1).addEndpointChain(this);
				}
				return null;
			}
			// Now handle the normal case where it is somewhere in between
			int n = nodes.size();
			int index = nodes.indexOf(node);
			Chain chain = new Chain();
			for (int i = index + 1; i < n; i++) {
				chain.nodes.add(nodes.get(i));
			}
			for (int i = n - 1; i >= index; i--) {
				Node moved = nodes.get(i);
				moved.removeChain(this);
				moved.addChain(chain);
				nodes.remove(i);
			}
			if (nodes.size() > 1) {
				nodes.get(index - 1).addEndpointChain(this);
			}
			if (chain.nodes.size() > 1) {
				chain.nodes.get(0).addEndpointChain(chain);
			}
			return chain;
		}
	}
}
