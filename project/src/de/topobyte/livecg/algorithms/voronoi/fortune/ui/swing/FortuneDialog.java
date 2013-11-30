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
package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortuneConfig;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.core.FortunePainter;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.action.OpenAction;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.action.SaveAction;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.eventqueue.EventQueueDialog;
import de.topobyte.livecg.core.config.LiveConfig;
import de.topobyte.livecg.core.export.ExportUtil;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.ui.action.QuitAction;

public class FortuneDialog extends JFrame implements Runnable
{

	private static final long serialVersionUID = 3917389635770683885L;

	private Color COLOR_SWEEP_CONTROL_BORDER = LiveConfig
			.getColor("algorithm.voronoi.fortune.colors.sweep.control.border");

	private FortunesSweep algorithm;
	private Canvas canvas;
	private Controls controls;
	private FortuneConfig config;

	private EventQueueDialog eventQueueDialog;

	private JPanel main;
	private JMenuBar menu;
	private Settings settings;

	private boolean running = false;
	private boolean foreward = true;
	private Thread thread;
	private Object wait = new Object();

	public FortuneDialog()
	{
		super("Fortune's sweep");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		init();
	}

	public void init()
	{
		/*
		 * Components, layout
		 */

		main = new JPanel();
		setContentPane(main);
		main.setLayout(new BorderLayout());

		algorithm = new FortunesSweep();
		config = new FortuneConfig();

		config.setDrawCircles(true);
		config.setDrawBeach(true);
		config.setDrawVoronoiLines(true);
		config.setDrawDelaunay(false);

		canvas = new Canvas(algorithm, config, getWidth(), getHeight() - 50);
		controls = new Controls(this, algorithm);
		settings = new Settings(canvas, config);

		SweepControl sweepControl = new SweepControl(algorithm);
		sweepControl.setBorder(BorderFactory
				.createLineBorder(new java.awt.Color(COLOR_SWEEP_CONTROL_BORDER
						.getARGB(), true)));

		Box north = new Box(BoxLayout.Y_AXIS);
		controls.setAlignmentX(Component.LEFT_ALIGNMENT);
		settings.setAlignmentX(Component.LEFT_ALIGNMENT);
		north.add(controls);
		north.add(settings);

		main.add(north, BorderLayout.NORTH);
		main.add(canvas, BorderLayout.CENTER);
		main.add(sweepControl, BorderLayout.SOUTH);

		algorithm.addAlgorithmWatcher(canvas);
		algorithm.addAlgorithmWatcher(sweepControl);

		canvas.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				algorithm.setWidth(canvas.getWidth());
				algorithm.setHeight(canvas.getHeight());
			}

		});

		/*
		 * Menus
		 */

		FortunePainter painter = new FortunePainter(algorithm, config, null);

		menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);
		JMenuItem open = new JMenuItem(new OpenAction(this));
		menuFile.add(open);
		JMenuItem save = new JMenuItem(new SaveAction(this));
		menuFile.add(save);

		ExportUtil.addExportPngItem(menuFile, this, painter, canvas);
		ExportUtil.addExportSvgItem(menuFile, this, painter, canvas);
		ExportUtil.addExportTikzItem(menuFile, this, painter, canvas);

		JMenuItem quit = new JMenuItem(new QuitAction());
		menuFile.add(quit);

		setJMenuBar(menu);

		setLocationByPlatform(true);
		setSize(800, 600);
		setVisible(true);

		/*
		 * EventQueue dialog
		 */

		eventQueueDialog = new EventQueueDialog(this, algorithm);
		eventQueueDialog.setVisible(true);
		eventQueueDialog.setLocation(getX() + getWidth(), (int) getLocation()
				.getY());

		/*
		 * Start thread
		 */

		thread = new Thread(this);
		thread.start();
	}

	public boolean toggleRunning()
	{
		if (running) {
			running = false;
		} else {
			if (!algorithm.isFinshed()) {
				running = true;
				synchronized (wait) {
					wait.notify();
				}
			}
		}
		return running;
	}

	public void stopRunning()
	{
		if (!running) {
			return;
		}
		running = false;
	}

	@Override
	public void run()
	{
		while (true) {
			if (running) {
				boolean eventsLeft;
				if (foreward) {
					eventsLeft = algorithm.nextPixel();
				} else {
					eventsLeft = algorithm.previousPixel();
				}
				if (!eventsLeft) {
					setPaused();
				}
				try {
					Thread.sleep(25L);
				} catch (InterruptedException ex) {
					// ignore
				}
			} else {
				setPaused();
			}
		}
	}

	private void setPaused()
	{
		running = false;
		controls.threadRunning(false);
		while (true) {
			try {
				synchronized (wait) {
					wait.wait();
				}
				controls.threadRunning(true);
				break;
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	/*
	 * Open / Save dialogs related stuff
	 */

	private File lastActiveDirectory = null;

	public File getLastActiveDirectory()
	{
		return lastActiveDirectory;
	}

	public void setLastActiveDirectory(File lastActiveDirectory)
	{
		this.lastActiveDirectory = lastActiveDirectory;
	}

	/*
	 * Various
	 */

	public FortunesSweep getAlgorithm()
	{
		return algorithm;
	}

	public void setForeward(boolean foreward)
	{
		this.foreward = foreward;
	}

	public boolean isForeward()
	{
		return foreward;
	}

	public boolean isRunning()
	{
		return running;
	}

	public Canvas getCanvas()
	{
		return canvas;
	}

	public Dimension getCanvasSize()
	{
		return canvas.getSize();
	}

	public FortuneConfig getConfig()
	{
		return config;
	}
}
