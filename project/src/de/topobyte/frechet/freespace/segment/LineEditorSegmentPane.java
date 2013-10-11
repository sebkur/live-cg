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

package de.topobyte.frechet.freespace.segment;

import de.topobyte.frechet.freespace.Config;
import de.topobyte.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.ui.lineeditor.LineChangeListener;
import de.topobyte.livecg.ui.lineeditor.LineEditor;

public class LineEditorSegmentPane extends SegmentPane implements
		LineChangeListener
{
	private static final long serialVersionUID = -1841371095191802602L;

	private LineEditor editor1;
	private LineEditor editor2;

	public LineEditorSegmentPane(Config config, LineEditor editor1,
			LineEditor editor2, int epsilon)
	{
		super(config, epsilon);
		this.editor1 = editor1;
		this.editor2 = editor2;
		editor1.getEditPane().addLineChangeListener(this);
		editor2.getEditPane().addLineChangeListener(this);
	}

	@Override
	public void lineChanged()
	{
		update();
	}

	public void update()
	{
		Chain line1 = editor1.getEditPane().getLine();
		Chain line2 = editor2.getEditPane().getLine();

		LineSegment seg1 = new LineSegment(line1.getFirstCoordinate(),
				line1.getLastCoordinate());
		LineSegment seg2 = new LineSegment(line2.getFirstCoordinate(),
				line2.getLastCoordinate());

		setSegment1(seg1);
		setSegment2(seg2);

		repaint();
	}

}
