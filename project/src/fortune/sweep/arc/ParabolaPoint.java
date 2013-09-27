package fortune.sweep.arc;

import fortune.sweep.events.CirclePoint;
import fortune.sweep.geometry.Point;

public class ParabolaPoint extends Point
{

	private double a, b, c;

	public ParabolaPoint(Point point)
	{
		super(point);
	}

	public double realX()
	{
		return getY();
	}

	public double realY(double d)
	{
		return d - getX();
	}

	public double getA()
	{
		return a;
	}

	public double getB()
	{
		return b;
	}

	public double getC()
	{
		return c;
	}

	public CirclePoint calculateCenter(Point next, ArcNode arcnode,
			Point prev)
	{
		CirclePoint circlepoint = null;
		Point p1 = new Point(arcnode.getX() - next.getX(), arcnode.getY()
				- next.getY());
		Point p2 = new Point(prev.getX() - arcnode.getX(), prev.getY()
				- arcnode.getY());
		if (p2.getY() * p1.getX() > p2.getX() * p1.getY()) {
			double d = -p1.getX() / p1.getY();
			double d1 = (next.getY() + p1.getY() / 2D) - d
					* (next.getX() + p1.getX() / 2D);
			double d2 = -p2.getX() / p2.getY();
			double d3 = (arcnode.getY() + p2.getY() / 2D) - d2
					* (arcnode.getX() + p2.getX() / 2D);
			double cx;
			double cy;
			if (p1.getY() == 0.0D) {
				cx = next.getX() + p1.getX() / 2D;
				cy = d2 * cx + d3;
			} else if (p2.getY() == 0.0D) {
				cx = arcnode.getX() + p2.getX() / 2D;
				cy = d * cx + d1;
			} else {
				cx = (d3 - d1) / (d - d2);
				cy = d * cx + d1;
			}
			// cx, cy is the center of the circle through three points
			circlepoint = new CirclePoint(cx, cy, arcnode);
		}
		return circlepoint;
	}

	public void init(double d)
	{
		double d1 = realX();
		double d2 = realY(d);
		a = 1.0D / (2D * d2);
		b = -d1 / d2;
		c = (d1 * d1) / (2D * d2) + d2 / 2D;
	}

	public double f(double y)
	{
		return a * y * y + b * y + c;
	}

	public static double[] solveQuadratic(double da, double db, double dc)
			throws MathException
	{
		double ad[] = new double[2];
		double d3 = db * db - 4D * da * dc;
		if (d3 < 0.0D) {
			throw new MathException();
		}
		if (da == 0.0D) {
			if (db != 0.0D) {
				ad[0] = -dc / db;
			} else {
				throw new MathException();
			}
		} else {
			double d4 = Math.sqrt(d3);
			double d5 = -db;
			double d6 = 2D * da;
			ad[0] = (d5 + d4) / d6;
			ad[1] = (d5 - d4) / d6;
		}
		return ad;
	}

}
