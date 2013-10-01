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
package de.topobyte.polygon.monotonepieces;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import de.topobyte.livecg.geometry.geom.AwtHelper;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.util.SwingUtil;

public class MonotonePiecesPanel extends JPanel
{

	private static final long serialVersionUID = 2129465700417909129L;

	private Polygon polygon;
	private Map<Node, VertexType> map = new HashMap<Node, VertexType>();

	public MonotonePiecesPanel(Polygon polygon)
	{
		this.polygon = polygon;

		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			map.put(node, VertexType.REGULAR);
		}
	}

	@Override
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		Area shape = AwtHelper.toShape(polygon);
		g.setColor(new Color(0x66ff0000, true));
		g.fill(shape);

		g.setColor(Color.BLACK);
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Coordinate c1 = shell.getCoordinate(i);
			int j = i + 1;
			if (j == shell.getNumberOfNodes()) {
				j = 0;
			}
			Coordinate c2 = shell.getCoordinate(j);
			g.drawLine((int) Math.round(c1.getX()),
					(int) Math.round(c1.getY()), (int) Math.round(c2.getX()),
					(int) Math.round(c2.getY()));
		}

		g.setColor(Color.BLACK);
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			VertexType type = map.get(node);
			Coordinate c = node.getCoordinate();

			double size = 6;

			Arc2D arc = new Arc2D.Double(c.getX() - size / 2, c.getY() - size
					/ 2, size, size, 0, 360, Arc2D.CHORD);
			Rectangle2D rect = new Rectangle2D.Double(c.getX() - size / 2,
					c.getY() - size / 2, size, size);
			Path2D triangle = new Path2D.Double();

			switch (type) {
			case REGULAR:
				g.fill(arc);
				break;
			case START:
				g.draw(rect);
				break;
			case END:
				g.fill(rect);
				break;
			case SPLIT:
				triangle.moveTo(c.getX(), c.getY() - size / 2);
				triangle.lineTo(c.getX() - size / 2, c.getY() + size / 2);
				triangle.lineTo(c.getX() + size / 2, c.getY() + size / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			case MERGE:
				triangle.moveTo(c.getX(), c.getY() + size / 2);
				triangle.lineTo(c.getX() - size / 2, c.getY() - size / 2);
				triangle.lineTo(c.getX() + size / 2, c.getY() - size / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			default:
				break;
			}
		}
	}
}
