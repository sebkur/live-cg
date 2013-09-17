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
package de.topobyte.livecg.geometry.ui.geometryeditor.object;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class NothingPanel extends JPanel
{

	private static final long serialVersionUID = 4202371334962474019L;

	public NothingPanel()
	{
		setLayout(new BorderLayout());
		JLabel label = new JLabel("no active object");
		add(label, BorderLayout.CENTER);
	}

}
