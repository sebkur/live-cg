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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.awt.event.MouseEvent;

import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.CrossingsTest;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.scrolling.ViewportMouseAdapter;
import de.topobyte.livecg.util.MouseOver;

public class PickNodesListener extends ViewportMouseAdapter<ShortestPathPanel>
{

	private static final int SELECTION_THRESHOLD = 10;

	private ShortestPathPanel spp;
	private AlgorithmMonitor algorithmMonitor;

	private boolean pressedStart = false;
	private boolean pressedTarget = false;

	public PickNodesListener(ShortestPathPanel spp,
			AlgorithmMonitor algorithmMonitor)
	{
		super(spp);
		this.spp = spp;
		this.algorithmMonitor = algorithmMonitor;
	}

	private boolean active()
	{
		return spp.getAlgorithm().getStatus() == 0;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		checkOverAndRepaint(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		checkOverAndRepaint(e);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		setNone();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		pressedStart = false;
		pressedTarget = false;
		if (isOn(e, spp.getAlgorithm().getNodeStart())) {
			pressedStart = true;
		} else if (isOn(e, spp.getAlgorithm().getNodeTarget())) {
			pressedTarget = true;
		}
		updatePressedState();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		boolean update = false;
		Node start = null;
		Node target = null;

		if (pressedStart) {
			start = new Node(getSceneCoordinate(e));
		}
		if (pressedTarget) {
			target = new Node(getSceneCoordinate(e));
		}
		if (pressedStart || pressedTarget) {
			pressedStart = false;
			pressedTarget = false;
			update = true;
			spp.getPainter().setDragStart(null);
			spp.getPainter().setDragTarget(null);
		}
		// TODO: Also implement some kind of snapping to nodes and slight
		// correction (moving into polygon) if the desired position is outside
		// the polygon but near
		Polygon polygon = spp.getAlgorithm().getPolygon();
		CrossingsTest test = new CrossingsTest(polygon.getShell());
		if (start != null) {
			if (test.covers(start.getCoordinate())) {
				spp.getAlgorithm().setStart(start);
			}
		}
		if (target != null) {
			if (test.covers(target.getCoordinate())) {
				spp.getAlgorithm().setTarget(target);
			}
		}
		update |= checkOver(e);
		if (update) {
			spp.repaint();
		}
		if (start != null || target != null) {
			algorithmMonitor.fireAlgorithmChangedListener();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		if (pressedStart) {
			spp.getPainter().setDragStart(getSceneCoordinate(e));
		} else if (pressedTarget) {
			spp.getPainter().setDragTarget(getSceneCoordinate(e));
		}
		spp.repaint();
	}

	private boolean isOn(MouseEvent e, Node node)
	{
		Coordinate c = node.getCoordinate();
		Coordinate cM = getSceneCoordinate(e);
		return cM.distance(c) < SELECTION_THRESHOLD;
	}

	private void checkOverAndRepaint(MouseEvent e)
	{
		if (checkOver(e)) {
			spp.repaint();
		}
	}

	private boolean checkOver(MouseEvent e)
	{
		MouseOver overStart = MouseOver.NONE;
		MouseOver overTarget = MouseOver.NONE;
		if (isOn(e, spp.getAlgorithm().getNodeStart())) {
			overStart = MouseOver.OVER;
		} else if (isOn(e, spp.getAlgorithm().getNodeTarget())) {
			overTarget = MouseOver.OVER;
		}
		boolean update = false;
		update |= spp.getPainter().setStartMouseOver(overStart);
		update |= spp.getPainter().setTargetMouseOver(overTarget);
		return update;
	}

	private void setNone()
	{
		boolean update = false;
		update |= spp.getPainter().setStartMouseOver(MouseOver.NONE);
		update |= spp.getPainter().setTargetMouseOver(MouseOver.NONE);
		if (update) {
			spp.repaint();
		}
	}

	private void updatePressedState()
	{
		boolean update = false;
		update |= spp.getPainter().setStartMouseOver(
				pressedStart ? MouseOver.ACTIVE : MouseOver.NONE);
		update |= spp.getPainter().setTargetMouseOver(
				pressedTarget ? MouseOver.ACTIVE : MouseOver.NONE);
		if (update) {
			spp.repaint();
		}
	}

}
