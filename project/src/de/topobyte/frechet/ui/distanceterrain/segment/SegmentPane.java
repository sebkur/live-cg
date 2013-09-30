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

package de.topobyte.frechet.ui.distanceterrain.segment;

import java.awt.Graphics;

import javax.swing.JPanel;

import de.topobyte.frechet.ui.freespace.calc.LineSegment;
import de.topobyte.frechet.ui.lineeditor.LineChangeListener;

public class SegmentPane extends JPanel implements LineChangeListener
{

	private static final long serialVersionUID = 8705743202734597623L;

	private DistanceTerrainPainter painter;

	public SegmentPane()
	{
		painter = new DistanceTerrainPainter(true);
	}

	public void setSegment1(LineSegment seg1)
	{
		painter.setSegment1(seg1);
	}

	public void setSegment2(LineSegment seg2)
	{
		painter.setSegment2(seg2);
	}

	@Override
	public void lineChanged()
	{
		repaint();
	}

	@Override
	public void paint(Graphics graphics)
	{
		painter.setSize(getWidth(), getHeight());
		painter.paint(graphics);
	}

}
