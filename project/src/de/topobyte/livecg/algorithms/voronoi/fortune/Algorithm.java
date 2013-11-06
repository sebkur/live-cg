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
package de.topobyte.livecg.algorithms.voronoi.fortune;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.arc.AbstractArcNodeVisitor;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ArcNode;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ArcNodeWalker;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.MathException;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ParabolaPoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.CirclePoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventPoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventQueueModification;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.HistoryEventQueue;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.SitePoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Edge;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelUtil;
import de.topobyte.livecg.core.geometry.dcel.HalfEdge;
import de.topobyte.livecg.core.geometry.dcel.Vertex;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.util.Stack;

public class Algorithm
{
	private Logger logger = LoggerFactory.getLogger(Algorithm.class);

	private static final int PLAY_N_PIXELS_BEYOND_SCREEN = 1000;

	/*
	 * Data structures to maintain voronoi diagram and delaunay triangulation.
	 */
	private Voronoi voronoi;
	private Delaunay delaunay;

	/*
	 * Current position of the sweepline.
	 */
	private double sweepX;

	/*
	 * Dimension of the area of interest.
	 */
	private int height;
	private int width;

	/*
	 * A list with the initial sites
	 */
	private List<Point> sites;

	/*
	 * Event queue with site and circle events plus a special pointer to the
	 * currently active event.
	 */
	private HistoryEventQueue events = new HistoryEventQueue(this);;
	private EventPoint currentEvent;

	/*
	 * This maintains a list of events that have been executed during the
	 * algorithm so that exactly these events may be reverted when playing the
	 * algorithm backwards.
	 */
	private Stack<EventPoint> executedEvents;

	/*
	 * The beachline data structure.
	 */
	private ArcNode arcs = null;

	/*
	 * Watchers that need to be notified once the algorithm moved to a new
	 * state.
	 */
	private List<AlgorithmWatcher> watchers = new ArrayList<AlgorithmWatcher>();

	public Algorithm()
	{
		sites = new ArrayList<Point>();
		voronoi = new Voronoi();
		init();
	}

	/*
	 * Public API
	 */

	public void addSite(Point point, boolean checkDegenerate)
	{
		events.insert(new SitePoint(point));
		sites.add(point);
		voronoi.addSite(point);
		if (checkDegenerate) {
			voronoi.checkDegenerate();
		}
	}

	public void setSites(List<Point> sites)
	{
		this.sites = sites;
		voronoi = new Voronoi();
		for (Point point : sites) {
			voronoi.addSite(point);
		}
		restart();
	}

	public void addWatcher(AlgorithmWatcher watcher)
	{
		watchers.add(watcher);
	}

	public void removeWatcher(AlgorithmWatcher watcher)
	{
		watchers.remove(watcher);
	}

	/*
	 * Dimension getters / setters
	 */

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	/*
	 * Various getters
	 */

	public double getSweepX()
	{
		return sweepX;
	}

	public Voronoi getVoronoi()
	{
		return voronoi;
	}

	public Delaunay getDelaunay()
	{
		return delaunay;
	}

	public HistoryEventQueue getEventQueue()
	{
		return events;
	}

	public EventPoint getCurrentEvent()
	{
		return currentEvent;
	}

	public ArcNode getArcs()
	{
		return arcs;
	}

	public List<Point> getSites()
	{
		return Collections.unmodifiableList(sites);
	}

	/*
	 * Internal methods
	 */

	private void notifyWatchers()
	{
		for (AlgorithmWatcher watcher : watchers) {
			watcher.update();
		}
	}

	private synchronized void init()
	{
		sweepX = 0;
		arcs = null;
		events.clear();
		executedEvents = new Stack<EventPoint>();
		currentEvent = null;
		voronoi.clear();
		delaunay = new Delaunay();
		for (Point point : sites) {
			events.insert(new SitePoint(point));
			voronoi.addSite(point);
		}
	}

	/*
	 * Sweepline control
	 */

	public synchronized boolean nextPixel()
	{
		return moveForward(1);
	}

	public synchronized boolean previousPixel()
	{
		return moveBackward(1);
	}

