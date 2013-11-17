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
package de.topobyte.livecg.geometryeditor.geometryeditor.action.visualizations;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.livecg.algorithms.convexhull.chan.ChansAlgorithm;
import de.topobyte.livecg.algorithms.convexhull.chan.ChansAlgorithmDialog;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.ChainHelper;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.PolygonHelper;
import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.Content;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class ChansAlgorithmAction extends BasicAction
{

	private static final long serialVersionUID = 8082551211695240945L;

	private GeometryEditPane editPane;

	public ChansAlgorithmAction(GeometryEditPane editPane)
	{
		super("Chan's Algorithm", "Visualize Chan's Algorithm",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Content content = editPane.getContent();
		List<Polygon> viable = new ArrayList<Polygon>();
		for (Polygon polygon : content.getPolygons()) {
			if (polygon.getHoles().size() == 0) {
				viable.add(polygon);
			}
			// TODO: if polygon is convex
		}
		if (viable.size() < 2) {
			System.out.println("not enough viable polygon");
			return;
		}

		List<Polygon> polygons = new ArrayList<Polygon>();

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
		new ChansAlgorithmDialog(algorithm);
	}
}
