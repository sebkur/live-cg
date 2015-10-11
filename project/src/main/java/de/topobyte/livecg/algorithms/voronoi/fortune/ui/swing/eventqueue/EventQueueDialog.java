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
package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.eventqueue;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;

public class EventQueueDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public EventQueueDialog(Window parent, FortunesSweep algorithm)
	{
		super(parent, "Event Queue");

		JPanel panel = new JPanel(new BorderLayout());
		setContentPane(panel);

		setSize(250, 500);

		final EventQueueModel eventQueueModel = new EventQueueModel(algorithm);

		JScrollPane jsp = new JScrollPane();
		final JList list = new JList(eventQueueModel);
		jsp.setViewportView(list);

		panel.add(jsp, BorderLayout.CENTER);
	}
}
