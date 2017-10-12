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
package de.topobyte.livecg.algorithms.convexhull.chan;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.topobyte.livecg.core.algorithm.AlgorithmWatcher;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.viewports.scrolling.ScrollableView;

public class ChansAlgorithmDialog implements AlgorithmWatcher
{

	private JFrame frame;

	private ChansAlgorithm algorithm;

	private ChansAlgorithmPanel cap;

	private JSlider sliderMajor;
	private JSlider sliderMinor;

	public ChansAlgorithmDialog(ChansAlgorithm algorithm)
	{
		this.algorithm = algorithm;

		frame = new JFrame("Chan's Algorithm");

		System.out.println(algorithm.getPolygons());
		ChanUtil chanUtil = new ChanUtil(algorithm.getPolygons());
		System.out.println("Major steps: " + chanUtil.getNumberOfMajorSteps());
		System.out.println("Total steps: " + chanUtil.getTotalNumberOfSteps());

		algorithm.addAlgorithmWatcher(this);

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		ChanConfig config = new ChanConfig();
		cap = new ChansAlgorithmPanel(algorithm, config);

		Settings toolbar = new Settings(algorithm, cap, config);
		ScrollableView<ChansAlgorithmPanel> scrollableView = new ScrollableView<>(
				cap);

		sliderMajor = new JSlider(0, chanUtil.getNumberOfMajorSteps());
		sliderMajor.setPaintTicks(true);
		sliderMajor.setMajorTickSpacing(1);
		sliderMajor.setValue(0);
		sliderMajor.setBorder(new TitledBorder("Major"));

		Box north = new Box(BoxLayout.Y_AXIS);
		toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderMajor.setAlignmentX(Component.LEFT_ALIGNMENT);
		north.add(toolbar);
		north.add(sliderMajor);

		main.add(north, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);

		/*
		 * Menu
		 */

		ChansAlgorithmPainter painter = new ChansAlgorithmPainter(algorithm,
				config, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, cap);

		frame.setJMenuBar(menu);

		frame.setLocationByPlatform(true);
		frame.setSize(700, 600);
		frame.setVisible(true);

		InputMap inputMap = main.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		ActionMap actionMap = main.getActionMap();
		actionMap.put("enter", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				ChansAlgorithmDialog.this.algorithm.nextStep();
			}
		});

		sliderMajor.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				setMajorStep();
			}
		});
	}

	public JFrame getFrame()
	{
		return frame;
	}

	protected void setMajorStep()
	{
		int value = sliderMajor.getValue();
		int hullSize = algorithm.getNumberOfNodesOnHull();
		if (value == hullSize) {
			return;
		}
		if (value > hullSize) {
			while (algorithm.getNumberOfNodesOnHull() < value) {
				algorithm.nextStep();
			}
		}
		if (value < hullSize) {
			if (value == 0) {
				while (!algorithm.isAtStart()) {
					algorithm.previousStep();
				}
			} else {
				while (algorithm.getNumberOfNodesOnHull() >= value) {
					algorithm.previousStep();
				}
				algorithm.nextStep();
			}
		}
		cap.repaint();
	}

	@Override
	public void updateAlgorithmStatus()
	{
		int n = algorithm.getNumberOfNodesOnHull();
		sliderMajor.setValue(n);
	}

}