	public synchronized boolean moveForward(double amount)
	{
		sweepX += amount;
		currentEvent = null;

		double xPosOld = sweepX;
		while (events.size() != 0 && xPosOld >= events.top().getX()) {
			EventPoint eventPoint = events.pop();
			sweepX = eventPoint.getX();
			process(eventPoint);
			currentEvent = eventPoint;
		}
		sweepX = xPosOld;

		if (currentEvent != null && sweepX > currentEvent.getX()) {
			currentEvent = null;
		}

		initArcs(arcs, sweepX);

		notifyWatchers();
		return !isFinshed();
	}

	public synchronized boolean moveBackward(double amount)
	{
		if (sweepX <= 0) {
			return false;
		}
		double xPosBefore = sweepX;
		sweepX -= amount;
		currentEvent = null;

		/*
		 * Go through executed events and revert everything within the interval
		 */
		while (executedEvents.size() > 0) {
			EventPoint lastEvent = executedEvents.top();
			if (!(lastEvent.getX() >= sweepX && lastEvent.getX() <= xPosBefore)) {
				break;
			}
			events.insert(lastEvent);
			restoreEventQueue(lastEvent);
			executedEvents.pop();
			if (lastEvent instanceof SitePoint) {
				SitePoint sitePoint = (SitePoint) lastEvent;
				revert(sitePoint);
			} else if (lastEvent instanceof CirclePoint) {
				CirclePoint circlePoint = (CirclePoint) lastEvent;
				revert(circlePoint);
			}
		}

		initArcs(arcs, sweepX);

		notifyWatchers();
		return sweepX > 0;
	}

	private void restoreEventQueue(EventPoint eventPoint)
	{
		List<EventQueueModification> modifications = events
				.getModifications(eventPoint);
		if (modifications == null) {
			return;
		}
		for (EventQueueModification modification : modifications) {
			events.revertModification(modification);
		}
	}

	public synchronized void setSweep(double x)
	{
		if (sweepX < x) {
			moveForward(x - sweepX);
		} else if (sweepX > x) {
			moveBackward(sweepX - x);
		}
	}

	public synchronized boolean isFinshed()
	{
		return !(events.size() != 0 || sweepX < PLAY_N_PIXELS_BEYOND_SCREEN
				+ width);
	}

	public synchronized void nextEvent()
	{
		if (events.size() > 0) {
			EventPoint eventPoint = events.pop();
			sweepX = eventPoint.getX();
			process(eventPoint);
			currentEvent = eventPoint;
		} else if (sweepX < width) {
			sweepX = width;
			currentEvent = null;
		}

		initArcs(arcs, sweepX);

		notifyWatchers();
	}

	public synchronized void previousEvent()
	{
		if (executedEvents.isEmpty()) {
			// If we are before the first event but after 0, just go to 0.
			if (sweepX > 0) {
				sweepX = 0;
				notifyWatchers();
			}
			return;
		}

		// Examine the last executed event
		EventPoint point = executedEvents.top();
		if (sweepX > point.getX()) {
			// If we are beyond some event, move the sweepline to that event and
			// pretend that event had just happened
			sweepX = point.getX();
			currentEvent = point;
		} else if (sweepX == point.getX()) {
			// If we are exactly at some event, revert that event
			executedEvents.pop();
			if (point instanceof SitePoint) {
				SitePoint sitePoint = (SitePoint) point;
				revert(sitePoint);
			} else if (point instanceof CirclePoint) {
				CirclePoint circlePoint = (CirclePoint) point;
				revert(circlePoint);
			}
			events.insert(point);
			restoreEventQueue(point);
			// And go to the previous event
			if (executedEvents.isEmpty()) {
				// If no executed events are left, we go to 0
				sweepX = 0;
				currentEvent = null;
			} else {
				point = executedEvents.top();
				sweepX = point.getX();
				currentEvent = point;
			}
		}

		initArcs(arcs, sweepX);

		notifyWatchers();
	}

	public synchronized void clear()
	{
		sites = new ArrayList<Point>();
		voronoi = new Voronoi();
		restart();
	}

	public synchronized void restart()
	{
		init();
		notifyWatchers();
	}

	/*
	 * Internal event processing
	 */

	private void process(EventPoint eventPoint)
	{
		logger.debug("processing: " + eventPoint);

		// Remember that this event has been executed
		executedEvents.push(eventPoint);

		initArcs(arcs, sweepX);

		// Actually execute the event depending on its type
		if (eventPoint instanceof SitePoint) {
			SitePoint sitePoint = (SitePoint) eventPoint;
			process(sitePoint);
		} else if (eventPoint instanceof CirclePoint) {
			CirclePoint circlePoint = (CirclePoint) eventPoint;
			process(circlePoint);
		}
	}

