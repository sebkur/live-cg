package de.topobyte.livecg.algorithms.voronoi.fortune.ui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.topobyte.livecg.algorithms.dcel.DcelConfig;
import de.topobyte.livecg.algorithms.dcel.DcelPainter;
import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.Delaunay;
import de.topobyte.livecg.algorithms.voronoi.fortune.Voronoi;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.AbstractArcNodeVisitor;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ArcNode;
import de.topobyte.livecg.algorithms.voronoi.fortune.arc.ArcNodeWalker;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.CirclePoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventPoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventQueue;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Edge;
import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;

public class FortunePainter extends BasicAlgorithmPainter
{

	private Algorithm algorithm;
	private Config config;
	private DcelPainter dcelPainter;

	public FortunePainter(Algorithm algorithm, Config config, Painter painter)
	{
		super(painter);
		this.algorithm = algorithm;
		this.config = config;
		DcelConfig dcelConfig = new DcelConfig();
		dcelPainter = new DcelPainter(dcelConfig, painter) {

			@Override
			public DCEL getDcel()
			{
				return FortunePainter.this.algorithm.getVoronoi().getDcel();
			}

		};
	}

	private String q(String property)
	{
		return "algorithm.voronoi.fortune.colors." + property;
	}

	private Color COLOR_BG = LiveConfig.getColor(q("background"));
	private Color COLOR_SWEEPLINE = LiveConfig.getColor(q("sweepline"));

	private Color COLOR_SITES = LiveConfig.getColor(q("sites"));
	private Color COLOR_SITES_VISITED = LiveConfig.getColor(q("sites.done"));
	private Color COLOR_SITES_ACTIVE = LiveConfig.getColor(q("sites.active"));
	private Color COLOR_CIRCLE_EVENT_POINTS = LiveConfig
			.getColor(q("circle.event.point"));
	private Color COLOR_CIRCLE_EVENT_POINTS_ACTIVE = LiveConfig
			.getColor(q("circle.event.point.active"));
	private Color COLOR_BEACHLINE_INTERSECTIONS = LiveConfig
			.getColor(q("beachline.intersections"));
	private Color COLOR_SPIKES = LiveConfig.getColor(q("spikes"));
	private Color COLOR_SPIKE_INTERSECTIONS = LiveConfig
			.getColor(q("spike.intersections"));

	private Color COLOR_VORONOI_SEGMENTS = LiveConfig
			.getColor(q("voronoi.segments"));
	private Color COLOR_VORONOI_TRACES = LiveConfig
			.getColor(q("voronoi.traces"));
	private Color COLOR_ARCS = LiveConfig.getColor(q("arcs"));
	private Color COLOR_CIRCLES = LiveConfig.getColor(q("circles"));
	private Color COLOR_DELAUNAY = LiveConfig.getColor(q("delaunay"));

	@Override
	public void setPainter(Painter painter)
	{
		super.setPainter(painter);
		dcelPainter.setPainter(painter);
	}

	@Override
	public void paint()
	{
		painter.setColor(COLOR_BG);
		painter.fillRect(0, 0, width, height);

		if (config.isDrawDcel()) {
			dcelPainter.paint();
		}

		paintSites(algorithm.getVoronoi());

		if (config.isDrawVoronoiLines() && !config.isDrawDcel()) {
			paintVoronoiEdges(algorithm.getVoronoi());
		}

		painter.setColor(COLOR_SWEEPLINE);
		painter.drawLine(algorithm.getSweepX(), 0, algorithm.getSweepX(),
				height);

		if (algorithm.getEventQueue() != null) {
			paintEventQueue(algorithm.getEventQueue(), config.isDrawCircles());
		}

		if (algorithm.getArcs() != null) {
			paintArcs(algorithm.getArcs(), algorithm.getSweepX());
		}

		if (algorithm.getCurrentEvent() != null) {
			paintEventPoint(algorithm.getCurrentEvent(),
					config.isDrawCircles(), true);
		}

		if (config.isDrawDelaunay()) {
			paintDelaunay(algorithm.getDelaunay());
		}
	}

	private void paintSites(Voronoi v)
	{
		painter.setColor(COLOR_SITES_VISITED);
		List<Point> sites = v.getSites();
		for (Point p : sites) {
			if (p.getX() < algorithm.getSweepX()) {
				painter.fillCircle(p.getX(), p.getY(), 3.5);
			}
		}
	}

	private void paintDelaunay(Delaunay d)
	{
		painter.setColor(COLOR_DELAUNAY);
		for (int i = 0; i < d.size(); i++) {
			Point p1 = d.get(i).getStart();
			Point p2 = d.get(i).getEnd();
			painter.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		}
	}

	private void paintVoronoiEdges(Voronoi v)
	{
		List<Edge> edges = v.getEdges();
		painter.setColor(COLOR_VORONOI_SEGMENTS);
		for (int i = 0; i < edges.size(); i++) {
			Point p1 = edges.get(i).getStart();
			Point p2 = edges.get(i).getEnd();
			painter.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
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

				painter.setColor(COLOR_CIRCLES);
				painter.drawCircle(cp.getX() - cp.getRadius(), cp.getY(),
						cp.getRadius());

				if (isActive) {
					painter.setColor(COLOR_CIRCLE_EVENT_POINTS_ACTIVE);
				} else {
					painter.setColor(COLOR_CIRCLE_EVENT_POINTS);
				}
				painter.fillCircle(eventPoint.getX(), eventPoint.getY(), 3.5);
			} else {
				if (isActive) {
					painter.setColor(COLOR_SITES_ACTIVE);
				} else {
					painter.setColor(COLOR_SITES);
				}
				painter.fillCircle(eventPoint.getX(), eventPoint.getY(), 3.5);
			}
		}
	}

	private void paintArcs(ArcNode arcNode, double sweepX)
	{
		ArcNodeWalker.walk(new AbstractArcNodeVisitor() {

			@Override
			public void arc(ArcNode current, ArcNode next, double y1,
					double y2, double sweepX)
			{
				if (config.isDrawVoronoiLines() && !config.isDrawDcel()) {
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
				if (config.isDrawBeach() || config.isDrawVoronoiLines()
						|| config.isDrawDcel()) {
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
		painter.setColor(COLOR_SPIKES);
		painter.drawLine(beachlineX, point.getY(), sweepX, point.getY());

		// snip debug: red dot where spike meets beachline
		painter.setColor(COLOR_SPIKE_INTERSECTIONS);
		painter.fillCircle(beachlineX, point.getY(), 2.5);
		// snap debug
	}

	private void paintBeachlineArc(double yTop, double yBottom,
			ArcNode current, double sweepX)
	{
		painter.setColor(COLOR_ARCS);
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
			painter.setColor(COLOR_VORONOI_TRACES);
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
			painter.setColor(COLOR_BEACHLINE_INTERSECTIONS);
			painter.fillCircle(beachX, beachY, 2.5);
			// snap debug
		}
	}

}
