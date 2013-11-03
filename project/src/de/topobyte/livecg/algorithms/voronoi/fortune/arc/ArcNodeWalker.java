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
package de.topobyte.livecg.algorithms.voronoi.fortune.arc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcNodeWalker
{
	final static Logger logger = LoggerFactory.getLogger(ArcNodeWalker.class);

	public static void walk(ArcNodeVisitor visitor, ArcNode arcNode,
			double height, double sweepX)
	{
		double y1 = 0.0;
		double y2 = height;

		for (ArcNode current = arcNode; current != null; current = current
				.getNext()) {
			ArcNode next = current.getNext();

			if (sweepX == current.getX()) {
				// spikes on site events
				visitor.spike(current, next, y1, y2, sweepX);
				y2 = current.getY();
			} else {
				if (next == null) {
					y2 = height;
				} else {
					if (sweepX == next.getX()) {
						y2 = next.getY();
					} else {
						try {
							double ad[] = ParabolaPoint.solveQuadratic(
									current.getA() - next.getA(),
									current.getB() - next.getB(),
									current.getC() - next.getC());
							y2 = ad[0];
						} catch (MathException e) {
							y2 = y1;
							logger.error("No parabola intersection while painting arc - SLine: "
									+ sweepX
									+ ", "
									+ current.toString()
									+ " "
									+ next.toString());
						}
					}
				}
				// beachline arcs
				visitor.arc(current, next, y1, y2, sweepX);
			}

			y1 = Math.max(0.0D, y2);
		}
	}
}
