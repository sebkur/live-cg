/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.circular.IntRing;

public class Info
{

	static final Logger logger = LoggerFactory.getLogger(Info.class);

	private Polygon polygon;

	private int minIndex = -1;
	private int maxIndex = -1;

	List<Node> nodes = new ArrayList<Node>();
	Map<Node, Side> side = new HashMap<Node, Side>();

	Info(Polygon polygon)
	{
		this.polygon = polygon;
	}

	void prepare()
	{
		// Determine top and bottom nodes
		determineTopBottom();

		// Merge chains by traversing left and right chain
		mergeChains();

		// Store left / right chain information
		storeSideInfo();
	}

	void storeSideInfo()
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

	void determineTopBottom()
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

	void mergeChains()
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

}
