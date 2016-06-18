/* This file is part of LiveCG.
 *
 * Copyright (C) 1997-1999 Pavel Kouznetsov
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

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.CirclePoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventPoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.HistoryEventQueue;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Edge;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.core.geometry.dcel.HalfEdge;
import de.topobyte.livecg.core.geometry.dcel.Vertex;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.util.Stack;

public class ArcNode extends ParabolaPoint
{
	final static Logger logger = LoggerFactory.getLogger(ArcNode.class);

	private ArcNode next, prev;
	private CirclePoint circlePoint;
	private Point startOfTrace;
	private Stack<Point> startOfTraceBackup = new Stack<>();
	private HalfEdge edge;

	public ArcNode(Point point)
	{
		super(point);
	}

	public ArcNode getNext()
	{
		return next;
	}

	public ArcNode getPrevious()
	{
		return prev;
	}

	public void setPrevious(ArcNode prev)
	{
		this.prev = prev;
	}

	public void setNext(ArcNode next)
	{
		this.next = next;
	}

	public Point getStartOfTrace()
	{
		return startOfTrace;
	}

	public CirclePoint getCirclePoint()
	{
		return circlePoint;
	}

	public void setCirclePoint(CirclePoint circlePoint)
	{
		this.circlePoint = circlePoint;
	}

	public void setStartOfTrace(Point startOfTrace)
	{
		this.startOfTrace = startOfTrace;
	}

	public HalfEdge getHalfedge()
	{
		return edge;
	}

	public void setHalfedge(HalfEdge edge)
	{
		this.edge = edge;
	}

	public void checkCircle(EventPoint reason, HistoryEventQueue eventQueue)
	{
		if (prev != null && next != null) {
			circlePoint = calculateCenter(next, this, prev);
			if (circlePoint != null) {
				eventQueue.insertEvent(reason, circlePoint);
			}
		}
	}

	public void removeCircle(EventPoint reason, HistoryEventQueue eventQueue)
	{
		if (circlePoint != null) {
			eventQueue.remove(reason, circlePoint);
			circlePoint = null;
		}
	}

	public void completeTrace(FortunesSweep algorithm, Point point,
			CirclePoint circlePoint)
	{
		if (startOfTrace != null) {
			algorithm.getVoronoi().addLine(new Edge(startOfTrace, point));
			startOfTraceBackup.push(startOfTrace);
			startOfTrace = null;
		}
	}

	public void uncompleteTrace()
	{
		startOfTrace = startOfTraceBackup.pop();
	}

	public void updateDcel(double y, double sweepX)
	{
		if (edge != null) {
			Vertex origin = edge.getOrigin();
			double beachX = sweepX - f(y);
			origin.setCoordinate(new Coordinate(beachX, y));
		}
	}

}
