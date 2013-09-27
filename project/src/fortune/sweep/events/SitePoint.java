package fortune.sweep.events;

import fortune.sweep.geometry.Point;

public class SitePoint extends EventPoint
{

	public SitePoint(Point point)
	{
		super(point);
	}

	public SitePoint(double x, double y)
	{
		super(x, y);
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof SitePoint)) {
			return false;
		}
		SitePoint o = (SitePoint) other;
		return o.getX() == getX() && o.getY() == getY();
	}
}
