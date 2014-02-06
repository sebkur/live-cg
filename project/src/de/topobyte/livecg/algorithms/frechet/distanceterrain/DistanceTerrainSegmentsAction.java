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
package de.topobyte.livecg.algorithms.frechet.distanceterrain;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.ui.action.BasicAction;

public class DistanceTerrainSegmentsAction extends BasicAction
{

	private static final long serialVersionUID = 3999319262987241522L;

	public DistanceTerrainSegmentsAction()
	{
		super(
				"Distance Terrain (segments)",
				"Visualize the Distance Terrain used to compute the Fréchet Distance",
				"res/images/24x24/way.png");
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		new DistanceTerrainSegmentsLauncher().launch();
	}

}