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
package de.topobyte.livecg.algorithms.jts.buffer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.ConfigChangedListener;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.scrolling.ScrollableView;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentChangedListener;

public class BufferDialog implements ContentChangedListener, ChangeListener
{

	private JFrame frame;

	private JSlider slider;
	private BufferPanel bufferPanel = null;

	private BufferConfig config;

	private BufferAlgorithm algorithm;

	public BufferDialog(final Content content)
	{
		config = new BufferConfig();

		algorithm = new BufferAlgorithm(content, config.getDistance());

		int minValue = -200;
		int maxValue = 500;
		slider = new JSlider(minValue, maxValue);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(100);
		slider.setValue(config.getDistance());
		slider.setBorder(new TitledBorder("Distance"));

		Settings settings = new Settings(config);

		config.addConfigChangedListener(new ConfigChangedListener() {

			@Override
			public void configChanged()
			{
				algorithm.setDistance(config.getDistance());
			}
		});

		bufferPanel = new BufferPanel(algorithm, config);
		ScrollableView<BufferPanel> scrollableView = new ScrollableView<BufferPanel>(
				bufferPanel);

		JPanel diagramPanel = new JPanel(new BorderLayout());
		diagramPanel.add(settings, BorderLayout.NORTH);
		diagramPanel.add(scrollableView, BorderLayout.CENTER);
		diagramPanel.setBorder(new TitledBorder("Polygon buffer"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		JPanel panel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		panel.add(slider, c);
		c.gridy = 1;
		c.weighty = 1.0;
		panel.add(diagramPanel, c);

		frame = new JFrame("Polygon buffer");
		frame.setContentPane(panel);

		/*
		 * Menus
		 */

		BufferPainter painter = new BufferPainter(algorithm, config, null);

		JMenuBar menu = new JMenuBar();
		frame.setJMenuBar(menu);

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportZoomedItems(menuFile, frame, painter, bufferPanel);

		/*
		 * Misc
		 */

		frame.setLocationByPlatform(true);
		frame.setSize(500, 600);
		frame.setVisible(true);

		slider.addChangeListener(this);

		content.addContentChangedListener(this);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				content.removeContentChangedListener(BufferDialog.this);
			}
		});

	}

	@Override
	public void contentChanged()
	{
		algorithm.update();
		bufferPanel.repaint();
	}

	public JFrame getFrame()
	{
		return frame;
	}

	@Override
	public void dimensionChanged()
	{
		// ignore
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		config.setDistance(slider.getValue());
		config.fireConfigChanged();
	}
}
