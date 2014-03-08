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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.ui.ContentLauncher;
import de.topobyte.livecg.ui.LaunchException;
import de.topobyte.livecg.ui.console.AlgorithmConsoleDialog;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class ShortestPathInPolygonLauncher implements ContentLauncher
{

	@Override
	public void launch(Content content, boolean exit) throws LaunchException
	{
		List<Polygon> polygons = content.getPolygons();
		List<Polygon> viable = new ArrayList<Polygon>();
		for (Polygon polygon : polygons) {
			if (polygon.getHoles().size() == 0) {
				viable.add(polygon);
			}
		}
		if (viable.size() < 1) {
			throw new LaunchException(
					"there is no simple polygon without holes");
		}
		Polygon polygon = viable.get(0);
		polygon = CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING);
		if (!PolygonHelper.isCounterClockwiseOriented(polygon)) {
			Chain shell = polygon.getShell();
			try {
				polygon = new Polygon(ChainHelper.invert(shell), null);
			} catch (CloseabilityException e) {
				// Should not happen
			}
		}

		PairOfNodes nodes = ShortestPathHelper.determineGoodNodes(polygon);
		Node start = nodes.getA();
		Node target = nodes.getB();

		ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm(polygon,
				start, target);
		ShortestPathDialog dialog = new ShortestPathDialog(algorithm);

		AlgorithmConsoleDialog console = new AlgorithmConsoleDialog(
				dialog.getFrame(), algorithm);
		console.setVisible(true);
		console.setLocation(dialog.getFrame().getX()
				+ dialog.getFrame().getWidth(), (int) console.getLocation()
				.getY());

		if (exit) {
			dialog.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

}
