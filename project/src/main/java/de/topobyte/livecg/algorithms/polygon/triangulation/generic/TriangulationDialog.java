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
package de.topobyte.livecg.algorithms.polygon.triangulation.generic;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesConfig;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.Settings;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.SplitResult;
import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.algorithms.polygon.util.DiagonalUtil;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.scrolling.ScrollableView;
import de.topobyte.livecg.util.graph.Graph;

public class TriangulationDialog
{

	private JFrame frame;

	public TriangulationDialog(Polygon polygon)
	{
		frame = new JFrame("Triangulation with dual graph");
		int margin = 15;

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		TriangulationOperation triangulationOperation = new TriangulationOperation(
				polygon);
		List<Diagonal> diagonals = triangulationOperation.getDiagonals();

		SplitResult splitResult = DiagonalUtil.split(polygon, diagonals);
		Graph<Polygon, Diagonal> graph = splitResult.getGraph();

		MonotonePiecesConfig config = new MonotonePiecesConfig();
		TriangulationPanel tp = new TriangulationPanel(polygon, diagonals,
				graph, config, margin);
		ScrollableView<TriangulationPanel> scrollableView = new ScrollableView<>(
				tp);

		Settings<TriangulationPanel> settings = new Settings<>(tp);

		main.add(settings, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		Rectangle bbox = BoundingBoxes.get(polygon);
		Rectangle scene = Rectangles.extend(bbox, margin);

		TriangulationPainter painter = new TriangulationPainter(scene, polygon,
				diagonals, graph, config, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, tp);

		frame.setJMenuBar(menu);

		/*
		 * Show dialog
		 */

		frame.setLocationByPlatform(true);
		frame.setSize(800, 500);
		frame.setVisible(true);
	}

	public JFrame getFrame()
	{
		return frame;
	}

}
