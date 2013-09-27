package fortune.sweep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.Stack;
import fortune.sweep.arc.ArcNode;
import fortune.sweep.arc.ArcTree;
import fortune.sweep.events.CirclePoint;
import fortune.sweep.events.EventPoint;
import fortune.sweep.events.EventQueueModification;
import fortune.sweep.events.EventQueueModification.Type;
import fortune.sweep.events.HistoryEventQueue;
import fortune.sweep.events.SitePoint;
import fortune.sweep.geometry.Edge;
import fortune.sweep.geometry.Point;

public class Algorithm
{
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
	private ArcTree arcs = new ArcTree();

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

	public void addSite(Point point)
	{
		boolean inserted = events.insertEvent(new SitePoint(point));
		if (inserted) {
			sites.add(point);
			voronoi.addSite(point);
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

	public ArcTree getArcs()
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
		arcs.clear();
		events.clear();
		executedEvents = new Stack<EventPoint>();
		currentEvent = null;
		voronoi.clear();
		delaunay = new Delaunay();
		for (Point point : sites) {
			boolean inserted = events.insertEvent(new SitePoint(point));
			if (inserted) {
				voronoi.addSite(point);
			}
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

		restoreEventQueue(xPosBefore);

		/*
		 * Go through executed events and revert everything within the interval
		 */
		while (executedEvents.size() > 0) {
			EventPoint lastEvent = executedEvents.top();
			if (!(lastEvent.getX() >= sweepX && lastEvent.getX() <= xPosBefore)) {
				break;
			}
			executedEvents.pop();
			if (lastEvent instanceof SitePoint) {
				SitePoint sitePoint = (SitePoint) lastEvent;
				revert(sitePoint);
			} else if (lastEvent instanceof CirclePoint) {
				CirclePoint circlePoint = (CirclePoint) lastEvent;
				revert(circlePoint);
			}
		}

		notifyWatchers();
		return sweepX > 0;
	}

	private void restoreEventQueue(double xPosBefore)
	{
		/*
		 * Restore event queue
		 */
		while (events.hasModification()) {
			EventQueueModification mod = events.getLatestModification();
			EventPoint event = events.getLatestModification().getEventPoint();
			if (event instanceof SitePoint) {
				if (!(mod.getX() >= sweepX && mod.getX() <= xPosBefore)) {
					break;
				}
			} else if (event instanceof CirclePoint) {
				if (mod.getType() == Type.REMOVE) {
					if (!(mod.getX() >= sweepX && mod.getX() <= xPosBefore)) {
						break;
					}
				} else if (mod.getType() == Type.ADD) {
					if (!(mod.getX() >= sweepX && mod.getX() <= xPosBefore)) {
						break;
					}
				}
			}
			events.revertModification();
		}
	}

	public synchronized void setSweep(double x)
	{
		if (sweepX < x) {
			moveForward(x - sweepX);
		} else if (sweepX > x) {
			moveBackward(sweepX - x);
		}

		notifyWatchers();
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
		notifyWatchers();
	}

	public void previousEvent()
	{
		if (executedEvents.isEmpty()) {
			// If we are before the first event but after 0, just go to 0.
			if (sweepX > 0) {
				sweepX = 0;
				notifyWatchers();
			}
			return;
		}

		double xPosBefore = sweepX;

		EventPoint point = executedEvents.pop();
		if (sweepX > point.getX()) {
			// If we are beyond some event
			sweepX = point.getX();
			restoreEventQueue(xPosBefore);
		} else if (sweepX == point.getX()) {
			// If we are exactly at some event
			restoreEventQueue(xPosBefore);

			if (point instanceof SitePoint) {
				SitePoint sitePoint = (SitePoint) point;
				revert(sitePoint);
			} else if (point instanceof CirclePoint) {
				CirclePoint circlePoint = (CirclePoint) point;
				revert(circlePoint);
			}
			if (executedEvents.isEmpty()) {
				point = null;
			} else {
				point = executedEvents.pop();
				sweepX = point.getX();
				restoreEventQueue(xPosBefore);
			}
		}
		if (point == null) {
			// If no executed events are left, we go to 0
			sweepX = 0;
			currentEvent = null;
		} else {
			// Revert the event that we just arrived at
			if (point instanceof SitePoint) {
				SitePoint sitePoint = (SitePoint) point;
				revert(sitePoint);
			} else if (point instanceof CirclePoint) {
				CirclePoint circlePoint = (CirclePoint) point;
				revert(circlePoint);
			}
			// Replay the event that we just arrived at
			events.pop();
			process(point);
			currentEvent = point;
		}
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
		// Remember that this event has been executed
		executedEvents.push(eventPoint);

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
		getArcs().insert(sitePoint, getSweepX(), getEventQueue());
	}

	private void revert(SitePoint sitePoint)
	{
		getArcs().remove(sitePoint);
	}

	// Circle events

	private void process(CirclePoint circlePoint)
	{
		// arc is the disappearing arc
		ArcNode arc = circlePoint.getArc();
		// prev and next are the new neighbors on the beachline
		ArcNode prev = arc.getPrevious();
		ArcNode next = arc.getNext();
		// point is the position of the new voronoi vertex
		Point point = new Point(circlePoint.getX() - circlePoint.getRadius(),
				circlePoint.getY());
		// Add two new voronoi edges
		prev.completeTrace(this, point);
		arc.completeTrace(this, point);
		// Add a new trace
		prev.setStartOfTrace(point);
		// Change arc pointers
		prev.setNext(next);
		next.setPrevious(prev);
		// Dismiss now invalid circle events
		if (prev.getCirclePoint() != null) {
			getEventQueue().remove(prev.getCirclePoint());
			prev.setCirclePoint(null);
		}
		if (next.getCirclePoint() != null) {
			getEventQueue().remove(next.getCirclePoint());
			next.setCirclePoint(null);
		}
		// Check for new circle events
		prev.checkCircle(getEventQueue());
		next.checkCircle(getEventQueue());
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
		// Remove edge from delaunay triangulation. Remove each each twice with
		// inverted coordinates to make sure equals() works with one of them.
		delaunay.remove(new Edge(arc, arc.getPrevious()));
		delaunay.remove(new Edge(arc, arc.getNext()));
		delaunay.remove(new Edge(arc.getPrevious(), arc));
		delaunay.remove(new Edge(arc.getNext(), arc));
	}

}
