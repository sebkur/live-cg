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
package de.topobyte.livecg.core.geometry.geom;

import java.util.ArrayList;
import java.util.List;

public class Polygon
{

	private Chain shell;
	private List<Chain> holes;

	public Polygon(Chain shell, List<Chain> holes)
	{
		this.shell = shell;
		shell.addPolygon(this);
		if (holes == null) {
			this.holes = new ArrayList<>();
		} else {
			this.holes = holes;
		}
		for (Chain chain : this.holes) {
			chain.addPolygon(this);
		}
	}

	public Chain getShell()
	{
		return shell;
	}

	public List<Chain> getHoles()
	{
		return holes;
	}

	public boolean isEmpty()
	{
		return shell.getNumberOfNodes() == 0;
	}

}
