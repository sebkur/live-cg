/* This file is part of Frechet tools. 
 * 
 * Copyright (C) 2012  Sebastian Kuerten
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

package de.topobyte.frechet.ui.misc;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu extends JMenuBar{

	private static final long serialVersionUID = -7983876851509766368L;

	public Menu()
	{
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");
		add(file);
		add(help);
		
		JMenuItem exit = new JMenuItem(new ExitAction());
		file.add(exit);
		
		JMenuItem about = new JMenuItem(new AboutAction());
		JMenuItem license = new JMenuItem(new LicenseAction());
		help.add(about);
		help.add(license);
	}
}
