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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import de.topobyte.livecg.algorithms.frechet.freespace.ConfigChangedListener;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpaceConfig;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpaceDiagram;
import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpacePainterChains;
import de.topobyte.livecg.algorithms.frechet.freespace.Settings;
import de.topobyte.livecg.algorithms.frechet.ui.lineview.ControlledLineSegmentView;
import de.topobyte.livecg.algorithms.frechet.ui.lineview.LineSegmentView;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.ContentChangedListener;

public class FreeSpaceDialog2 implements ContentChangedListener
{

	private JFrame frame;

	final static int STEP_SIZE = 1;
	final static int STEP_SIZE_BIG = 10;

	private FreeSpaceDiagram diagram = null;
	private LineSegmentView segmentView = null;

	private int epsilon = 100;
	private Chain chain1 = null;
	private Chain chain2 = null;

	public FreeSpaceDialog2(final Content content)
	{
		List<Chain> chains = content.getChains();
		if (chains.size() < 2) {
			System.out.println("not enough chains");
			return;
		}
		System.out.println("showing frechet diagram");

		chain1 = chains.get(0);
		chain2 = chains.get(1);

		int maxEpsilon = 300;
		final JSlider slider = new JSlider(0, maxEpsilon);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		slider.setValue(epsilon);
		slider.setBorder(new TitledBorder("epsilon"));

		FreeSpaceConfig config = new FreeSpaceConfig();
		Settings settings = new Settings(config);

		config.addConfigChangedListener(new ConfigChangedListener() {

			@Override
			public void configChanged()
			{
				diagram.repaint();
			}
		});

		diagram = new FreeSpaceDiagram(config, epsilon, chain1, chain2);
		JPanel diagramPanel = new JPanel(new BorderLayout());
		diagramPanel.add(settings, BorderLayout.NORTH);
		diagramPanel.add(diagram, BorderLayout.CENTER);
		diagramPanel.setBorder(new TitledBorder("Free space"));

		segmentView = new LineSegmentView(epsilon, chain1, chain2, true, false,
				true, false);
		ControlledLineSegmentView controlledView = new ControlledLineSegmentView(
				segmentView);
		controlledView.setBorder(new TitledBorder("Curves"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1.0;
		JPanel panel = new JPanel(new GridBagLayout());
		c.weighty = 0.0;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		panel.add(slider, c);

		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridy = 1;
		c.gridx = 0;
		panel.add(diagramPanel, c);
		c.weightx = 0.0;
		c.gridx = 1;
		panel.add(controlledView, c);

		frame = new JFrame("Fréchet distance");
		frame.setContentPane(panel);

		/*
		 * Menus
		 */

		FreeSpacePainterChains painter = new FreeSpacePainterChains(config,
				epsilon, chain1, chain2, null);

		JMenuBar menu = new JMenuBar();
		frame.setJMenuBar(menu);

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, diagram);

		/*
		 * Misc
		 */

		frame.setSize(850, 450);
		frame.setVisible(true);

		slider.addChangeListener(new EpsilonChangedListener(diagram));
		slider.addChangeListener(new EpsilonChangedListener(segmentView));
		slider.addChangeListener(new EpsilonChangedListener(painter));

		content.addContentChangedListener(this);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				content.removeContentChangedListener(FreeSpaceDialog2.this);
			}
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent e)
			{
				if (e.getSource() != frame) {
					return;
				}

				MouseWheelEvent event = (MouseWheelEvent) e;

				int modifiers = event.getModifiers();
				boolean big = (modifiers & InputEvent.CTRL_MASK) != 0;

				int rotation = event.getWheelRotation();
				int value = slider.getValue();
				int newValue = value + rotation
						* (big ? STEP_SIZE_BIG : STEP_SIZE);
				slider.setValue(newValue);
			}
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	@Override
	public void contentChanged()
	{
		diagram.repaint();
		segmentView.repaint();
	}

	@Override
	public void dimensionChanged()
	{
		// ignore
	}

	public JFrame getFrame()
	{
		return frame;
	}
}
