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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.ui.ContentLauncher;
import de.topobyte.livecg.ui.LaunchException;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class ChanLauncher implements ContentLauncher
{

	@Override
	public void launch(Content content, boolean exit) throws LaunchException
	{
		List<Polygon> viable = new ArrayList<>();
		for (Polygon polygon : content.getPolygons()) {
			if (polygon.getHoles().size() == 0) {
				viable.add(polygon);
			}
		}
		if (viable.size() < 2) {
			throw new LaunchException("not enough convex polygons");
		}

		List<Polygon> polygons = new ArrayList<>();

		for (Polygon polygon : viable) {
			if (PolygonHelper.isCounterClockwiseOriented(polygon)) {
				polygons.add(CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));
			} else {
				Chain shell = polygon.getShell();
				try {
					polygon = new Polygon(ChainHelper.invert(shell), null);
					polygons.add(CopyUtil.copy(polygon,
							PolygonMode.REUSE_NOTHING));
				} catch (CloseabilityException e) {
					// Should not happen
				}
			}
		}

		ChansAlgorithm algorithm = new ChansAlgorithm(polygons);
		ChansAlgorithmDialog dialog = new ChansAlgorithmDialog(algorithm);

		if (exit) {
			dialog.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}

}
