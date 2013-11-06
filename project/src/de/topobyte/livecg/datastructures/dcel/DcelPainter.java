/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.datastructures.dcel;

import java.awt.geom.GeneralPath;

import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.HalfEdge;
import de.topobyte.livecg.core.geometry.dcel.Vertex;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.lina.Matrix;
import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;
import de.topobyte.livecg.core.painting.TransformingAlgorithmPainter;
import de.topobyte.livecg.core.scrolling.TransformHelper;

public abstract class DcelPainter extends TransformingAlgorithmPainter
{

	private String q(String property)
	{
		return "datastructures.dcel.colors." + property;
	}

	private Color COLOR_BG = LiveConfig.getColor(q("background"));
	private Color COLOR_NODES = LiveConfig.getColor(q("nodes"));
	private Color COLOR_EDGES = LiveConfig.getColor(q("edges"));
	private Color COLOR_ARROWS = LiveConfig.getColor(q("arrows"));
	private Color COLOR_CONNECTORS = LiveConfig.getColor(q("connectors"));

	private DcelConfig config;

	public DcelPainter(Rectangle scene, DcelConfig config, Painter painter)
	{
		super(scene, painter);
		this.config = config;
	}

	public abstract DCEL getDcel();

	@Override
	public void paint()
	{
		preparePaint();

		synchronized (getDcel()) {
			double gap = 5;
			double shorten = 6;
			double markerLen = 12;
			double alpha = Math.PI / 8;
			double minArrowLen = 4;

			fillBackground(COLOR_BG);

			painter.setColor(COLOR_NODES);
			for (Vertex vertex : getDcel().getVertices()) {
				Coordinate c = transformer.transform(vertex.getCoordinate());
				painter.fillCircle(c.getX(), c.getY(), 4);
			}

			painter.setColor(COLOR_EDGES);
			for (HalfEdge halfedge : getDcel().getHalfedges()) {
				HalfEdge twin = halfedge.getTwin();
				Vertex origin = halfedge.getOrigin();
				Vertex destination = twin.getOrigin();
				Coordinate co = transformer.transform(origin.getCoordinate());
				Coordinate cd = transformer.transform(destination
						.getCoordinate());
				painter.drawLine(co.getX(), co.getY(), cd.getX(), cd.getY());
			}

			DCEL dcel = DcelUtil.clone(getDcel());
			Matrix matrix = TransformHelper.createMatrix(scene, this);
			DcelUtil.transform(dcel, matrix);

			for (HalfEdge halfedge : dcel.getHalfedges()) {
				HalfEdgeArrow arrow = new HalfEdgeArrow(halfedge, gap, shorten,
						markerLen, alpha);

				if (!arrow.isValid()) {
					continue;
				}
				painter.setStrokeWidth(1.0);
				painter.setColor(COLOR_ARROWS);
				drawLine(arrow.getOrigin(), arrow.getDestination());
				if (arrow.getLength() >= minArrowLen) {
					drawLine(arrow.getDestination(), arrow.getMarker());
				}

				if (config.isDrawConnectors()) {
					painter.setStrokeWidth(1.0);
					painter.setColor(COLOR_CONNECTORS);
					HalfEdge next = halfedge.getNext();
					if (next == null) {
						continue;
					}
					HalfEdgeArrow nextArrow = new HalfEdgeArrow(next, gap,
							shorten, markerLen, alpha);
					if (!nextArrow.isValid()) {
						continue;
					}
					// drawLine(arrow.getDestination(),
					// nextArrow.getOrigin());

					Vertex origin = halfedge.getOrigin();
					Vertex destination = halfedge.getTwin().getOrigin();
					Coordinate co = origin.getCoordinate();
					Coordinate cd = destination.getCoordinate();

					Vertex nextDestination = next.getTwin().getOrigin();
					Coordinate cnd = nextDestination.getCoordinate();

					Vector2 e1 = new Vector2(co, cd).normalized();
					Vector2 e2 = new Vector2(cd, cnd).normalized();

					Vector2 c1 = arrow.getDestination().add(e1.mult(shorten));
					Vector2 c2 = nextArrow.getOrigin().sub(e2.mult(shorten));

					GeneralPath path = new GeneralPath();

					Coordinate orig = coordinate(arrow.getDestination());
					path.moveTo(orig.getX(), orig.getY());

					Coordinate dest = coordinate(nextArrow.getOrigin());
					path.curveTo(c1.getX(), c1.getY(), c2.getX(), c2.getY(),
							dest.getX(), dest.getY());

					painter.draw(path);
				}
			}
		}
	}

	private Coordinate coordinate(Vector2 v)
	{
		return new Coordinate(v.getX(), v.getY());
	}

	private void drawLine(Vector2 v1, Vector2 v2)
	{
		painter.drawLine(v1.getX(), v1.getY(), v2.getX(), v2.getY());
	}

}
