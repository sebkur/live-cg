package de.topobyte.livecg.algorithms.voronoi.fortune.events;

import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ArcNode;

public class CirclePoint extends EventPoint
{

	private double radius;
	private ArcNode arc;

	public CirclePoint(double x, double y, ArcNode arcnode)
	{
		super(x, y);
		arc = arcnode;
		radius = distance(arcnode);
		setX(getX() + radius);
	}

	public double getRadius()
	{
		return radius;
	}

	public ArcNode getArc()
	{
		return arc;
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof CirclePoint)) {
			return false;
		}
		CirclePoint o = (CirclePoint) other;
		return o.getX() == getX() && o.getY() == getY();
	}
}
