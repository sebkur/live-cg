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
package de.topobyte.livecg.ui.geometryeditor.object.multiple;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.object.action.ConvexHullAction;
import de.topobyte.livecg.ui.geometryeditor.object.multiple.action.ShowFilterMenuAction;
import de.topobyte.livecg.ui.geometryeditor.object.multiple.action.ToPolygonAction;
import de.topobyte.swing.layout.GridBagHelper;

public class MultipleObjectsActionPanel extends JPanel
{

	private static final long serialVersionUID = 6408336797693213234L;

	private GeometryEditPane editPane;
	private JButton toPolygon;

	public MultipleObjectsActionPanel(GeometryEditPane editPane)
	{
		this.editPane = editPane;

		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		JButton filters = new JButton();
		ShowFilterMenuAction filtersAction = new ShowFilterMenuAction(editPane,
				filters);
		filters.setAction(filtersAction);
		filters.setMargin(new Insets(0, 0, 0, 0));

		ToPolygonAction toPolygonAction = new ToPolygonAction(editPane);
		toPolygon = new JButton(toPolygonAction);
		toPolygon.setMargin(new Insets(0, 0, 0, 0));
		toPolygon.setText(null);

		ConvexHullAction chAction = new ConvexHullAction(editPane);
		JButton convexHull = new JButton(chAction);
		convexHull.setMargin(new Insets(0, 0, 0, 0));
		convexHull.setText(null);

		c.fill = GridBagConstraints.BOTH;
		GridBagHelper.setGxGy(c, GridBagConstraints.RELATIVE, 0);
		add(filters, c);
		add(toPolygon, c);
		add(convexHull, c);
	}

	public void update()
	{
		List<Node> nodes = editPane.getCurrentNodes();
		List<Chain> chains = editPane.getCurrentChains();
		List<Polygon> polygons = editPane.getCurrentPolygons();
		boolean onlyRings = true;
		if (nodes.size() > 0 || polygons.size() > 0) {
			onlyRings = false;
		}
		if (onlyRings) {
			for (Chain chain : chains) {
				if (!chain.isClosed()) {
					onlyRings = false;
					break;
				}
			}
		}
		toPolygon.setEnabled(onlyRings);
	}
}
