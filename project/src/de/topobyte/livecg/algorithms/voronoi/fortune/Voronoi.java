package de.topobyte.livecg.algorithms.voronoi.fortune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Edge;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.core.geometry.dcel.DCEL;

public class Voronoi
{
	final static Logger logger = LoggerFactory.getLogger(Voronoi.class);
	
	private List<Point> sites = new ArrayList<Point>();
	private List<Edge> edges = new ArrayList<Edge>();
	private Map<Point, List<Edge>> pointToEdges = new HashMap<Point, List<Edge>>();
	private DCEL dcel = new DCEL();

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

	public DCEL getDcel()
	{
		return dcel;
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
				logger.info("Moved point: " + next.getX() + " -> " + min.getX());
			}
		}
	}

	public void clear()
	{
		edges.clear();
		dcel.getVertices().clear();
		dcel.getHalfedges().clear();
		dcel.getFaces().clear();
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
