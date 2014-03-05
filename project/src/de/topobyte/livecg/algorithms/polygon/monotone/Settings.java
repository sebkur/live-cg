/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.algorithms.polygon.monotone;

import javax.swing.JToolBar;

import de.topobyte.livecg.util.ZoomInput;

public class Settings extends JToolBar
{

	private static final long serialVersionUID = 1L;

	public Settings()
	{

	}

	public Settings(MonotoneTriangulationAlgorithm algorithm,
			MonotoneTriangulationPanel mtp, MonotoneTriangulationConfig config)
	{
		ZoomInput zoomInput = new ZoomInput(mtp);
		add(zoomInput);
	}

}
