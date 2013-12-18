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
package de.topobyte.livecg.datastructures.content;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.scrolling.ScrollableView;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class ContentDialog
{

	private JFrame frame;

	public ContentDialog(Content content)
	{
		frame = new JFrame("Content");
		int margin = 15;

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		ContentConfig config = new ContentConfig();
		ContentPanel contentPanel = new ContentPanel(content, config, margin);
		Settings settings = new Settings(contentPanel);

		ScrollableView<ContentPanel> scrollableView = new ScrollableView<ContentPanel>(
				contentPanel);

		main.add(settings, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		Rectangle scene = content.getScene();

		ContentPainter painter = new ContentPainter(scene, content, config,
				null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, contentPanel);
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
