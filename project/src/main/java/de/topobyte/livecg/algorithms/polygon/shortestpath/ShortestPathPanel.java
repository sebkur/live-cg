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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import de.topobyte.livecg.core.export.SizeProvider;
import de.topobyte.livecg.core.scrolling.ScenePanel;

public class ShortestPathPanel extends ScenePanel implements SizeProvider
{

	private static final long serialVersionUID = 7441840910845794124L;

	private ShortestPathAlgorithm algorithm;

	private ShortestPathPainter visualizationPainter;

	public ShortestPathPanel(ShortestPathAlgorithm algorithm,
			ShortestPathConfig config)
	{
		super(algorithm.getScene());
		this.algorithm = algorithm;

		visualizationPainter = new ShortestPathPainter(algorithm, config,
				painter);
		super.visualizationPainter = visualizationPainter;
	}

	public ShortestPathAlgorithm getAlgorithm()
	{
		return algorithm;
	}

	public ShortestPathPainter getPainter()
	{
		return visualizationPainter;
	}

}
