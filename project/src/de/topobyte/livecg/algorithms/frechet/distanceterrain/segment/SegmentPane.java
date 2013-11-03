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
package de.topobyte.livecg.algorithms.frechet.distanceterrain.segment;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainPainterSegments;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.geometryeditor.segmenteditor.SegmentChangeListener;
import de.topobyte.livecg.util.SwingUtil;

public class SegmentPane extends JPanel implements SegmentChangeListener
{

	private static final long serialVersionUID = 8705743202734597623L;

	private DistanceTerrainPainterSegments terrainPainter;

	private AwtPainter painter;

	public SegmentPane()
	{
		painter = new AwtPainter(null);
		terrainPainter = new DistanceTerrainPainterSegments(true, painter);
	}

	public void setSegment1(LineSegment seg1)
	{
		terrainPainter.setSegment1(seg1);
	}

	public void setSegment2(LineSegment seg2)
	{
		terrainPainter.setSegment2(seg2);
	}

	@Override
	public void segmentChanged()
	{
		repaint();
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

		painter.setGraphics(g);
		terrainPainter.setWidth(getWidth());
		terrainPainter.setHeight(getHeight());
		terrainPainter.paint();
	}

}
