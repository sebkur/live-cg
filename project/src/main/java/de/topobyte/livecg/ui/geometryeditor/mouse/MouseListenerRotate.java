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
package de.topobyte.livecg.ui.geometryeditor.mouse;

import java.awt.event.MouseEvent;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.viewports.geometry.Coordinate;

public class MouseListenerRotate extends EditPaneMouseListener
{

	final static Logger logger = LoggerFactory
			.getLogger(MouseListenerRotate.class);

	private RotateInfo rotateInfo = null;

	public MouseListenerRotate(GeometryEditPane editPane)
	{
		super(editPane);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1) {
			Coordinate center = centerOfSelectedObjects();
			rotateInfo = new RotateInfo(getX(e), getY(e), center.getX(),
					center.getY());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (editPane.somethingSelected()) {
			rotateInfo.update(getX(e), getY(e));
			double alpha = rotateInfo.getAngleToLast();
			logger.debug("rotate by : " + alpha);
			logger.debug("rotate around  : " + rotateInfo.getCenter());
			rotateSelectedObjects(rotateInfo.getCenter(), alpha);
			editPane.getContent().fireContentChanged();
		}
	}

	private void rotateSelectedObjects(Coordinate center, double alpha)
	{
		Set<Node> toRotate = editPane.getSelectedNodes();

		double sin = Math.sin(alpha);
		double cos = Math.cos(alpha);

		Vector2 vc = new Vector2(center);
		for (Node node : toRotate) {
			Coordinate old = node.getCoordinate();
			Vector2 v = new Vector2(old);
			Vector2 t = v.sub(vc);
			double x = cos * t.getX() - sin * t.getY();
			double y = sin * t.getX() + cos * t.getY();
			Vector2 rotated = new Vector2(x, y);
			Vector2 r = rotated.add(vc);
			node.setCoordinate(new Coordinate(r.getX(), r.getY()));
		}
	}

	private Coordinate centerOfSelectedObjects()
	{
		Set<Node> nodes = editPane.getSelectedNodes();

		double x = 0, y = 0;
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			x += c.getX();
			y += c.getY();
		}
		x /= nodes.size();
		y /= nodes.size();
		return new Coordinate(x, y);
	}
}
