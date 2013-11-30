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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

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
import de.topobyte.livecg.core.algorithm.steps.RepeatedStep;
import de.topobyte.livecg.core.algorithm.steps.Step;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.scrolling.ScrollableView;

public class ShortestPathDialog implements AlgorithmChangedListener,
		AlgorithmWatcher
{

	final static Logger logger = LoggerFactory
			.getLogger(ShortestPathDialog.class);

	private JFrame frame;

	private ShortestPathConfig config;
	private ShortestPathAlgorithm algorithm;

	private JSlider sliderDiagonals;
	private JSlider sliderFunnel;
	private boolean ignoreFunnelSliderEvents = false;

	private ShortestPathPanel spp;

	public ShortestPathDialog(ShortestPathAlgorithm algorithm)
	{
		this.algorithm = algorithm;

		frame = new JFrame("Shortest Path in Polygons");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		config = new ShortestPathConfig();
		config.setDrawDualGraph(false);

		algorithm.addAlgorithmChangedListener(this);

		spp = new ShortestPathPanel(algorithm, config);
		PickNodesListener pickNodesListener = new PickNodesListener(spp);

		ScrollableView<ShortestPathPanel> scrollableView = new ScrollableView<ShortestPathPanel>(
				spp);

		scrollableView.addMouseListener(pickNodesListener);
		scrollableView.addMouseMotionListener(pickNodesListener);

		Settings settings = new Settings(algorithm, spp, config);

		int max = algorithm.getNumberOfSteps();
		sliderDiagonals = new JSlider(0, max);
		sliderDiagonals.setPaintTicks(true);
		sliderDiagonals.setMajorTickSpacing(1);
		sliderDiagonals.setValue(0);
		sliderDiagonals.setBorder(new TitledBorder("Diagonals"));

		sliderFunnel = new JSlider(0, 1);
		sliderFunnel.setPaintTicks(true);
		sliderFunnel.setMajorTickSpacing(1);
		sliderFunnel.setValue(0);
		sliderFunnel.setBorder(new TitledBorder("Funnel"));

		algorithm.addAlgorithmWatcher(this);

		Box north = new Box(BoxLayout.Y_AXIS);
		settings.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderDiagonals.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderFunnel.setAlignmentX(Component.LEFT_ALIGNMENT);
		north.add(settings);
		north.add(sliderDiagonals);
		north.add(sliderFunnel);

		main.add(north, BorderLayout.NORTH);
		main.add(scrollableView, BorderLayout.CENTER);
		// main.add(south, BorderLayout.SOUTH);

		/*
		 * Menu
		 */

		ShortestPathPainter painter = new ShortestPathPainter(algorithm,
				config, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportPngZoomedItem(menuFile, frame, painter, spp);
		ExportUtil.addExportSvgZoomedItem(menuFile, frame, painter, spp);
		ExportUtil.addExportTikzZoomedItem(menuFile, frame, painter, spp);

		frame.setJMenuBar(menu);

		frame.setLocationByPlatform(true);
		frame.setSize(800, 700);
		frame.setVisible(true);

		sliderDiagonals.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				setDiagonal();
			}
		});

		sliderFunnel.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (ignoreFunnelSliderEvents) {
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
		int value = sliderDiagonals.getValue();
		if (value != algorithm.getStatus()) {
			algorithm.setStatus(value, 0);
			spp.repaint();
		}
	}

	protected void setSubStatus()
	{
		int value = sliderFunnel.getValue();
		if (value != algorithm.getSubStatus()) {
			algorithm.setSubStatus(value);
			spp.repaint();
		}
	}

	@Override
	public void algorithmChanged()
	{
		sliderDiagonals.setMaximum(algorithm.getNumberOfSteps());
	}

	@Override
	public void updateAlgorithmStatus()
	{
		sliderDiagonals.setValue(algorithm.getStatus());
		logger.debug("Diagonal: " + algorithm.getStatus());

		int nSteps = algorithm.numberOfStepsToNextDiagonal();
		logger.debug("Steps to update funnel: " + nSteps);

		logger.debug("Substatus: " + algorithm.getSubStatus());
		ignoreFunnelSliderEvents = true;
		sliderFunnel.setMaximum(nSteps);
		ignoreFunnelSliderEvents = false;
		sliderFunnel.setValue(algorithm.getSubStatus());

		List<Step> steps = algorithm.stepsToNextDiagonal();
		for (Step step : steps) {
			if (step instanceof RepeatedStep) {
				RepeatedStep repeated = (RepeatedStep) step;
				logger.debug(step.getClass().getSimpleName() + ": "
						+ repeated.howOften());
			} else {
				logger.debug(step.getClass().getSimpleName());
			}
		}
	}
}
