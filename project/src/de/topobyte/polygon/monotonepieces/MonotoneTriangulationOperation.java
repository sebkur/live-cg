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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;

public class MonotoneTriangulationOperation
{

	final static Logger logger = LoggerFactory
			.getLogger(MonotoneTriangulationOperation.class);

	private Polygon polygon;

	public MonotoneTriangulationOperation(Polygon polygon)
	{
		this.polygon = polygon;

		// Determine top and bottom nodes
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		int minIndex = -1;
		int maxIndex = -1;
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

		// Merge chains by traversing left and right chain
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(shell.getNode(minIndex));
		IntRing left = new IntRing(shell.getNumberOfNodes(), minIndex).next();
		IntRing right = new IntRing(shell.getNumberOfNodes(), minIndex).prev();
		while (left.value() != maxIndex || right.value() != maxIndex) {
			if (left.value() == maxIndex) {
				logger.debug("Left chain fished");
				Coordinate c = shell.getCoordinate(right.value());
				logger.debug("Add: " + (right.value() + 1) + ": " + c.getY());
				right.prev();
				continue;
			} else if (right.value() == maxIndex) {
				logger.debug("Right chain fished");
				Coordinate c = shell.getCoordinate(left.value());
				logger.debug("Add: " + (left.value() + 1) + ": " + c.getY());
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
				left.next();
			} else {
				Coordinate c = shell.getCoordinate(right.value());
				logger.debug("Add: " + (right.value() + 1) + ": " + c.getY());
				right.prev();
			}
		}
		nodes.add(shell.getNode(maxIndex));
	}
}
