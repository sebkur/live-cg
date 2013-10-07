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
package de.topobyte.polygon.shortestpath;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.util.MouseOver;

public class PickNodesListener extends MouseAdapter
{
	private static final int SELECTION_THRESHOLD = 10;

	private ShortestPathPanel spp;

	private boolean pressedStart = false;
	private boolean pressedTarget = false;

	public PickNodesListener(ShortestPathPanel spp)
	{
		this.spp = spp;
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
		if (pressedStart || pressedTarget) {
			pressedStart = false;
			pressedTarget = false;
			update = true;
			spp.setDragStart(null);
			spp.setDragTarget(null);
		}
		update |= checkOver(e);
		if (update) {
			spp.repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!active()) {
			return;
		}
		if (pressedStart) {
			spp.setDragStart(new Coordinate(e.getX(), e.getY()));
		} else if (pressedTarget) {
			spp.setDragTarget(new Coordinate(e.getX(), e.getY()));
		}
		spp.repaint();
	}

	private boolean isOn(MouseEvent e, Node node)
	{
		Coordinate c = node.getCoordinate();
		Coordinate cM = new Coordinate(e.getX(), e.getY());
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
		update |= spp.setStartMouseOver(overStart);
		update |= spp.setTargetMouseOver(overTarget);
		return update;
	}

	private void setNone()
	{
		boolean update = false;
		update |= spp.setStartMouseOver(MouseOver.NONE);
		update |= spp.setTargetMouseOver(MouseOver.NONE);
		if (update) {
			spp.repaint();
		}
	}

	private void updatePressedState()
	{
		boolean update = false;
		update |= spp.setStartMouseOver(pressedStart ? MouseOver.ACTIVE
				: MouseOver.NONE);
		update |= spp.setTargetMouseOver(pressedTarget ? MouseOver.ACTIVE
				: MouseOver.NONE);
		if (update) {
			spp.repaint();
		}
	}

}
