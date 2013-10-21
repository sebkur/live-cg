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
package de.topobyte.livecg.algorithms.voronoi.fortune;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.topobyte.livecg.algorithms.voronoi.fortune.geometry.Point;
import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;

public class TestFortunesSweep
{

	public static void main(String[] args)
	{
		Logger.getLogger(Algorithm.class.getPackage().getName()).setLevel(Level.DEBUG);
		
		FortuneDialog dialog = new FortuneDialog();
		Algorithm algorithm = dialog.getAlgorithm();
		algorithm.addSite(new Point(200, 200));
		algorithm.addSite(new Point(250, 250));
		algorithm.addSite(new Point(300, 150));
		algorithm.addSite(new Point(350, 300));
		algorithm.restart();
	}
}
