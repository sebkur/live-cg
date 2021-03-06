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

import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainConfig;
import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.ui.segmenteditor.SegmentChangeListener;
import de.topobyte.livecg.ui.segmenteditor.SegmentEditor;

public class SegmentEditorSegmentPane extends SegmentPane implements
		SegmentChangeListener
{

	private static final long serialVersionUID = 5084896077938502642L;

	private SegmentEditor editor1;
	private SegmentEditor editor2;

	public SegmentEditorSegmentPane(DistanceTerrainConfig config,
			SegmentEditor editor1, SegmentEditor editor2)
	{
		super(config);
		this.editor1 = editor1;
		this.editor2 = editor2;
		editor1.getEditPane().addLineChangeListener(this);
		editor2.getEditPane().addLineChangeListener(this);
	}

	@Override
	public void segmentChanged()
	{
		update();
	}

	public void update()
	{
		Chain segment1 = editor1.getEditPane().getSegment();
		Chain segment2 = editor2.getEditPane().getSegment();

		LineSegment seg1 = new LineSegment(segment1.getFirstCoordinate(),
				segment1.getLastCoordinate());
		LineSegment seg2 = new LineSegment(segment2.getFirstCoordinate(),
				segment2.getLastCoordinate());

		setSegment1(seg1);
		setSegment2(seg2);

		repaint();
	}

}
