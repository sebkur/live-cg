package fortune.sweep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fortune.sweep.geometry.Edge;
import fortune.sweep.geometry.Point;

public class Voronoi
{
	private List<Point> sites = new ArrayList<Point>();
	private List<Edge> edges = new ArrayList<Edge>();
	private Map<Point, List<Edge>> pointToEdges = new HashMap<Point, List<Edge>>();

	public Voronoi()
	{
		checkDegenerate();
	}

	public List<Point> getSites()
	{
		return sites;
	}

	public List<Edge> getEdges()
	{
		return edges;
	}

	public void checkDegenerate()
	{
		if (sites.size() > 1) {
			Point min = sites.get(0), next = min;
			for (int i = 1; i < sites.size(); i++) {
				Point element = sites.get(i);
				if (element.getX() <= min.getX()) {
					next = min;
					min = element;
				} else if (element.getX() <= min.getX()) {
					next = element;
				}
			}

			if (min.getX() == next.getX() && min != next) {
				min.setX(min.getX() - 1);
				System.out.println("Moved point: " + next.getX() + " -> "
						+ min.getX());
			}
		}
	}

	public void clear()
	{
		edges.clear();
	}

	public void addSite(Point site)
	{
		sites.add(site);
	}

	public Point getSite(int i)
	{
		return sites.get(i);
	}

	public int getNumberOfSites()
	{
		return sites.size();
	}

	public void addLine(Edge edge)
	{
		edges.add(edge);
		List<Edge> start = pointToEdges.get(edge.getStart());
		if (start == null) {
			start = new ArrayList<Edge>();
			pointToEdges.put(edge.getStart(), start);
		}
		start.add(edge);
		List<Edge> end = pointToEdges.get(edge.getEnd());
		if (end == null) {
			end = new ArrayList<Edge>();
			pointToEdges.put(edge.getEnd(), end);
		}
		end.add(edge);
	}

	public void removeLinesFromVertex(Point point)
	{
		List<Edge> edges = pointToEdges.get(point);
		for (Edge edge : edges) {
			this.edges.remove(edge);
		}
	}
}
