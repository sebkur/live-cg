package fortune.sweep.geometry;

public class Edge
{

	private Point p1, p2;

	public Edge(Point p1, Point p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point getStart()
	{
		return p1;
	}

	public Point getEnd()
	{
		return p2;
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof Edge)) {
			return false;
		}
		Edge edge = (Edge) other;
		return edge.p1.equals(p1) && edge.p2.equals(p2);
	}

}
