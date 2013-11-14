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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.util.ImageLoader;

public class Toolbar extends JToolBar
{

	private static final long serialVersionUID = 1L;

	private ChansAlgorithm algorithm;

	private String[] icons = { "res/images/24x24/media-skip-backward.png",
			"res/images/24x24/media-skip-forward.png", };
	private String[] texts = { "Prev", "Next" };

	public Toolbar(ChansAlgorithm algorithm)
	{
		this.algorithm = algorithm;
		setFloatable(false);
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

}