	// Site events

	private void process(SitePoint sitePoint)
	{
		// Nothing special happens for the first arc
		if (arcs == null) {
			arcs = new ArcNode(sitePoint);
			return;
		}
		// Define a parabola for the new site
		ParabolaPoint parabolaPoint = new ParabolaPoint(sitePoint);
		parabolaPoint.init(sweepX);
		// Go through arcs
		ArcNode iter = arcs;
		while (iter != null) {
			ArcNode current = iter;
			ArcNode next = current.getNext();
			iter = iter.getNext();

			// If we reached the last arc, we insert there anyway (Also if the
			// node that created the last node has the same x-coordinate as this
			// node)
			if (next == null) {
				insert(sitePoint, current, parabolaPoint);
				break;
			}
			// We first check two cases with points that are not in general
			// position, in this context meaning that they have the same
			// x-coordinate. In both cases, there has already been another site
			// p with the same x-coordinate before this site. The arc of that
			// node cannot be split (first case). Also p splits its predecessor
			// in two arcs of which only the lower part is may be split by the
			// new site (second case).

			// @formatter:off
			// \
			//  \
			//   \
			//    |------------x <-- p (Don't split this arc)
			//   /
			//  /--------------x <-- new site
			// /
			// @formatter:on
			if (current.getX() == sitePoint.getX()) {
				continue;
			}

			// @formatter:off
			// \
			//  \ <-- Don't split this arc
			//   \
			//    |------------x <-- p
			//   /
			//  /--------------x <-- new site
			// /
			// @formatter:on
			if (next.getX() == sitePoint.getX()) {
				continue;
			}

			// Otherwise we examine the intersection with the arc.
			double xs[];
			try {
				xs = ParabolaPoint.solveQuadratic(current.getA() - next.getA(),
						current.getB() - next.getB(),
						current.getC() - next.getC());
				if (xs[0] <= parabolaPoint.realX() && xs[0] != xs[1]) {
					continue;
				}
			} catch (MathException e) {
				logger.error("Exception while calculating intersection");
				break;
			}

			// Continue with the subroutine
			insert(sitePoint, current, parabolaPoint);
			break;
		}
	}

	private void insert(SitePoint site, ArcNode splitArc,
			ParabolaPoint parabolaPoint)
	{
		// Behavior is different when the node that created the split arc has
		// the same x-coordinate as the new site. This only happens if there
		// sites with the same x-coordinate are the first sites, such that there
		// is no arc with an intersection at the moment of the event.
		boolean parallelSpikes = splitArc.getX() == parabolaPoint.getX();
		Point start;
		if (parallelSpikes) {
			// Create one new arc and insert it after the splitted arc
			ArcNode newArc = new ArcNode(parabolaPoint);
			splitArc.setNext(newArc);
			newArc.setPrevious(splitArc);

			// Create a supporting point at the left of the image
			start = new Point(0, (splitArc.getY() + parabolaPoint.getY()) / 2);
			splitArc.setStartOfTrace(start);
		} else {
			// Delete now invalid circle-event
			splitArc.removeCircle(site, events);

			// Insert new arc and update pointers
			ArcNode newArc = new ArcNode(parabolaPoint);
			newArc.setNext(new ArcNode(splitArc));
			newArc.setPrevious(splitArc);
			newArc.getNext().setNext(splitArc.getNext());
			newArc.getNext().setPrevious(newArc);
			if (splitArc.getNext() != null) {
				splitArc.getNext().setPrevious(newArc.getNext());
			}
			splitArc.setNext(newArc);

			// Check for new circle events
			splitArc.checkCircle(site, events);
			splitArc.getNext().getNext().checkCircle(site, events);

			// Create traces for voronoi edges
			splitArc.getNext().getNext()
					.setStartOfTrace(splitArc.getStartOfTrace());
			start = new Point(sweepX - splitArc.f(parabolaPoint.getY()),
					parabolaPoint.getY());
			splitArc.setStartOfTrace(start);
			splitArc.getNext().setStartOfTrace(start);
		}

		/*
		 * DCEL
		 */

		DCEL dcel = voronoi.getDcel();
		synchronized (dcel) {
			Vertex v1 = new Vertex(new Coordinate(start.getX(), start.getY()),
					null);
			Vertex v2 = new Vertex(new Coordinate(start.getX(), start.getY()),
					null);
			HalfEdge a = DcelUtil.createEdge(dcel, v1, v2, true, true);
			HalfEdge b = a.getTwin();

			logger.debug("create");
			logger.debug("a: " + a);
			logger.debug("b: " + b);

			if (!parallelSpikes) {
				splitArc.getNext().getNext()
						.setHalfedge(splitArc.getHalfedge());
			}
			splitArc.setHalfedge(a);
			splitArc.getNext().setHalfedge(b);

			if (parallelSpikes) {
				splitArc.getNext().setHalfedge(null);
			}
		}
	}

