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

package de.topobyte.livecg.geometryeditor.geometryeditor.scale;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.scrolling.ViewportWithSignals;
import de.topobyte.livecg.core.scrolling.ViewportListener;

public abstract class Scale extends JPanel implements ViewportListener
{

	private static final long serialVersionUID = 8572548898229307068L;

	@Override
	public abstract Dimension getPreferredSize();

	public abstract boolean isHorizontal();

	private String q = "geometryeditor.colors.scale.";

	private Color colorBackground = LiveConfig.getColor(q + "background");
	private Color colorBaseline = LiveConfig.getColor(q + "baseline");
	private Color colorLines = LiveConfig.getColor(q + "lines");
	private Color colorFont = LiveConfig.getColor(q + "font");
	private Color colorMarker = LiveConfig.getColor(q + "marker");

	private ViewportWithSignals viewport;

	public Scale(ViewportWithSignals viewport)
	{
		this.viewport = viewport;
		setPreferredSize(getPreferredSize());
	}

	@Override
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

		AwtPainter p = new AwtPainter(g);

		p.setColor(colorBackground);
		p.fillRect(0, 0, getWidth(), getHeight());

		// font metrics for font alignment for vertical bars
		FontMetrics fm = getFontMetrics(getFont());

		int width = getWidth();
		int height = getHeight();

		p.setColor(colorBaseline);
		p.setStrokeWidth(2.0);
		// baseline
		if (horizontal) {
			p.drawLine(0, height, width, height);
		} else {
			p.drawLine(width, 0, width, height);
		}

		// Find best smallest step value, by alternatingly multiplying with 2 or
		// 5, respectively alternatingly divide by 2 or 5.
		double sstep = 10;
		if (sstep * viewport.getZoom() > 20) {
			boolean odd = true;
			while (true) {
				if (odd) {
					if (sstep * viewport.getZoom() > 20) {
						sstep /= 2;
					} else {
						break;
					}
				} else {
					if (sstep * viewport.getZoom() > 50) {
						sstep /= 5;
					} else {
						break;
					}
				}
				odd = !odd;
			}
		} else {
			boolean odd = false;
			while (true) {
				if (odd) {
					if (sstep * 2 * viewport.getZoom() < 20) {
						sstep *= 2;
					} else {
						break;
					}
				} else {
					if (sstep * 5 * viewport.getZoom() < 50) {
						sstep *= 5;
					} else {
						break;
					}
				}
				odd = !odd;
			}
		}

		// scale line definitions
		ScaleLine[] lines = new ScaleLine[] {
				new ScaleLine(30, 4.0f, sstep * 10, true),
				new ScaleLine(20, 3.0f, sstep * 5, true),
				new ScaleLine(10, 1.0f, sstep, false) };
		// scale line drawing
		for (int i = 0; i < lines.length; i++) {
			ScaleLine line = lines[i];
			double s = horizontal ? -viewport.getPositionX() : -viewport
					.getPositionY();
			double limit = horizontal ? width / viewport.getZoom()
					- viewport.getPositionX() : height / viewport.getZoom()
					- viewport.getPositionY();
			double sv = 0;
			if (s < 0) {
				while (sv > s) {
					sv -= line.getStep();
				}
			} else {
				while (sv + line.getStep() < s) {
					sv += line.getStep();
				}
			}
			// Define number of decimal digits to display
			double step = line.getStep();
			int digits = 0;
			if (Math.abs(Math.round(step) - step) < 0.0001) {
				digits = 0;
			} else if (Math.abs(Math.round(step * 10) - step * 10) < 0.0001) {
				digits = 1;
			} else {
				digits = 2;
			}
			// Loop over positions
			positions: for (double j = sv; j < limit; j += step) {
				for (int k = 0; k < i; k++) {
					if (lines[k].occupies(j)) {
						continue positions;
					}
				}
				float lineSize = line.getHeight();
				float strokeWidth = line.getStrokeWidth();
				p.setStrokeWidth(strokeWidth);
				int base = horizontal ? height : width;
				int start = Math.round(base - lineSize);

				p.setColor(colorLines);
				if (horizontal) {
					double x = (j + viewport.getPositionX())
							* viewport.getZoom();
					p.drawLine(x, start, x, base);
				} else {
					double y = (j + viewport.getPositionY())
							* viewport.getZoom();
					p.drawLine(start, y, base, y);
				}

				if (line.hasLabel()) {
					p.setColor(colorFont);
					String label = String.format("%." + digits + "f", j);
					if (horizontal) {
						double x = (j + viewport.getPositionX())
								* viewport.getZoom();
						p.drawString(label, x, start);
					} else {
						double y = (j + viewport.getPositionY())
								* viewport.getZoom();
						int labelWidth = fm.stringWidth(label);
						int x = width - labelWidth - 5;
						p.drawString(label, x, y);
					}
				}
			}
		}
		// marker
		p.setStrokeWidth(1.0);
		p.setColor(colorMarker);
		if (marker != null) {
			if (horizontal) {
				p.drawLine(marker, 0, marker, height);
			} else {
				p.drawLine(0, marker, width, marker);
			}
		}
	}

	private Integer marker = null;

	public void setMarker(Integer position)
	{
		marker = position;
	}

	@Override
	public void viewportChanged()
	{
		repaint();
	}

	@Override
	public void zoomChanged()
	{
		repaint();
	}

	@Override
	public void complexChange()
	{
		repaint();
	}

}
