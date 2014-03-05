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
package de.topobyte.livecg.algorithms.polygon.monotone;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.algorithm.AlgorithmChangedListener;
import de.topobyte.livecg.core.algorithm.AlgorithmWatcher;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.scrolling.ScrollableView;

public class MonotoneTriangulationDialog implements AlgorithmChangedListener,
		AlgorithmWatcher
{

	final static Logger logger = LoggerFactory
			.getLogger(MonotoneTriangulationDialog.class);

	private JFrame frame;

	private MonotoneTriangulationConfig config;
	private MonotoneTriangulationAlgorithm algorithm;

	private JSlider sliderMajor;
	private JSlider sliderMinor;
	private boolean ignoreMinorSliderEvents = false;

	private MonotoneTriangulationPanel mtp;

	public MonotoneTriangulationDialog(MonotoneTriangulationAlgorithm algorithm)
	{
		this.algorithm = algorithm;

		frame = new JFrame("Shortest Path in Polygons");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		config = new MonotoneTriangulationConfig();

		algorithm.addAlgorithmChangedListener(this);

		mtp = new MonotoneTriangulationPanel(algorithm, config);

		ScrollableView<MonotoneTriangulationPanel> scrollableView = new ScrollableView<MonotoneTriangulationPanel>(
				mtp);

		Settings settings = new Settings(algorithm, mtp, config);
		settings.setFloatable(false);

		int max = algorithm.getNumberOfSteps();
		sliderMajor = new JSlider(0, max);
		sliderMajor.setPaintTicks(true);
		sliderMajor.setMajorTickSpacing(1);
		sliderMajor.setValue(0);
		sliderMajor.setBorder(new TitledBorder("Nodes"));

		sliderMinor = new JSlider(0, 1);
		sliderMinor.setPaintTicks(true);
		sliderMinor.setMajorTickSpacing(1);
		sliderMinor.setValue(0);
		sliderMinor.setBorder(new TitledBorder("Substeps"));

		algorithm.addAlgorithmWatcher(this);

		Box north = new Box(BoxLayout.Y_AXIS);
		settings.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderMajor.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderMinor.setAlignmentX(Component.LEFT_ALIGNMENT);
		north.add(settings);
		north.add(sliderMajor);
		north.add(sliderMinor);

		main.add(north, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);
		// main.add(south, BorderLayout.SOUTH);

		/*
		 * Menu
		 */

		MonotoneTriangulationPainter painter = new MonotoneTriangulationPainter(
				algorithm, config, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportItems(menuFile, frame, painter, mtp);

		frame.setJMenuBar(menu);

		frame.setLocationByPlatform(true);
		frame.setSize(800, 700);
		frame.setVisible(true);

		sliderMajor.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				setDiagonal();
			}
		});

		sliderMinor.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (ignoreMinorSliderEvents) {
					return;
				}
				setSubStatus();
			}
		});

		updateAlgorithmStatus();
	}

	public JFrame getFrame()
	{
		return frame;
	}

	protected void setDiagonal()
	{
		int value = sliderMajor.getValue();
		if (value != algorithm.getStatus()) {
			algorithm.setStatus(value, 0);
			mtp.repaint();
		}
	}

	protected void setSubStatus()
	{
		int value = sliderMinor.getValue();
		if (value != algorithm.getSubStatus()) {
			algorithm.setSubStatus(value);
			mtp.repaint();
		}
	}

	@Override
	public void algorithmChanged()
	{
		sliderMajor.setMaximum(algorithm.getNumberOfSteps());
	}

	@Override
	public void updateAlgorithmStatus()
	{
		sliderMajor.setValue(algorithm.getStatus());
		logger.debug("Diagonal: " + algorithm.getStatus());

		int nSteps = algorithm.numberOfMinorSteps();
		logger.debug("Steps to update funnel: " + nSteps);

		logger.debug("Substatus: " + algorithm.getSubStatus());
		ignoreMinorSliderEvents = true;
		sliderMinor.setMaximum(nSteps);
		ignoreMinorSliderEvents = false;
		sliderMinor.setValue(algorithm.getSubStatus());
	}
}