	private void revert(SitePoint sitePoint)
	{
		if (arcs == null) {
			return;
		}
		if (arcs.getNext() == null) {
			arcs = null;
			return;
		}
		ArcNode iter = arcs;
		while (iter != null) {
			if (iter.getX() == sitePoint.getX()
					&& iter.getY() == sitePoint.getY()) {
				revert(iter);
				break;
			}
			iter = iter.getNext();
		}
	}

	private void revert(ArcNode iter)
	{
		ArcNode prev = iter.getPrevious();
		ArcNode next = iter.getNext();
		boolean degenerate = iter.getX() == prev.getX();

		// Remove iter and next
		if (prev.equals(next)) {
			prev.setNext(next.getNext());
			if (prev.getNext() != null) {
				prev.getNext().setPrevious(prev);
			}
			prev.setStartOfTrace(next.getStartOfTrace());
		}

		if (degenerate) {
			prev.setNext(null);
		}

		// Update DCEL
		DCEL dcel = voronoi.getDcel();
		synchronized (dcel) {
			HalfEdge a = prev.getHalfedge();
			HalfEdge b = iter.getHalfedge();
			logger.debug("remove");
			logger.debug("a: " + a);
			logger.debug("b: " + b);
			if (degenerate) {
				prev.setHalfedge(null);
				dcel.getHalfedges().remove(a);
				dcel.getHalfedges().remove(a.getTwin());
				dcel.getVertices().remove(a.getOrigin());
				dcel.getVertices().remove(a.getTwin().getOrigin());
			} else {
				iter.setHalfedge(null);
				prev.setHalfedge(null);
				dcel.getHalfedges().remove(a);
				dcel.getVertices().remove(a.getOrigin());
				dcel.getHalfedges().remove(b);
				dcel.getVertices().remove(b.getOrigin());
				prev.setHalfedge(next.getHalfedge());
			}
		}
	}

	// Circle events

