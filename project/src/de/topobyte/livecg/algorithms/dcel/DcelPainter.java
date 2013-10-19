/* This file is part of LiveCG. 
 * 
 * Copyright (C) 2013  Sebastian Kuerten
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.algorithms.dcel;

import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.HalfEdge;
import de.topobyte.livecg.core.geometry.dcel.Vertex;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.lina2.Vector;
import de.topobyte.livecg.core.painting.BasicAlgorithmPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Painter;

public class DcelPainter extends BasicAlgorithmPainter
{

	private DCEL dcel;

	public DcelPainter(Painter painter, DCEL dcel)
	{
		super(painter);
		this.dcel = dcel;
	}

	@Override
	public void paint()
	{
		painter.setColor(new Color(255, 255, 255));
		painter.fillRect(0, 0, getWidth(), getHeight());

		painter.setColor(new Color(0, 0, 0));
		for (Vertex vertex : dcel.vertices) {
			Coordinate c = vertex.getCoordinate();
			painter.fillCircle(c.getX(), c.getY(), 4);
		}

		painter.setColor(new Color(0, 0, 255));
		for (HalfEdge halfedge : dcel.halfedges) {
			HalfEdge twin = halfedge.getTwin();
			Vertex origin = halfedge.getOrigin();
			Vertex destination = twin.getOrigin();
			Coordinate co = origin.getCoordinate();
			Coordinate cd = destination.getCoordinate();
			painter.drawLine(co.getX(), co.getY(), cd.getX(), cd.getY());
		}
		for (HalfEdge halfedge : dcel.halfedges) {
			HalfEdgeArrow arrow = new HalfEdgeArrow(halfedge);

			if (arrow.isValid()) {
				painter.setStrokeWidth(1.0);
				painter.setColor(new Color(255, 0, 255));
				drawLine(arrow.getOrigin(), arrow.getDestination());
				if (arrow.getLength() > 4) {
					drawLine(arrow.getDestination(), arrow.getMarker());
				}

				painter.setStrokeWidth(1.0);
				painter.setColor(new Color(0, 255, 0));
				HalfEdge next = halfedge.getNext();
				if (next != null) {
					HalfEdgeArrow nextArrow = new HalfEdgeArrow(next);
					if (nextArrow.isValid()) {
						drawLine(arrow.getDestination(), nextArrow.getOrigin());
					}
				}
			}

		}
	}

	private void drawLine(Vector v1, Vector v2)
	{
		painter.drawLine(v1.getX(), v1.getY(), v2.getX(), v2.getY());
	}

}
