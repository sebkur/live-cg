/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.topobyte.livecg.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;

public class SwingUtil
{

	public static void useAntialiasing(Graphics2D g, boolean b)
	{
		if (b) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	/**
	 * Get the containing frame of the given component.
	 *$
	 * @param component
	 *            the component to find the frame for.
	 * @return the containing JFrame
	 */
	public static JFrame getContainingFrame(Component component)
	{
		Container parent = component.getParent();
		while (!(parent == null || parent instanceof JFrame)) {
			parent = parent.getParent();
		}
		return (JFrame) parent;
	}

}
