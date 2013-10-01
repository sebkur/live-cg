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

import de.topobyte.frechet.ui.freespace.calc.Vector;
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

	private Map<Node, Double> dets = new HashMap<Node, Double>();
	private Map<Node, Double> angles = new HashMap<Node, Double>();

	public MonotonePiecesPanel(Polygon polygon)
	{
		this.polygon = polygon;

		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			map.put(node, VertexType.REGULAR);
		}

		/*
		 * Find interior side
		 */

		double sum1 = 0, sum2 = 0;

		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			int pre = i - 1;
			if (pre < 0) {
				pre = shell.getNumberOfNodes() - 1;
			}
			int suc = i + 1;
			if (suc >= shell.getNumberOfNodes()) {
				suc = 0;
			}
			Node nodePre = shell.getNode(pre);
			Node nodeSuc = shell.getNode(suc);

			Coordinate c = node.getCoordinate();
			Coordinate cPre = nodePre.getCoordinate();
			Coordinate cSuc = nodeSuc.getCoordinate();

			double angle = angle(c, cPre, cSuc);
			double det = determinant(c, cPre, cSuc);
			dets.put(node, det);
			angles.put(node, angle);

			sum1 += angle;
			sum2 += Math.PI * 2 - angle;
		}

		boolean interiorOnLeftSide = true;
		if (sum1 > sum2) {
			interiorOnLeftSide = false;
		}
		System.out.println(interiorOnLeftSide);

		/*
		 * Classify vertices
		 */

		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			Node node = shell.getNode(i);
			int pre = i - 1;
			if (pre < 0) {
				pre = shell.getNumberOfNodes() - 1;
			}
			int suc = i + 1;
			if (suc >= shell.getNumberOfNodes()) {
				suc = 0;
			}
			Node nodePre = shell.getNode(pre);
			Node nodeSuc = shell.getNode(suc);

			Coordinate c = node.getCoordinate();
			Coordinate cPre = nodePre.getCoordinate();
			Coordinate cSuc = nodeSuc.getCoordinate();

			if (c.getY() < cPre.getY() && c.getY() < cSuc.getY()) {
				map.put(node, VertexType.START);
				double interiorAngle = angle(c, cPre, cSuc);
				if (!interiorOnLeftSide) {
					interiorAngle = Math.PI * 2 - interiorAngle;
				}
				if (interiorAngle > Math.PI) {
					map.put(node, VertexType.SPLIT);
				}
			} else if (c.getY() > cPre.getY() && c.getY() > cSuc.getY()) {
				map.put(node, VertexType.END);
				double interiorAngle = angle(c, cPre, cSuc);
				if (!interiorOnLeftSide) {
					interiorAngle = Math.PI * 2 - interiorAngle;
				}
				if (interiorAngle > Math.PI) {
					map.put(node, VertexType.MERGE);
				}
			}
		}
	}

	private double angle(Coordinate c, Coordinate cPre, Coordinate cSuc)
	{
		Vector v1 = new Vector(cPre.getX() - c.getX(), cPre.getY() - c.getY());
		Vector v2 = new Vector(cSuc.getX() - c.getX(), cSuc.getY() - c.getY());
		double dotProduct = v1.dotProduct(v2);
		double cosAngle = dotProduct / (v1.norm() * v2.norm());
		double angle = Math.acos(cosAngle);
		double det = determinant(c, cPre, cSuc);
		if (det > 0) {
			angle = Math.PI * 2 - angle;
		}
		return angle;
	}

	private double determinant(Coordinate c, Coordinate cPre, Coordinate cSuc)
	{
		double det = (c.getX() - cPre.getX()) * (cSuc.getY() - cPre.getY())
				- (c.getY() - cPre.getY()) * (cSuc.getX() - cPre.getX());
		return det;
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

			double arcSize = 6;
			double rectSize = 6;
			double triangleSize = 8;

			Arc2D arc = new Arc2D.Double(c.getX() - arcSize / 2, c.getY()
					- arcSize / 2, arcSize, arcSize, 0, 360, Arc2D.CHORD);
			Rectangle2D rect = new Rectangle2D.Double(c.getX() - rectSize / 2,
					c.getY() - rectSize / 2, rectSize, rectSize);
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
				triangle.moveTo(c.getX(), c.getY() - triangleSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						+ triangleSize / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			case MERGE:
				triangle.moveTo(c.getX(), c.getY() + arcSize / 2);
				triangle.lineTo(c.getX() - triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.lineTo(c.getX() + triangleSize / 2, c.getY()
						- triangleSize / 2);
				triangle.closePath();
				g.fill(triangle);
				break;
			default:
				break;
			}
		}
	}
}
