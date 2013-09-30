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

package de.topobyte.frechet.ui.frechet.segment;

import java.awt.Graphics;

import javax.swing.JPanel;

import de.topobyte.frechet.ui.frechet.EpsilonSettable;
import de.topobyte.frechet.ui.frechet.calc.LineSegment;
import de.topobyte.frechet.ui.lineeditor.LineChangeListener;

public class SegmentPane extends JPanel implements LineChangeListener,
		EpsilonSettable
{

	private static final long serialVersionUID = 8167797259833415618L;
	
	private FreeSpacePainter painter;

	public SegmentPane(int epsilon)
	{
		painter = new FreeSpacePainter(epsilon, false, true);
	}

	public void setEpsilon(int eps)
	{
		painter.setEpsilon(eps);
		repaint();
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
