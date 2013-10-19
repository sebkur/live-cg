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
		painter.setColor(new Color(255, 0, 255));
		for (HalfEdge halfedge : dcel.halfedges) {
			HalfEdge twin = halfedge.getTwin();
			Vertex origin = halfedge.getOrigin();
			Vertex destination = twin.getOrigin();
			Coordinate co = origin.getCoordinate();
			Coordinate cd = destination.getCoordinate();

			double gap = 5;
			double shorten = 6;
			double length = 15;
			double alpha = Math.PI / 8;
			double lsa = length * Math.sin(alpha);
			double lca = length * Math.cos(alpha);

			Vector vo = new Vector(co);
			Vector vd = new Vector(cd);
			Vector he = new Vector(co, cd).normalized();
			Vector ppd = he.perpendicular().normalized();
			
			Vector ao = vo.add(ppd.mult(gap)).add(he.mult(shorten));
			Vector ad = vd.add(ppd.mult(gap)).sub(he.mult(shorten));
			painter.drawLine(ao.getX(), ao.getY(), ad.getX(), ad.getY());
			
			Vector end = ad.add(ppd.mult(lsa)).sub(he.mult(lca));

			painter.drawLine(ad.getX(), ad.getY(), end.getX(), end.getY());
		}
	}

}
