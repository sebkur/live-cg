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

import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.RepeatedStep;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.Step;
import de.topobyte.livecg.core.AlgorithmChangedListener;
import de.topobyte.livecg.core.AlgorithmWatcher;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.core.scrolling.ScrollableView;

public class ShortestPathDialog implements AlgorithmChangedListener,
		AlgorithmWatcher
{

	private JFrame frame;

	private Config config;
	private ShortestPathAlgorithm algorithm;

	private JSlider sliderDiagonals;
	private JSlider sliderFunnel;
	private ShortestPathPanel spp;

	public ShortestPathDialog(ShortestPathAlgorithm algorithm)
	{
		this.algorithm = algorithm;

		frame = new JFrame("Shortest Path in Polygons");

		JPanel main = new JPanel();
		frame.setContentPane(main);
		main.setLayout(new BorderLayout());

		config = new Config();
		config.setDrawDualGraph(false);

		AlgorithmMonitor algorithmMonitor = new AlgorithmMonitor();
		algorithmMonitor.addAlgorithmChangedListener(this);

		spp = new ShortestPathPanel(algorithm, config);
		PickNodesListener pickNodesListener = new PickNodesListener(spp,
				algorithmMonitor);

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

		algorithm.addWatcher(this);

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

		Rectangle bbox = BoundingBoxes.get(algorithm.getPolygon());
		Rectangle scene = Rectangles.extend(bbox, 15);

		ShortestPathPainter painter = new ShortestPathPainter(scene, algorithm,
				config, null);

		JMenuBar menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);

		ExportUtil.addExportPngZoomedItem(menuFile, frame, painter, spp);
		ExportUtil.addExportSvgZoomedItem(menuFile, frame, painter, spp);

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
			algorithm.setSubStatus(0);
			algorithm.setStatus(value);
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
		System.out.println("Diagonal: " + algorithm.getStatus());

		int nSteps = algorithm.numberOfStepsToUpdateFunnel();
		System.out.println("Steps to update funnel: " + nSteps);

		sliderFunnel.setValue(algorithm.getSubStatus());
		sliderFunnel.setMaximum(nSteps);

		List<Step> steps = algorithm.stepsToUpdateFunnel();
		for (Step step : steps) {
			if (step instanceof RepeatedStep) {
				RepeatedStep repeated = (RepeatedStep) step;
				System.out.println(step.getClass().getSimpleName() + ": "
						+ repeated.howOften());
			} else {
				System.out.println(step.getClass().getSimpleName());
			}
		}
	}
}
