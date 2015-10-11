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
package de.topobyte.livecg.ui.segmenteditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.scrolling.ViewportWithSignals;
import de.topobyte.livecg.core.scrolling.ViewportListener;
import de.topobyte.livecg.util.SwingUtil;

public class SegmentEditPane extends JPanel implements ViewportWithSignals
{

	private static final long serialVersionUID = 7921493627117424315L;

	private int maxWidth;
	private int maxHeight;

	public SegmentEditPane(int width, int height, Chain segment)
	{
		this.maxWidth = width;
		this.maxHeight = height;
		this.segment = segment;

		setBackground(new Color(0xFAFAFA));

		SegmentEditMouseListener mouseListener = new SegmentEditMouseListener(
				this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}

	public int getMaxWidth()
	{
		return maxWidth;
	}

	public int getMaxHeight()
	{
		return maxHeight;
	}

	/*
	 * content
	 */

	private Chain segment = new Chain();

	public Chain getSegment()
	{
		return segment;
	}

	/*
	 * drawing
	 */

	private Color colorValidArea = new Color(0xEEEEEE);
	private Color colorEditLines = Color.BLACK;
	private Color colorEditLinePoints = Color.BLUE;
	private Color colorEditLinePoint0 = Color.RED;

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

		g.setColor(colorValidArea);
		g.fillRect(0, 0, maxWidth, maxHeight);

		draw(g, segment, colorEditLines, colorEditLinePoints,
				colorEditLinePoint0);
	}

	private void draw(Graphics2D g, Chain editable, Color colorLine,
			Color colorPoints, Color colorPoint0)
	{
		int n = editable.getNumberOfNodes();
		SwingUtil.useAntialiasing(g, true);
		// line segments
		SwingUtil.useAntialiasing(g, true);
		g.setColor(colorLine);
		g.setStroke(new BasicStroke(1.0f));
		Coordinate last = editable.getCoordinate(0);
		for (int i = 0; i < n; i++) {
			Coordinate current = editable.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			g.drawLine(x1, y1, x2, y2);
			last = current;
		}
		SwingUtil.useAntialiasing(g, false);
		// points
		g.setStroke(new BasicStroke(1.0f));
		for (int i = 0; i < n; i++) {
			if (i == 0) {
				g.setColor(colorPoint0);
			} else {
				g.setColor(colorPoints);
			}
			Coordinate current = editable.getCoordinate(i);
			int x = (int) Math.round(current.getX());
			int y = (int) Math.round(current.getY());
			g.drawRect(x - 2, y - 2, 4, 4);
		}
	}

	private List<SegmentChangeListener> listeners = new ArrayList<SegmentChangeListener>();

	public void addLineChangeListener(SegmentChangeListener listener)
	{
		listeners.add(listener);
	}

	public void removeLineChangeListener(SegmentChangeListener listener)
	{
		listeners.remove(listener);
	}

	public void triggerChange()
	{
		for (SegmentChangeListener listener : listeners) {
			listener.segmentChanged();
		}
	}

	@Override
	public double getPositionX()
	{
		return 0;
	}

	@Override
	public double getPositionY()
	{
		return 0;
	}

	@Override
	public double getZoom()
	{
		return 1;
	}

	@Override
	public void setZoom(double value)
	{
		// ignore
	}

	private List<ViewportListener> viewportListeners = new ArrayList<ViewportListener>();

	@Override
	public void addViewportListener(ViewportListener listener)
	{
		viewportListeners.add(listener);
	}

	@Override
	public void removeViewportListener(ViewportListener listener)
	{
		viewportListeners.remove(listener);
	}

	private void fireViewportListenersViewportChanged()
	{
		for (ViewportListener listener : viewportListeners) {
			listener.viewportChanged();
		}
	}

	@Override
	public void setPositionX(double value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPositionY(double value)
	{
		// TODO Auto-generated method stub

	}
}
