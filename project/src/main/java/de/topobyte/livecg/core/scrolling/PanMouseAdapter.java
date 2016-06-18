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
package de.topobyte.livecg.core.scrolling;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import de.topobyte.livecg.core.geometry.geom.Coordinate;

public class PanMouseAdapter<T extends JComponent & Viewport & HasScene & HasMargin>
		extends MouseAdapter
{

	private boolean pressed = false;
	private DragInfo dragInfo = null;

	private T view;

	private ViewportMath<T> calculator;

	public PanMouseAdapter(T view)
	{
		this.view = view;

		calculator = new ViewportMath<>(view);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3) {
			pressed = true;
			dragInfo = new DragInfo(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON3) {
			pressed = false;
			dragInfo = null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (!pressed) {
			return;
		}
		dragInfo.update(e.getX(), e.getY());
		Coordinate delta = dragInfo.getDeltaToLast();

		double dx = delta.getX() / view.getZoom();
		double dy = delta.getY() / view.getZoom();
		double nx = view.getPositionX() + dx;
		double ny = view.getPositionY() + dy;

		nx = Math.max(nx, calculator.getMinimumOffset(true));
		nx = Math.min(nx, calculator.getMaximumOffset());
		ny = Math.max(ny, calculator.getMinimumOffset(false));
		ny = Math.min(ny, calculator.getMaximumOffset());

		view.setPositionX(nx);
		view.setPositionY(ny);
		view.repaint();
	}

}
