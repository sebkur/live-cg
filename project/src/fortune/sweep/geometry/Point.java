package fortune.sweep.geometry;

public class Point
{

	private double x, y;

	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Point(Point point)
	{
		x = point.x;
		y = point.y;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double distance(Point point)
	{
		double dx = point.x - x;
		double dy = point.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof Point)) {
			return false;
		}
		Point o = (Point) other;
		return o.getX() == getX() && o.getY() == getY();
	}

	@Override
	public String toString()
	{
		return x + ", " + y;
	}

	@Override
	public int hashCode()
	{
		long bitsX = Double.doubleToLongBits(x);
		long bitsY = Double.doubleToLongBits(x);
		long bits = bitsX + bitsY;
		return (int) (bits ^ (bits >>> 32));
	}

}
