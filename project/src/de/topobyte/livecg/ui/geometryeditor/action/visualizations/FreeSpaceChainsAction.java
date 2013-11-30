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
package de.topobyte.livecg.ui.geometryeditor.action.visualizations;

import java.awt.event.ActionEvent;
import java.util.List;

import de.topobyte.livecg.algorithms.frechet.ui.FreeSpaceDialog1;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class FreeSpaceChainsAction extends BasicAction
{

	private static final long serialVersionUID = 4601842937930128005L;

	private GeometryEditPane editPane;

	public FreeSpaceChainsAction(GeometryEditPane editPane)
	{
		super(
				"Free Space (chains)",
				"Visualize the Free Space Diagram used to compute the Fréchet Distance",
				"res/images/24x24/way.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Content content = editPane.getContent();
		List<Chain> chains = content.getChains();
		if (chains.size() < 2) {
			System.out.println("not enough chains");
			return;
		}
		new FreeSpaceDialog1(content);
	}

}
