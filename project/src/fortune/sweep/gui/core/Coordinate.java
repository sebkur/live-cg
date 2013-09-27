package fortune.sweep.gui.core;

public class Coordinate
{

	private double x, y;

	public Coordinate(double x, double y)
	{
		this.x = x;
		this.y = y;
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

	public double distance(Coordinate point)
	{
		double dx = point.x - x;
		double dy = point.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof Coordinate)) {
			return false;
		}
		Coordinate o = (Coordinate) other;
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
