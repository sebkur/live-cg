package de.topobyte.livecg.algorithms.voronoi.fortune.arc;

import de.topobyte.livecg.algorithms.voronoi.fortune.events.HistoryEventQueue;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.core.geometry.dcel.DCEL;

public class ArcTree
{

	private ArcNode arcs;

	public boolean insert(Point point, double sweepX,
			HistoryEventQueue eventQueue, DCEL dcel)
	{
		if (arcs == null) {
			arcs = new ArcNode(point);
			return true;
		}
		return false;
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
