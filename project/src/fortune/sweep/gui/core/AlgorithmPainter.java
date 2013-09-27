package fortune.sweep.gui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fortune.sweep.Algorithm;
import fortune.sweep.Delaunay;
import fortune.sweep.Voronoi;
import fortune.sweep.arc.AbstractArcNodeVisitor;
import fortune.sweep.arc.ArcNode;
import fortune.sweep.arc.ArcNodeWalker;
import fortune.sweep.events.CirclePoint;
import fortune.sweep.events.EventPoint;
import fortune.sweep.events.EventQueue;
import fortune.sweep.geometry.Edge;
import fortune.sweep.geometry.Point;

public class AlgorithmPainter
{

	private Algorithm algorithm;
	private Config config;
	private Painter painter;

	private int width, height;

	public AlgorithmPainter(Algorithm algorithm, Config config, Painter painter)
	{
		this.algorithm = algorithm;
		this.config = config;
		this.painter = painter;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public void setPainter(Painter painter)
	{
		this.painter = painter;
	}

	private int colorBackground = 0xffffff;
	private int colorSweepline = 0xff0000;

	private int colorSites = 0x000000;
	private int colorSitesVisited = 0x666666;
	private int colorSiteActive = 0xff0000;
	private int colorCircleEventPoints = 0x00ff00;
	private int colorCircleEventPointsActive = 0xff0000;
	private int colorBeachlineIntersections = 0x00ff00;
	private int colorSpikes = 0x000000;
	private int colorSpikeIntersections = 0xff0000;

	private int colorVoronoiSegments = 0x0000ff;
	private int colorVoronoiTraces = 0xff0000;
	private int colorArcs = 0x000000;
	private int colorCircles = 0x000000;
	private int colorDelaunay = 0x999999;

	public void paint()
	{
		painter.setColor(new Color(colorBackground));
		painter.fillRect(0, 0, width, height);

		paintSitesAndEdges(algorithm.getVoronoi());

		painter.setColor(new Color(colorSweepline));
		painter.drawLine(algorithm.getSweepX(), 0, algorithm.getSweepX(),
				height);

		if (algorithm.getEventQueue() != null && algorithm.getArcs() != null) {
			paintEventQueue(algorithm.getEventQueue(), config.isDrawCircles());
			paintArcs(algorithm.getArcs().getArcs(), algorithm.getSweepX());
		}

		if (algorithm.getCurrentEvent() != null) {
			paintEventPoint(algorithm.getCurrentEvent(),
					config.isDrawCircles(), true);
		}

		if (config.isDrawDelaunay()) {
			paintDelaunay(algorithm.getDelaunay());
		}
	}

	private void paintDelaunay(Delaunay d)
	{
		painter.setColor(new Color(colorDelaunay));
		for (int i = 0; i < d.size(); i++) {
			Point p1 = d.get(i).getStart();
			Point p2 = d.get(i).getEnd();
			painter.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
	}

	private void paintSitesAndEdges(Voronoi v)
	{
		List<Point> sites = v.getSites();
		List<Edge> edges = v.getEdges();

		painter.setColor(new Color(colorSitesVisited));
		for (int i = 0; i < sites.size(); i++) {
			Point p = sites.get(i);
			painter.fillCircle(p.getX(), p.getY(), 3.5);
		}

		painter.setColor(new Color(colorVoronoiSegments));
		if (config.isDrawVoronoiLines()) {
			for (int i = 0; i < edges.size(); i++) {
				Point p1 = edges.get(i).getStart();
				Point p2 = edges.get(i).getEnd();
				painter.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
			}
		}
	}

	private void paintEventQueue(EventQueue queue, boolean drawCircles)
	{
		Iterator<EventPoint> iterator = queue.getCopy().iterator();
		while (iterator.hasNext()) {
			EventPoint eventPoint = iterator.next();
			paintEventPoint(eventPoint, drawCircles, false);
		}
	}

	private void paintEventPoint(EventPoint eventPoint, boolean drawCircles,
			boolean isActive)
	{
		if (drawCircles || !(eventPoint instanceof CirclePoint)) {
			if (eventPoint instanceof CirclePoint) {
				CirclePoint cp = (CirclePoint) eventPoint;

				painter.setColor(new Color(colorCircles));
				painter.drawCircle(cp.getX() - cp.getRadius(), cp.getY(),
						cp.getRadius());

				if (isActive) {
					painter.setColor(new Color(colorCircleEventPointsActive));
				} else {
					painter.setColor(new Color(colorCircleEventPoints));
				}
				painter.fillCircle(eventPoint.getX(), eventPoint.getY(), 3.5);
			} else {
				if (isActive) {
					painter.setColor(new Color(colorSiteActive));
				} else {
					painter.setColor(new Color(colorSites));
				}
				painter.fillCircle(eventPoint.getX(), eventPoint.getY(), 3.5);
			}
		}
	}

	private void paintArcs(ArcNode arcNode, double sweepX)
	{
		for (ArcNode current = arcNode; current != null; current = current
				.getNext()) {
			current.init(sweepX);
		}

		ArcNodeWalker.walk(new AbstractArcNodeVisitor() {

			@Override
			public void arc(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				if (config.isDrawVoronoiLines()) {
					paintTraces(y2, current, sweepX);
				}

				if (config.isDrawBeach()) {
					paintBeachlineArc(y1, y2, current, sweepX);
				}
			}
		}, arcNode, height, sweepX);

		ArcNodeWalker.walk(new AbstractArcNodeVisitor() {

			@Override
			public void arc(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				if (config.isDrawBeach() || config.isDrawVoronoiLines()) {
					paintBeachlineIntersections(y2, current, sweepX);
				}
			}
		}, arcNode, height, sweepX);

		ArcNodeWalker.walk(new AbstractArcNodeVisitor() {

			@Override
			public void spike(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				if (sweepX == current.getX()) {
					// spikes on site events
					if (config.isDrawBeach()) {
						paintSpike(sweepX, current, next);
					}
				}
			}
		}, arcNode, height, sweepX);

	}

	private void paintSpike(double sweepX, ArcNode point, ArcNode arc)
	{
		double beachlineX = arc != null ? sweepX - arc.f(point.getY()) : 0.0D;
		painter.setColor(new Color(colorSpikes));
		painter.drawLine(beachlineX, point.getY(), sweepX, point.getY());

		// snip debug: red dot where spike meets beachline
		painter.setColor(new Color(colorSpikeIntersections));
		painter.fillCircle(beachlineX, point.getY(), 2.5);
		// snap debug
	}

	private void paintBeachlineArc(double yTop, double yBottom,
			ArcNode current, double sweepX)
	{
		painter.setColor(new Color(colorArcs));
		// y stepping for parabola approximation
		int yStep = 3;
		// yMax: clamp yBottom between 0 and 'height'
		double yMax = Math.min(Math.max(0.0D, yBottom), height);
		// initialize x1 and y1 for yTop
		double x1 = sweepX - current.f(yTop);
		double y1 = yTop;
		// draw at least one segment to avoid gaps in corner cases
		boolean firstSegment = true;
		
		List<Coordinate> coords = new ArrayList<Coordinate>();
		coords.add(new Coordinate(x1, y1));
		// loop over y values
		for (double y2 = yTop + yStep; y2 < yMax || firstSegment; y2 += yStep) {
			firstSegment = false;
			// make last segment reach the beachline intersection
			if (y2 + yStep >= yMax) {
				y2 = yMax;
			}
			double x2 = sweepX - current.f(y2);
			if (y2 > yTop && (x1 >= 0.0D || x2 >= 0.0D)) {
				coords.add(new Coordinate(x2, y2));
			}
			// remember coordinates values for the next round
			x1 = x2;
			y1 = y2;
		}
		painter.drawPath(coords);
	}

	private void paintTraces(double beachY, ArcNode current, double sweepX)
	{
		Point startOfTrace = current.getStartOfTrace();
		if (startOfTrace != null) {
			double beachX = sweepX - current.f(beachY);
			painter.setColor(new Color(colorVoronoiTraces));
			painter.drawLine(startOfTrace.getX(), startOfTrace.getY(), beachX,
					beachY);
		}
	}

	private void paintBeachlineIntersections(double beachY, ArcNode current,
			double sweepX)
	{
		Point startOfTrace = current.getStartOfTrace();
		if (startOfTrace != null) {
			double beachX = sweepX - current.f(beachY);
			// snip debug: green dots where neighboring beachline arcs
			// intersect
			painter.setColor(new Color(colorBeachlineIntersections));
			painter.fillCircle(beachX, beachY, 2.5);
			// snap debug
		}
	}

}
