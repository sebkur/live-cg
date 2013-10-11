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

package de.topobyte.livecg.algorithms.frechet.lineview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class LineViewControl extends JPanel
{

	private static final long serialVersionUID = 8617637401144584172L;

	public LineViewControl(final LineView lineView)
	{
		setBorder(new TitledBorder("Buffers"));
		final JCheckBox checkPointP = new JCheckBox("points p");
		final JCheckBox checkPointQ = new JCheckBox("points q");
		final JCheckBox checkLineP = new JCheckBox("segments p");
		final JCheckBox checkLineQ = new JCheckBox("segments q");
		add(checkPointP);
		add(checkPointQ);
		add(checkLineP);
		add(checkLineQ);

		checkPointP.setSelected(lineView.isDrawPointBufferP());
		checkPointQ.setSelected(lineView.isDrawPointBufferQ());
		checkLineP.setSelected(lineView.isDrawSegmentBufferP());
		checkLineQ.setSelected(lineView.isDrawSegmentBufferQ());

		checkPointP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				lineView.setDrawPointBufferP(checkPointP.isSelected());
				lineView.repaint();
			}
		});
		checkPointQ.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				lineView.setDrawPointBufferQ(checkPointQ.isSelected());
				lineView.repaint();
			}
		});
		checkLineP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				lineView.setDrawSegmentBufferP(checkLineP.isSelected());
				lineView.repaint();
			}
		});
		checkLineQ.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				lineView.setDrawSegmentBufferQ(checkLineQ.isSelected());
				lineView.repaint();
			}
		});
	}
}
