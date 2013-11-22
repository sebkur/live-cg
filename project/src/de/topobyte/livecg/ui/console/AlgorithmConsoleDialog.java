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
package de.topobyte.livecg.ui.console;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.core.algorithm.Algorithm;

public class AlgorithmConsoleDialog extends JDialog
{

	private static final long serialVersionUID = -4308148067866265620L;

	public AlgorithmConsoleDialog(Algorithm algorithm)
	{
		/*
		 * Console
		 */
		AlgorithmOutputConsole console = new AlgorithmOutputConsole(algorithm);

		/*
		 * UI
		 */

		JPanel main = new JPanel(new BorderLayout());
		JToolBar tools = new JToolBar();
		tools.setFloatable(false);

		main.add(tools, BorderLayout.NORTH);
		main.add(console, BorderLayout.CENTER);

		setContentPane(main);
		setSize(500, 400);

		JToggleButton scrollLock = new JToggleButton("scroll lock");
		tools.add(scrollLock);
	}
}
