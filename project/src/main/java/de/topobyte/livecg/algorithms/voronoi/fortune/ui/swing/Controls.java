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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.util.ImageLoader;

public class Controls extends JToolBar implements ActionListener
{

	private static final long serialVersionUID = -8452143409724541737L;

	private FortuneDialog fortune;

	private FortunesSweep algorithm;

	private JButton buttons[];

	private static String KEY_PLAY = "play";
	private static String KEY_PLAY_REVERSE = "play-reverse";
	private static String KEY_PAUSE = "pause";
	private static String KEY_PREVIOUS_EVENT = "previous-event";
	private static String KEY_NEXT_EVENT = "next-event";
	private static String KEY_PREV_PIXEL = "previous-pixel";
	private static String KEY_NEXT_PIXEL = "next-pixel";
	private static String KEY_CLEAR = "clear";
	private static String KEY_RESTART = "restart";

	private static String KEY_ADD_RANDOM = "add-random";

	private static Map<String, String> texts = new HashMap<String, String>();
	static {
		texts.put(KEY_PLAY, "Play");
		texts.put(KEY_PLAY_REVERSE, "Play Reverse");
		texts.put(KEY_PAUSE, "Pause");
		texts.put(KEY_PREVIOUS_EVENT, "Previous event");
		texts.put(KEY_NEXT_EVENT, "Next event");
		texts.put(KEY_PREV_PIXEL, "Previous pixel");
		texts.put(KEY_NEXT_PIXEL, "Next pixel");
		texts.put(KEY_CLEAR, "Clear");
		texts.put(KEY_RESTART, "Restart");
		texts.put(KEY_ADD_RANDOM, "Add random points");
	}

	private static Map<String, String> paths = new HashMap<String, String>();
	static {
		paths.put(KEY_PLAY, "res/images/24x24/media-playback-start.png");
		paths.put(KEY_PLAY_REVERSE,
				"res/images/24x24/media-playback-reverse.png");
		paths.put(KEY_PAUSE, "res/images/24x24/media-playback-pause.png");
		paths.put(KEY_PREVIOUS_EVENT,
				"res/images/24x24/media-skip-backward.png");
		paths.put(KEY_NEXT_EVENT, "res/images/24x24/media-skip-forward.png");
		paths.put(KEY_PREV_PIXEL, "res/images/24x24/media-seek-backward.png");
		paths.put(KEY_NEXT_PIXEL, "res/images/24x24/media-seek-forward.png");
		paths.put(KEY_CLEAR, "res/images/24x24/media-eject.png");
		paths.put(KEY_RESTART, "res/images/24x24/media-playback-stop.png");
		paths.put(KEY_ADD_RANDOM, null);
	}

	private Map<String, Icon> icons = new HashMap<String, Icon>();

	public Controls(FortuneDialog fortune, FortunesSweep algorithm)
	{
		this.fortune = fortune;
		this.algorithm = algorithm;

		setFloatable(false);

		for (String key : paths.keySet()) {
			Icon icon = ImageLoader.load(paths.get(key));
			icons.put(key, icon);
		}

		Map<String, JButton> buttonMap = new HashMap<String, JButton>();

		String keys[] = { KEY_PLAY, KEY_PLAY_REVERSE, KEY_PREVIOUS_EVENT,
				KEY_NEXT_EVENT, KEY_PREV_PIXEL, KEY_NEXT_PIXEL, KEY_RESTART,
				KEY_CLEAR, KEY_ADD_RANDOM };
		buttons = new JButton[keys.length];
		for (int i = 0; i < keys.length; i++) {
			buttons[i] = new JButton(icons.get(keys[i]));
			buttons[i].setToolTipText(texts.get(keys[i]));
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			buttons[i].addActionListener(this);
			add(buttons[i]);
			buttonMap.put(keys[i], buttons[i]);
		}

		JButton buttonRandom = buttonMap.get(KEY_ADD_RANDOM);

		buttonRandom.setText("add random");
		buttonRandom.setMaximumSize(new Dimension(
				buttonRandom.getMaximumSize().width, 32767));

		buttonRandom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Controls.this.fortune.getCanvas().addRandomPoints();
			}
		});

		threadRunning(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int i = 0;
		if (e.getSource() == buttons[i++]) {
			if (fortune.isForeward()) {
				boolean running = fortune.toggleRunning();
				threadRunning(running);
			} else {
				fortune.setForeward(true);
				if (!fortune.isRunning()) {
					fortune.toggleRunning();
				}
			}
			threadRunning(fortune.isRunning());
		} else if (e.getSource() == buttons[i++]) {
			if (!fortune.isForeward()) {
				boolean running = fortune.toggleRunning();
				threadRunning(running);
			} else {
				fortune.setForeward(false);
				if (!fortune.isRunning()) {
					fortune.toggleRunning();
				}
			}
			threadRunning(fortune.isRunning());
		} else if (e.getSource() == buttons[i++]) {
			algorithm.previousEvent();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.nextEvent();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.previousPixel();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.nextPixel();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.restart();
		} else if (e.getSource() == buttons[i++]) {
			fortune.stopRunning();
			threadRunning(false);
			algorithm.clear();
		}
	}

	public void threadRunning(boolean running)
	{
		if (running) {
			if (fortune.isForeward()) {
				set(buttons[0], KEY_PAUSE);
				set(buttons[1], KEY_PLAY_REVERSE);
			} else {
				set(buttons[0], KEY_PLAY);
				set(buttons[1], KEY_PAUSE);
			}
			buttons[4].setEnabled(false);
			buttons[5].setEnabled(false);
		} else {
			set(buttons[0], KEY_PLAY);
			set(buttons[1], KEY_PLAY_REVERSE);
			buttons[4].setEnabled(true);
			buttons[5].setEnabled(true);
		}
		buttons[0].invalidate();
		invalidate();
		validate();
	}

	private void set(JButton button, String key)
	{
		button.setToolTipText(texts.get(key));
		button.setIcon(icons.get(key));
	}
}
