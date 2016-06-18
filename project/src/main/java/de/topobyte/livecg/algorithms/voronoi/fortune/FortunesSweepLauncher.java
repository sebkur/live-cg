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
package de.topobyte.livecg.algorithms.voronoi.fortune;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.ui.ContentLauncher;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentHelper;

public class FortunesSweepLauncher implements ContentLauncher
{

	@Override
	public void launch(Content content, boolean exit)
	{
		List<Node> nodes = ContentHelper.collectNodes(content);

		FortuneDialog dialog = new FortuneDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		List<Point> sites = new ArrayList<>();
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			sites.add(new Point(c.getX(), c.getY()));
		}
		dialog.getAlgorithm().setSites(sites);

		if (exit) {
			dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

}
