package data.pointset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointSet
{

	private List<Point> points = new ArrayList<Point>();

	public void add(Point point)
	{
		points.add(point);
	}

	public List<Point> getPoints()
	{
		return Collections.unmodifiableList(points);
	}

}
