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

package de.topobyte.livecg.geometryeditor.geometryeditor.scale;

import java.awt.Dimension;

import de.topobyte.livecg.core.scrolling.ViewportWithSignals;

public class ScaleY extends Scale
{

	private static final long serialVersionUID = -7665177848187638297L;

	public ScaleY(ViewportWithSignals viewport)
	{
		super(viewport);
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(50, 0);
	}

	@Override
	public boolean isHorizontal()
	{
		return false;
	}
}
