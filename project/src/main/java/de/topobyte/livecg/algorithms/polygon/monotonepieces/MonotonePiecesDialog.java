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
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;
import de.topobyte.viewports.scrolling.ScrollableView;

public class MonotonePiecesDialog
{

	private JFrame frame;

	public MonotonePiecesDialog(MonotonePiecesAlgorithm algorithm)
	{
		frame = new JFrame("Monotone pieces");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		MonotonePiecesConfig polygonConfig = new MonotonePiecesConfig();
		MonotonePiecesPanel mpp = new MonotonePiecesPanel(algorithm,
				polygonConfig);
		ScrollableView<MonotonePiecesPanel> scrollableView = new ScrollableView<>(
				mpp);

		Settings<MonotonePiecesPanel> settings = new Settings<>(mpp);

		main.add(settings, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		Map<Polygon, ColorCode> colorMap = ColorMapBuilder
				.buildColorMap(algorithm.getExtendedGraph());

		VisualizationPainter painter = new MonotonePiecesPainter(algorithm,
				polygonConfig, colorMap, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, mpp);

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
