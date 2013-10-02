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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
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
	}
}
