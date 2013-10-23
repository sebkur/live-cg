package de.topobyte.livecg.algorithms.voronoi.fortune.events;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;

public class EventPoint extends Point
{

	public EventPoint(Point point)
	{
		super(point);
	}

	public EventPoint(double x, double y)
	{
		super(x, y);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + super.toString();
	}

}
