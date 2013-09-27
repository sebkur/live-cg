package fortune.sweep.arc;

import util.Stack;
import fortune.sweep.Algorithm;
import fortune.sweep.events.CirclePoint;
import fortune.sweep.events.HistoryEventQueue;
import fortune.sweep.geometry.Edge;
import fortune.sweep.geometry.Point;

public class ArcNode extends ParabolaPoint
{
	private ArcNode next, prev;
	private CirclePoint circlePoint;
	private Point startOfTrace;
	private Stack<Point> startOfTraceBackup = new Stack<Point>();

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

	public void completeTrace(Algorithm algorithm, Point point)
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
			HistoryEventQueue eventQueue) throws MathException
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
			startOfTrace = new Point(sweepX - f(parabolaPoint.getY()),
					parabolaPoint.getY());
			next.startOfTrace = new Point(sweepX - f(parabolaPoint.getY()),
					parabolaPoint.getY());
		} else {
			next.insert(parabolaPoint, sweepX, eventQueue);
		}
	}

}
