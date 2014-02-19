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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.util.ImageLoader;
import de.topobyte.livecg.util.ZoomInput;

public class Settings extends JToolBar implements ItemListener
{

	private static final long serialVersionUID = 1L;

	private ChansAlgorithm algorithm;

	private String[] icons = { "res/images/24x24/media-skip-backward.png",
			"res/images/24x24/media-skip-forward.png", };
	private String[] texts = { "Prev", "Next" };

	private ChansAlgorithmPanel cap;
	private ChanConfig config;

	private JToggleButton buttonPhases;
	private JToggleButton buttonNumbers;

	public Settings(ChansAlgorithm algorithm, ChansAlgorithmPanel cap,
			ChanConfig config)
	{
		this.algorithm = algorithm;
		this.cap = cap;
		this.config = config;
		setFloatable(false);

		buttonPhases = new JToggleButton("Phases");
		buttonPhases.setSelected(config.isDrawAlgorithmPhase());
		buttonNumbers = new JToggleButton("Numbers");
		buttonNumbers.setSelected(config.isDrawPolygonNumbers());

		add(buttonPhases);
		add(buttonNumbers);

		JToggleButton buttons[] = new JToggleButton[] { buttonPhases,
				buttonNumbers };
		for (JToggleButton button : buttons) {
			button.addItemListener(this);
			button.setMaximumSize(new Dimension(button.getMaximumSize().width,
					32767));
		}

		ZoomInput zoom = new ZoomInput(cap);
		add(zoom);

		for (int i = 0; i < icons.length; i++) {
			Icon icon = ImageLoader.load(icons[i]);
			JButton button = new JButton(icon);
			button.setToolTipText(texts[i]);
			add(button);
			final int id = i;
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (id == 0) {
						previous();
					} else if (id == 1) {
						next();
					}
				}
			});
		}
	}

	protected void previous()
	{
		algorithm.previousStep();
	}

	protected void next()
	{
		algorithm.nextStep();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		JToggleButton button = (JToggleButton) e.getItem();
		boolean flag = button.isSelected();
		if (button == buttonPhases) {
			config.setDrawAlgorithmPhase(flag);
		} else if (button == buttonNumbers) {
			config.setDrawPolygonNumbers(flag);
		}

		cap.repaint();
	}
}
