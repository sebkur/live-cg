package fortune.sweep.arc;

import fortune.sweep.events.HistoryEventQueue;
import fortune.sweep.geometry.Point;

public class ArcTree
{

	private ArcNode arcs;

	public void insert(Point point, double sweepX, HistoryEventQueue eventQueue)
	{
		if (arcs == null) {
			arcs = new ArcNode(point);
			return;
		}
		try {
			ParabolaPoint parabolaPoint = new ParabolaPoint(point);
			parabolaPoint.init(sweepX);
			arcs.init(sweepX);
			arcs.insert(parabolaPoint, sweepX, eventQueue);
			return;
		} catch (MathException e) {
			System.out
					.println("*** error: No parabola intersection during ArcTree.insert()");
		}
	}

	public void remove(Point point)
	{
		int size = size();
		if (size == 0) {
			return;
		}
		if (size == 1) {
			arcs = null;
			return;
		}
		ArcNode iter = arcs;
		while (iter != null) {
			if (iter.getX() == point.getX() && iter.getY() == point.getY()) {
				ArcNode prev = iter.getPrevious();
				ArcNode next = iter.getNext();
				if (prev.equals(next)) {
					prev.setNext(next.getNext());
					if (prev.getNext() != null) {
						prev.getNext().setPrevious(prev);
					}
					prev.setStartOfTrace(next.getStartOfTrace());
				}
				break;
			}
			iter = iter.getNext();
		}
	}

	public ArcNode getArcs()
	{
		return arcs;
	}

	public int size()
	{
		if (arcs == null) {
			return 0;
		}
		ArcNode a = arcs;
		int size = 0;
		while (a != null) {
			size += 1;
			a = a.getNext();
		}
		return size;
	}

	public void clear()
	{
		arcs = null;
	}

}
