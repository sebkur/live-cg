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

package de.topobyte.frechet.ui.frechet.lines;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.topobyte.frechet.ui.frechet.EpsilonSettable;
import de.topobyte.frechet.ui.frechet.calc.LineSegment;
import de.topobyte.frechet.ui.frechet.segment.SegmentPane;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;

public class FrechetDiagram extends JPanel implements EpsilonSettable
{
	private static final long serialVersionUID = 5024820193840910054L;

	private int epsilon;
	private final Chain line1;
	private final Chain line2;

	private JPanel content = new JPanel(new GridBagLayout());
	private List<SegmentPane> panes = new ArrayList<SegmentPane>();

	public FrechetDiagram(int epsilon, Chain line1, Chain line2)
	{
		this.epsilon = epsilon;
		this.line1 = line1;
		this.line2 = line2;

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.0;

		c.gridy = 1;
		c.weighty = 1.0;
		add(content, c);

		setup();
	}

	private void setup()
	{
		content.removeAll();
		panes.clear();

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		int nSegmentsP = line1.getNumberOfNodes() - 1;
		int nSegmentsQ = line2.getNumberOfNodes() - 1;

		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				LineSegment segP = getSegment(line1, x);
				LineSegment segQ = getSegment(line2, nSegmentsQ - y - 1);

				SegmentPane segmentPane = new SegmentPane(epsilon);
				segmentPane.setSegment1(segP);
				segmentPane.setSegment2(segQ);

				segmentPane.setBorder(BorderFactory
						.createLineBorder(Color.BLACK));

				panes.add(segmentPane);

				c.gridx = x;
				c.gridy = y;
				content.add(segmentPane, c);
			}
		}
	}

	private LineSegment getSegment(Chain line, int n)
	{
		Coordinate c1 = line.getCoordinate(n);
		Coordinate c2 = line.getCoordinate(n + 1);
		return new LineSegment(c1, c2);
	}

	@Override
	public void setEpsilon(int epsilon)
	{
		this.epsilon = epsilon;
		for (SegmentPane pane : panes) {
			pane.setEpsilon(epsilon);
		}
	}

	public void updateSegmentsFromLines()
	{
		System.out.println("updating segments from lines");
		int nSegmentsP = line1.getNumberOfNodes() - 1;
		int nSegmentsQ = line2.getNumberOfNodes() - 1;

		int n = 0;
		for (int x = 0; x < nSegmentsP; x++) {
			for (int y = 0; y < nSegmentsQ; y++) {
				LineSegment segP = getSegment(line1, x);
				LineSegment segQ = getSegment(line2, nSegmentsQ - y - 1);

				SegmentPane segmentPane = panes.get(n);
				segmentPane.setSegment1(segP);
				segmentPane.setSegment2(segQ);
				n++;
			}
		}
	}
}
