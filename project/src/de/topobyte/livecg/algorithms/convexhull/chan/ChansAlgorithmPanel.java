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

import de.topobyte.livecg.core.AlgorithmWatcher;
import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.scrolling.ScenePanel;

public class ChansAlgorithmPanel extends ScenePanel implements SizeProvider,
		AlgorithmWatcher
{
	private static final long serialVersionUID = -788826737883369137L;

	public ChansAlgorithmPanel(ChansAlgorithm algorithm)
	{
		super(algorithm.getScene());

		algorithmPainter = new ChansAlgorithmPainter(algorithm, painter);

		algorithm.addAlgorithmWatcher(this);
	}

	@Override
	public void updateAlgorithmStatus()
	{
		repaint();
	}

}
