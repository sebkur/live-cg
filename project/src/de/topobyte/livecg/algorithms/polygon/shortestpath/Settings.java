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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.core.algorithm.AlgorithmWatcher;
import de.topobyte.livecg.util.ImageLoader;
import de.topobyte.livecg.util.ZoomInput;

public class Settings extends JToolBar implements ItemListener,
		AlgorithmWatcher
{

	private static final long serialVersionUID = -6537449209660520005L;

	private ShortestPathAlgorithm algorithm;
	private ShortestPathPanel spp;
	private Config config;

	private JToggleButton[] buttons;
	private JButton[] control;
	private Map<String, JButton> controlMap = new HashMap<String, JButton>();

	private static final String TEXT_DRAW_DUAL_GRAPH = "Dual Graph";

	private static final String TEXT_PREVIOUS_DIAG = "Previous diagonal";
	private static final String TEXT_NEXT_DIAG = "Next diagonal";
	private static final String TEXT_PREVIOUS = "Previous step";
	private static final String TEXT_NEXT = "Next step";

	private static final String[] names = { TEXT_PREVIOUS_DIAG, TEXT_NEXT_DIAG,
			TEXT_PREVIOUS, TEXT_NEXT };
	private static final String[] images = {
			"res/images/24x24/media-skip-backward.png",
			"res/images/24x24/media-skip-forward.png",
			"res/images/24x24/media-seek-backward.png",
			"res/images/24x24/media-seek-forward.png" };

	public Settings(ShortestPathAlgorithm algorithm, ShortestPathPanel spp,
			Config config)
	{
		this.algorithm = algorithm;
		this.spp = spp;
		this.config = config;

		setFloatable(false);

		String as[] = { TEXT_DRAW_DUAL_GRAPH };

		buttons = new JToggleButton[as.length];
		for (int i = 0; i < as.length; i++) {
			buttons[i] = new JToggleButton(as[i]);
			buttons[i].addItemListener(this);
			add(buttons[i]);
			buttons[i].setMaximumSize(new Dimension(
					buttons[i].getMaximumSize().width, 32767));
		}

		buttons[0].setSelected(config.isDrawDualGraph());

		ZoomInput zoom = new ZoomInput(spp);
		add(zoom);

		control = new JButton[names.length];
		for (int i = 0; i < names.length; i++) {
			Icon icon = ImageLoader.load(images[i]);
			control[i] = new JButton(icon);
			control[i].setToolTipText(names[i]);
			final String name = names[i];
			controlMap.put(name, control[i]);
			add(control[i]);
			control[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					control(name);
				}
			});
		}

		algorithm.addAlgorithmWatcher(this);
		setButtonStatesDependingOnAlgorithmStatus();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		JToggleButton button = (JToggleButton) e.getItem();
		String s = button.getText();
		boolean flag = button.isSelected();
		if (s.equals(TEXT_DRAW_DUAL_GRAPH)) {
			config.setDrawDualGraph(flag);
		}
		spp.repaint();
	}

	protected void control(String name)
	{
		if (name.equals(TEXT_PREVIOUS_DIAG)) {
			tryPreviousDiagonal(0);
		} else if (name.equals(TEXT_NEXT_DIAG)) {
			tryNextDiagonal();
		} else if (name.equals(TEXT_PREVIOUS)) {
			int subStatus = algorithm.getSubStatus();
			if (subStatus > 0) {
				algorithm.setSubStatus(subStatus - 1);
				spp.repaint();
			} else if (subStatus == 0) {
				tryPreviousDiagonal(-1);
			}
		} else if (name.equals(TEXT_NEXT)) {
			int subStatus = algorithm.getSubStatus();
			int nostuf = algorithm.numberOfStepsToNextDiagonal();
			if (subStatus < nostuf) {
				algorithm.setSubStatus(subStatus + 1);
				spp.repaint();
			} else if (subStatus == nostuf) {
				tryNextDiagonal();
			}
		}
	}

	private void tryPreviousDiagonal(int subStatus)
	{
		int status = algorithm.getStatus();
		if (status > 0) {
			algorithm.setStatus(status - 1, subStatus);
			spp.repaint();
		}
	}

	private void tryNextDiagonal()
	{
		int status = algorithm.getStatus();
		if (status < algorithm.getNumberOfSteps()) {
			algorithm.setStatus(status + 1, 0);
			spp.repaint();
		}
	}

	@Override
	public void updateAlgorithmStatus()
	{
		setButtonStatesDependingOnAlgorithmStatus();
	}

	private void setButtonStatesDependingOnAlgorithmStatus()
	{
		boolean nextDiagonal = algorithm.getStatus() != algorithm
				.getNumberOfSteps();
		boolean nextStep = nextDiagonal
				|| algorithm.getSubStatus() < algorithm
						.numberOfStepsToNextDiagonal();
		boolean prevDiagonal = algorithm.getStatus() != 0;
		boolean prevStep = prevDiagonal || algorithm.getSubStatus() != 0;
		controlMap.get(TEXT_PREVIOUS_DIAG).setEnabled(prevDiagonal);
		controlMap.get(TEXT_NEXT_DIAG).setEnabled(nextDiagonal);
		controlMap.get(TEXT_PREVIOUS).setEnabled(prevStep);
		controlMap.get(TEXT_NEXT).setEnabled(nextStep);
	}
}
