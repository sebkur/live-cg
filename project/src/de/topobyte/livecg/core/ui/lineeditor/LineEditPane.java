/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

package de.topobyte.livecg.core.ui.lineeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.util.SwingUtil;

public class LineEditPane extends JPanel
{

	private static final long serialVersionUID = 7921493627117424315L;

	private int maxWidth;
	private int maxHeight;

	public LineEditPane(int width, int height, Chain line)
	{
		this.maxWidth = width;
		this.maxHeight = height;
		this.line = line;

		setBackground(new Color(0xFAFAFA));

		LineEditMouseListener mouseListener = new LineEditMouseListener(this);
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

	private Chain line = new Chain();

	public Chain getLine()
	{
		return line;
	}

	/*
	 * drawing
	 */

	private Color colorValidArea = new Color(0xEEEEEE);
	private Color colorEditLines = Color.BLACK;
	private Color colorEditLinePoints = Color.BLUE;
	private Color colorEditLinePoint0 = Color.RED;

	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

		g.setColor(colorValidArea);
		g.fillRect(0, 0, maxWidth, maxHeight);

		draw(g, line, colorEditLines, colorEditLinePoints, colorEditLinePoint0);
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

	private List<LineChangeListener> listeners = new ArrayList<LineChangeListener>();

	public void addLineChangeListener(LineChangeListener listener)
	{
		listeners.add(listener);
	}

	public void removeLineChangeListener(LineChangeListener listener)
	{
		listeners.remove(listener);
	}

	public void triggerChange()
	{
		for (LineChangeListener listener : listeners) {
			listener.lineChanged();
		}
	}

}
