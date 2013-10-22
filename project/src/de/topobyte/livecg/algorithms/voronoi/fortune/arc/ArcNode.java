package de.topobyte.livecg.algorithms.voronoi.fortune.arc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.CirclePoint;
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

	public void completeTrace(Algorithm algorithm, Point point,
			CirclePoint circlePoint)
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

	public void updateDcel(double y, double sweepX)
	{
		if (edge != null) {
			Vertex origin = edge.getOrigin();
			double beachX = sweepX - f(y);
			origin.setCoordinate(new Coordinate(beachX, y));
		}
	}

}
