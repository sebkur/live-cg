/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
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
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.scrolling.ScrollableView;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class MonotonePiecesDialog
{

	private JFrame frame;

	public MonotonePiecesDialog(MonotonePiecesAlgorithm algorithm)
	{
		frame = new JFrame("Monotone pieces");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		Config polygonConfig = new Config();
		MonotonePiecesPanel mpp = new MonotonePiecesPanel(algorithm);
		ScrollableView<MonotonePiecesPanel> scrollableView = new ScrollableView<MonotonePiecesPanel>(
				mpp);

		Settings<MonotonePiecesPanel> settings = new Settings<MonotonePiecesPanel>(
				mpp);

		main.add(settings, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(algorithm
				.getExtendedGraph());

		Rectangle bbox = BoundingBoxes.get(algorithm.getPolygon());
		Rectangle scene = Rectangles.extend(bbox, 15);

		AlgorithmPainter painter = new MonotonePiecesPainter(scene, algorithm,
				polygonConfig, colorMap, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportPngItem(menuFile, frame, painter, mpp);
		ExportUtil.addExportSvgItem(menuFile, frame, painter, mpp);

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
