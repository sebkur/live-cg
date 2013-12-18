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
package de.topobyte.livecg.algorithms.polygon.monotonepieces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.scrolling.ScrollableView;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class MonotonePiecesTriangulationDialog
{

	private JFrame frame;

	public MonotonePiecesTriangulationDialog(
			MonotonePiecesTriangulationAlgorithm algorithm)
	{
		frame = new JFrame("Triangulation via monotone pieces");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		MonotonePiecesConfig polygonConfig = new MonotonePiecesConfig();
		MonotonePiecesTriangulationPanel mptp = new MonotonePiecesTriangulationPanel(
				algorithm, polygonConfig);
		ScrollableView<MonotonePiecesTriangulationPanel> scrollableView = new ScrollableView<MonotonePiecesTriangulationPanel>(
				mptp);

		Settings<MonotonePiecesTriangulationPanel> settings = new Settings<MonotonePiecesTriangulationPanel>(
				mptp);

		main.add(settings, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(algorithm
				.getExtendedGraph());

		AlgorithmPainter painter = new MonotonePiecesTriangulationPainter(
				algorithm, polygonConfig, colorMap, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, mptp);

		frame.setJMenuBar(menu);

		/*
		 * Show
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
