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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeListener;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseModeProvider;

public class GeometryEditPane extends JPanel implements MouseModeProvider,
		ContentChangedListener
{

	private static final long serialVersionUID = -8078013859398953550L;

	private MouseMode mouseMode = MouseMode.EDIT;

	private Content content;

	public GeometryEditPane()
	{
		content = new Content();
		content.addContentChangedListener(this);

		setBackground(new Color(0xFAFAFA));

		EditorMouseListener mouseListener = new EditorMouseListener(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		setTransferHandler(new EditPaneTransferHandler(content));
	}

	@Override
	public MouseMode getMouseMode()
	{
		return mouseMode;
	}

	@Override
	public void setMouseMode(MouseMode mouseMode)
	{
		this.mouseMode = mouseMode;
		for (MouseModeListener listener : listeners) {
			listener.mouseModeChanged(mouseMode);
		}
	}

	private List<MouseModeListener> listeners = new ArrayList<MouseModeListener>();

	@Override
	public void addMouseModeListener(MouseModeListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void removeMouseModeListener(MouseModeListener listener)
	{
		listeners.remove(listener);
	}

	/*
	 * content
	 */

	public Content getContent()
	{
		return content;
	}

	/*
	 * drawing
	 */

	private Color colorEditLines = Color.BLACK;
	private Color colorEditLinePoints = Color.BLACK;
	private Color colorEditingLines = Color.BLACK;
	private Color colorEditingLinePoints = Color.BLUE;
	private Color colorLastEditingLinePoints = Color.RED;

	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

		List<Editable> lines = content.getLines();
		for (int i = 0; i < lines.size(); i++) {
			Editable line = lines.get(i);
			draw(g, line, colorEditLines, colorEditLinePoints, getName(i));
		}

		if (content.getEditingLine() != null) {
			draw(g, content.getEditingLine(), colorEditingLines,
					colorEditingLinePoints, "");
			g.setColor(colorLastEditingLinePoints);
			Coordinate c = content.getEditingLine().getLastCoordinate();
			g.drawRect((int) Math.round(c.getX() - 3),
					(int) Math.round(c.getY() - 3), 6, 6);
		}
	}

	private String getName(int i)
	{
		return new Character((char) ('P' + i)).toString();
	}

	private void draw(Graphics2D g, Editable editable, Color colorLine,
			Color colorPoints, String name)
	{
		int n = editable.getNumberOfCoordinates();
		if (n == 0) {
			return;
		}
		useAntialiasing(g, true);
		// line segments
		useAntialiasing(g, true);
		g.setColor(colorLine);
		g.setStroke(new BasicStroke(1.0f));
		Coordinate last = editable.getCoordinate(0);
		for (int i = 1; i < n; i++) {
			Coordinate current = editable.getCoordinate(i);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(current.getX());
			int y2 = (int) Math.round(current.getY());
			g.drawLine(x1, y1, x2, y2);
			last = current;
		}
		if (editable.isClosed()) {
			Coordinate first = editable.getCoordinate(0);
			int x1 = (int) Math.round(last.getX());
			int y1 = (int) Math.round(last.getY());
			int x2 = (int) Math.round(first.getX());
			int y2 = (int) Math.round(first.getY());
			g.drawLine(x1, y1, x2, y2);
		}
		useAntialiasing(g, false);
		// points
		g.setColor(colorPoints);
		g.setStroke(new BasicStroke(1.0f));
		for (int i = 0; i < n; i++) {
			Coordinate current = editable.getCoordinate(i);
			int x = (int) Math.round(current.getX());
			int y = (int) Math.round(current.getY());
			g.drawRect(x - 2, y - 2, 4, 4);
		}
		// label
		Coordinate first = editable.getFirstCoordinate();
		g.drawString(name, (int) Math.round(first.getX()) - 2,
				(int) Math.round(first.getY()) - 4);
	}

	private void useAntialiasing(Graphics2D g, boolean b)
	{
		if (b) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	@Override
	public void contentChanged()
	{
		repaint();
	}

}
