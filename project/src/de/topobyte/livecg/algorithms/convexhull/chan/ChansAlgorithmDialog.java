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
package de.topobyte.livecg.algorithms.convexhull.chan;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.topobyte.livecg.core.scrolling.ScrollableView;

public class ChansAlgorithmDialog
{

	private JFrame frame;

	private ChansAlgorithm algorithm;

	private ChansAlgorithmPanel cap;

	public ChansAlgorithmDialog(ChansAlgorithm algorithm)
	{
		this.algorithm = algorithm;

		frame = new JFrame("Chan's Algorithm");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		cap = new ChansAlgorithmPanel(algorithm);

		ScrollableView<ChansAlgorithmPanel> scrollableView = new ScrollableView<ChansAlgorithmPanel>(
				cap);

		main.add(scrollableView, BorderLayout.CENTER);

		frame.setLocationByPlatform(true);
		frame.setSize(700, 600);
		frame.setVisible(true);
	}

	public JFrame getFrame()
	{
		return frame;
	}

}
