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
package de.topobyte.livecg.algorithms.frechet.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.topobyte.livecg.algorithms.frechet.distanceterrain.ConfigChangedListener;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrain;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainConfig;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.DistanceTerrainPainterChains;
import de.topobyte.livecg.algorithms.frechet.distanceterrain.Settings;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentChangedListener;

public class DistanceTerrainDialog implements ContentChangedListener,
		ChangeListener
{

	private JFrame frame;

	private JSlider slider;
	private DistanceTerrain diagram = null;

	private DistanceTerrainConfig config;

	private Chain chain1 = null;
	private Chain chain2 = null;

	public DistanceTerrainDialog(final Content content)
	{
		List<Chain> chains = content.getChains();
		if (chains.size() < 2) {
			System.out.println("not enough chains");
			return;
		}
		System.out.println("showing frechet diagram");

		chain1 = chains.get(0);
		chain2 = chains.get(1);

		config = new DistanceTerrainConfig();

		int minValue = 100;
		int maxValue = 1000;
		slider = new JSlider(minValue, maxValue);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(100);
		slider.setValue(config.getScale());
		slider.setBorder(new TitledBorder("Scale"));

		Settings settings = new Settings(config);

		config.addConfigChangedListener(new ConfigChangedListener() {

			@Override
			public void configChanged()
			{
				diagram.repaint();
			}
		});

		diagram = new DistanceTerrain(config, chain1, chain2);
		JPanel diagramPanel = new JPanel(new BorderLayout());
		diagramPanel.add(settings, BorderLayout.NORTH);
		diagramPanel.add(diagram, BorderLayout.CENTER);
		diagramPanel.setBorder(new TitledBorder("Distance terrain"));

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

		frame = new JFrame("Fréchet distance");
		frame.setContentPane(panel);

		/*
		 * Menus
		 */

		DistanceTerrainPainterChains painter = new DistanceTerrainPainterChains(
				config, chain1, chain2, null);

		JMenuBar menu = new JMenuBar();
		frame.setJMenuBar(menu);

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, diagram);

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
				content.removeContentChangedListener(DistanceTerrainDialog.this);
			}
		});

	}

	@Override
	public void contentChanged()
	{
		diagram.update();
		diagram.repaint();
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
		config.setScale(slider.getValue());
		config.fireConfigChanged();
	}
}