	private void process(CirclePoint circlePoint)
	{
		// arc is the disappearing arc
		final ArcNode arc = circlePoint.getArc();
		// prev and next are the new neighbors on the beachline
		final ArcNode prev = arc.getPrevious();
		final ArcNode next = arc.getNext();
		// point is the position of the new voronoi vertex
		Point point = new Point(circlePoint.getX() - circlePoint.getRadius(),
				circlePoint.getY());
		// Add two new voronoi edges
		prev.completeTrace(this, point, circlePoint);
		arc.completeTrace(this, point, circlePoint);
		// Add a new trace
		prev.setStartOfTrace(point);

		// Change arc pointers
		prev.setNext(next);
		next.setPrevious(prev);
		// Dismiss now invalid circle events
		if (prev.getCirclePoint() != null) {
			getEventQueue().remove(circlePoint, prev.getCirclePoint());
			prev.setCirclePoint(null);
		}
		if (next.getCirclePoint() != null) {
			getEventQueue().remove(circlePoint, next.getCirclePoint());
			next.setCirclePoint(null);
		}
		// Check for new circle events
		prev.checkCircle(circlePoint, getEventQueue());
		next.checkCircle(circlePoint, getEventQueue());

		ArcNodeWalker.walk(new AbstractArcNodeVisitor() {

			@Override
			public void arc(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				if (current == prev) {
					current.updateDcel(y2, sweepX);
				}
			}
		}, arcs, height, sweepX);

		// Get both halfedges starting at the voronoi vertex
		HalfEdge e1 = prev.getHalfedge();
		HalfEdge e2 = arc.getHalfedge();

		if (e1.getOrigin().getCoordinate()
				.distance(e2.getOrigin().getCoordinate()) > 0.0001) {
			logger.error("Meeting halfedges do not coincide in vertex.");
			logger.error("e1.origin: " + e1.getOrigin().getCoordinate());
			logger.error("e1.target: "
					+ e1.getTwin().getOrigin().getCoordinate());
			logger.error("e2.origin: " + e2.getOrigin().getCoordinate());
			logger.error("e2.target: "
					+ e2.getTwin().getOrigin().getCoordinate());
		}

		DCEL dcel = voronoi.getDcel();
		synchronized (dcel) {
			// Create a new vertex for the new trace
			Vertex v = new Vertex(new Coordinate(point.getX(), point.getY()),
					null);
			dcel.getVertices().add(v);
			// Create two new halfedges that connect v with the voronoi vertex
			HalfEdge a = DcelUtil.createEdge(dcel, v, e1.getOrigin(), false,
					false);
			HalfEdge b = a.getTwin();

			// Replace one of the old edges vertex with the other's vertex
			dcel.getVertices().remove(e2.getOrigin());
			e2.setOrigin(e1.getOrigin());

			// Connect new halfedges
			a.setPrev(b);
			b.setNext(a);
			// Connect old halfedges
			e2.getTwin().setNext(e1);
			e1.setPrev(e2.getTwin());
			// Connect new halfedges with the old ones
			a.setNext(e2);
			e2.setPrev(a);
			b.setPrev(e1.getTwin());
			e1.getTwin().setNext(b);

			// Update the arc's edge pointer
			prev.setHalfedge(a);
			logger.debug("connect");
			logger.debug("new: " + a);
			logger.debug("e1: " + e1);
			logger.debug("e2: " + e2);
		}
	}

	private void revert(CirclePoint circlePoint)
	{
		// Reinsert arc between previous and next
		ArcNode arc = circlePoint.getArc();
		arc.getNext().setPrevious(arc);
		arc.getPrevious().setNext(arc);
		// Restore trace starting at removed voronoi vertex
		Point point = new Point(circlePoint.getX() - circlePoint.getRadius(),
				circlePoint.getY());
		arc.uncompleteTrace();
		arc.getPrevious().uncompleteTrace();
		// Remove vertex/edges from voronoi diagram
		voronoi.removeLinesFromVertex(point);
		// Remove edge from delaunay triangulation. Remove each edge twice with
		// inverted coordinates to make sure equals() works with one of them.
		delaunay.remove(new Edge(arc, arc.getPrevious()));
		delaunay.remove(new Edge(arc, arc.getNext()));
		delaunay.remove(new Edge(arc.getPrevious(), arc));
		delaunay.remove(new Edge(arc.getNext(), arc));

		// Update DCEL
		DCEL dcel = voronoi.getDcel();
		synchronized (dcel) {
			HalfEdge edge = arc.getPrevious().getHalfedge();
			HalfEdge e1 = edge.getTwin().getPrev().getTwin();
			HalfEdge e2 = edge.getNext();
			logger.debug("disconnect");
			logger.debug("rem:" + edge);
			logger.debug("e1:" + e1);
			logger.debug("e2:" + e2);

			Coordinate origin = e2.getOrigin().getCoordinate();
			Vertex v = new Vertex(new Coordinate(origin.getX(), origin.getY()),
					null);
			e2.setOrigin(v);
			dcel.getVertices().add(v);

			dcel.getVertices().remove(edge.getOrigin());
			dcel.getHalfedges().remove(edge);
			dcel.getHalfedges().remove(edge.getTwin());

			arc.getPrevious().setHalfedge(e1);
			arc.setHalfedge(e2);

			e1.setPrev(e1.getTwin());
			e1.getTwin().setNext(e1);

			e2.setPrev(e2.getTwin());
			e2.getTwin().setNext(e2);
		}
	}

	private void initArcs(ArcNode arcNode, double sweepX)
	{
		for (ArcNode current = arcNode; current != null; current = current
				.getNext()) {
			current.init(sweepX);
		}

		ArcNodeWalker.walk(new AbstractArcNodeVisitor() {

			@Override
			public void spike(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				current.updateDcel(y2, sweepX);
			}

			@Override
			public void arc(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				current.updateDcel(y2, sweepX);
			}
		}, arcNode, height, sweepX);
	}

}
