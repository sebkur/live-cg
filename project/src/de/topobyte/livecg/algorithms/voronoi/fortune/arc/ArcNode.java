package de.topobyte.livecg.algorithms.voronoi.fortune.arc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.CirclePoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.HistoryEventQueue;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Edge;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
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
	private Stack<Point> startOfTraceBackup = new Stack<Point>();
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

	public void checkCircle(HistoryEventQueue eventQueue)
	{
		if (prev != null && next != null) {
			circlePoint = calculateCenter(next, this, prev);
			if (circlePoint != null) {
				eventQueue.insertEvent(circlePoint);
			}
		}
	}

	public void removeCircle(HistoryEventQueue eventQueue)
	{
		if (circlePoint != null) {
			eventQueue.remove(circlePoint);
			circlePoint = null;
		}
	}

	public void completeTrace(Algorithm algorithm, Point point, CirclePoint circlePoint)
	{
		if (startOfTrace != null) {
			algorithm.getVoronoi().addLine(new Edge(startOfTrace, point));
			algorithm.getDelaunay().add(new Edge(this, next));
			startOfTraceBackup.push(startOfTrace);
			startOfTrace = null;
		}
	}

	public void uncompleteTrace()
	{
		startOfTrace = startOfTraceBackup.pop();
	}

	public void insert(ParabolaPoint parabolaPoint, double sweepX,
			HistoryEventQueue eventQueue, DCEL dcel) throws MathException
	{
		boolean split = true;
		if (next != null) {
			next.init(sweepX);
			if (sweepX > next.getX() && sweepX > getX()) {
				double xs[] = solveQuadratic(getA() - next.getA(), getB()
						- next.getB(), getC() - next.getC());
				if (xs[0] <= parabolaPoint.realX() && xs[0] != xs[1]) {
					split = false;
				}
			} else {
				split = false;
			}
		}

		if (split) {
			removeCircle(eventQueue);

			/*
			 * insert new arc and update pointers
			 */

			ArcNode arcnode = new ArcNode(parabolaPoint);
			arcnode.next = new ArcNode(this);
			arcnode.prev = this;
			arcnode.next.next = next;
			arcnode.next.prev = arcnode;

			if (next != null) {
				next.prev = arcnode.next;
			}

			next = arcnode;

			/*
			 * circle events
			 */

			checkCircle(eventQueue);
			next.next.checkCircle(eventQueue);

			/*
			 * traces
			 */

			next.next.startOfTrace = startOfTrace;
			Point start = new Point(sweepX - f(parabolaPoint.getY()),
					parabolaPoint.getY());
			startOfTrace = start;
			next.startOfTrace = start;

			/*
			 * DCEL
			 */
			
			v1 = new Vertex(new Coordinate(start.getX(), start.getY()), null);
			v2 = new Vertex(new Coordinate(start.getX(), start.getY()), null);
			a = new HalfEdge(v1, null, null, null, null);
			b = new HalfEdge(v2, null, null, null, null);
			a.setTwin(b);
			b.setTwin(a);
			a.setNext(b);
			a.setPrev(b);
			b.setNext(a);
			b.setPrev(a);
			dcel.vertices.add(v1);
			dcel.vertices.add(v2);
			dcel.halfedges.add(a);
			dcel.halfedges.add(b);
			
			logger.debug("create");
			logger.debug("a: " + a);
			logger.debug("b: " + b);
			
			next.next.edge = edge;
			edge = a;
			next.edge = b;
		} else {
			next.insert(parabolaPoint, sweepX, eventQueue, dcel);
		}
	}

	Vertex v1, v2;
	HalfEdge a, b;

	public void updateDcel(double y, double sweepX)
	{
		if (edge != null) {
			Vertex origin = edge.getOrigin();			
			double beachX = sweepX - f(y);
			origin.setCoordinate(new Coordinate(beachX, y));
		}
	}

}
