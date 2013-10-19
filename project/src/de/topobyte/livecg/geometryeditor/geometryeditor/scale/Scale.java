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

package de.topobyte.livecg.geometryeditor.geometryeditor.scale;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public abstract class Scale extends JPanel
{

	private static final long serialVersionUID = 8572548898229307068L;

	public abstract Dimension getPreferredSize();

	public abstract boolean isHorizontal();

	public Scale()
	{
		setBackground(Color.WHITE);
		setPreferredSize(getPreferredSize());
	}

	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;

		if (isHorizontal()) {
			paintHorizontal(g);
		} else {
			paintVertical(g);
		}
	}

	private void paintHorizontal(Graphics2D g)
	{
		paintLines(g, true);
	}

	private void paintVertical(Graphics2D g)
	{
		paintLines(g, false);
	}

	private void paintLines(Graphics2D g, boolean horizontal)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color colorBaseline = Color.BLACK;
		Color colorLines = Color.RED;
		Color colorFont = Color.BLACK;
		Color colorMarker = Color.BLACK;

		// font metrics for font alignment for vertical bars
		FontMetrics fm = getFontMetrics(getFont());

		int width = getWidth();
		int height = getHeight();

		g.setColor(colorBaseline);
		g.setStroke(new BasicStroke(2.0f));
		// baseline
		if (horizontal) {
			g.drawLine(0, height, width, height);
		} else {
			g.drawLine(width, 0, width, height);
		}
		// scale line definitions
		ScaleLine[] lines = new ScaleLine[] {
				new ScaleLine(30, 4.0f, 100, true),
				new ScaleLine(20, 3.0f, 50, true),
				new ScaleLine(10, 1.0f, 10, false) };
		// scale line drawing
		for (int i = 0; i < lines.length; i++) {
			ScaleLine line = lines[i];
			int limit = horizontal ? width : height;
			positions: for (int p = 0; p < limit; p += line.getStep()) {
				for (int k = 0; k < i; k++) {
					if (lines[k].occupies(p)) {
						continue positions;
					}
				}
				float lineSize = line.getHeight();
				float strokeWidth = line.getStrokeWidth();
				g.setStroke(new BasicStroke(strokeWidth));
				int base = horizontal ? height : width;
				int start = Math.round(base - lineSize);

				g.setColor(colorLines);
				if (horizontal) {
					g.drawLine(p, start, p, base);
				} else {
					g.drawLine(start, p, base, p);
				}

				if (line.hasLabel()) {
					g.setColor(colorFont);
					String label = String.format("%d", p);
					if (horizontal) {
						g.drawString(label, p, start);
					} else {
						int labelWidth = fm.stringWidth(label);
						int x = width - labelWidth - 5;
						g.drawString(label, x, p);
					}
				}
			}
		}
		// marker
		g.setStroke(new BasicStroke(1.0f));
		g.setColor(colorMarker);
		if (marker != null) {
			if (horizontal) {
				g.drawLine(marker, 0, marker, height);
			} else {
				g.drawLine(0, marker, width, marker);
			}
		}
	}

	private Integer marker = null;

	public void setMarker(Integer position)
	{
		marker = position;
	}

}
