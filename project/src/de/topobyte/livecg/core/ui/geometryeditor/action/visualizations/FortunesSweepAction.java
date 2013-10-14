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
package de.topobyte.livecg.core.ui.geometryeditor.action.visualizations;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.ui.action.BasicAction;
import de.topobyte.livecg.core.ui.geometryeditor.Content;
import de.topobyte.livecg.core.ui.geometryeditor.GeometryEditPane;

public class FortunesSweepAction extends BasicAction
{

	private static final long serialVersionUID = 6593980335787529594L;

	private GeometryEditPane editPane;

	public FortunesSweepAction(GeometryEditPane editPane)
	{
		super(
				"Fortune's Sweep",
				"Visualizes Fortune's Sweep line algorithm for computing the Voronoi diagram",
				"res/images/24x24/node.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Content content = editPane.getContent();

		List<Node> nodes = new ArrayList<Node>();

		for (Chain chain : content.getChains()) {
			collectNodes(nodes, chain);
		}
		for (Polygon polygon : content.getPolygons()) {
			collectNodes(nodes, polygon.getShell());
			for (Chain hole : polygon.getHoles()) {
				collectNodes(nodes, hole);
			}
		}

		FortuneDialog dialog = new FortuneDialog();
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		List<Point> sites = new ArrayList<Point>();
		for (Node node : nodes) {
			Coordinate c = node.getCoordinate();
			sites.add(new Point(c.getX(), c.getY()));
		}
		dialog.getAlgorithm().setSites(sites);
	}

	private void collectNodes(List<Node> nodes, Chain chain)
	{
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			if (!nodes.contains(node)) {
				nodes.add(node);
			}
		}
	}

}
