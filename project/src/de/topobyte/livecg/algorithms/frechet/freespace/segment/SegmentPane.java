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

package de.topobyte.livecg.algorithms.frechet.freespace.segment;

import java.awt.Graphics;

import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.frechet.freespace.Config;
import de.topobyte.livecg.algorithms.frechet.freespace.EpsilonSettable;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpacePainter;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.FreeSpaceUtil;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.Interval;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.geometryeditor.lineeditor.LineChangeListener;
import de.topobyte.livecg.util.DoubleUtil;

public class SegmentPane extends JPanel implements LineChangeListener,
		EpsilonSettable
{

	private static final long serialVersionUID = 8167797259833415618L;

	private FreeSpacePainter painter;

	private int epsilon;
	private LineSegment seg1;
	private LineSegment seg2;

	public SegmentPane(Config config, int epsilon)
	{
		this.epsilon = epsilon;
		painter = new FreeSpacePainter(config, epsilon);
		updateReachableSpace();
	}

	public void setEpsilon(int epsilon)
	{
		this.epsilon = epsilon;
		painter.setEpsilon(epsilon);
		updateReachableSpace();
		repaint();
	}

	public void setSegment1(LineSegment seg1)
	{
		this.seg1 = seg1;
		painter.setSegment1(seg1);
		updateReachableSpace();
	}

	public void setSegment2(LineSegment seg2)
	{
		this.seg2 = seg2;
		painter.setSegment2(seg2);
		updateReachableSpace();
	}

	@Override
	public void lineChanged()
	{
		updateReachableSpace();
		repaint();
	}

	@Override
	public void paint(Graphics graphics)
	{
		painter.setSize(getWidth(), getHeight());
		painter.paint(graphics);
	}

	private void updateReachableSpace()
	{
		if (seg1 == null || seg2 == null) {
			return;
		}
		Interval BF1 = FreeSpaceUtil // bottom
				.freeSpace(seg2, seg1, 0, epsilon);
		Interval LF1 = FreeSpaceUtil // left
				.freeSpace(seg1, seg2, 0, epsilon);

		double ls = LF1.getStart();
		double le = LF1.getEnd();
		double bs = BF1.getStart();
		double be = BF1.getEnd();
		Interval BR1 = null;
		Interval LR1 = null;
		if (DoubleUtil.isValid(ls) && ls <= 0 && le >= 0) {
			LR1 = new Interval(LF1.getStart(), LF1.getEnd());
		}
		if (DoubleUtil.isValid(bs) && bs <= 0 && be >= 0) {
			BR1 = new Interval(BF1.getStart(), BF1.getEnd());
		}

		painter.setBR1(BR1);
		painter.setLR1(LR1);
	}

}
