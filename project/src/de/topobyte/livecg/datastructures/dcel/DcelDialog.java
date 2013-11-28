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
package de.topobyte.livecg.datastructures.dcel;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelConverter;
import de.topobyte.livecg.core.geometry.dcel.DcelUtil;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.scrolling.ScrollableView;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class DcelDialog
{

	private JFrame frame;

	public DcelDialog(Content content)
	{
		frame = new JFrame("DCEL");
		int margin = 15;

		DCEL dcel = DcelConverter.convert(content);

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		DcelConfig config = new DcelConfig();
		DcelPanel dcelPanel = new DcelPanel(dcel, config, margin);
		Settings settings = new Settings(dcelPanel);

		ScrollableView<DcelPanel> scrollableView = new ScrollableView<DcelPanel>(
				dcelPanel);

		main.add(settings, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		Rectangle bbox = DcelUtil.getBoundingBox(dcel);
		Rectangle scene = Rectangles.extend(bbox, margin);

		DcelPainter painter = new InstanceDcelPainter(scene, dcel, config, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportPngZoomedItem(menuFile, frame, painter, dcelPanel);
		ExportUtil.addExportSvgZoomedItem(menuFile, frame, painter, dcelPanel);
		ExportUtil.addExportTikzZoomedItem(menuFile, frame, painter, dcelPanel);

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
