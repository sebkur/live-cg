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
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 *$
 */
public class BufferedImageIcon implements Icon
{

	private BufferedImage bi;

	/**
	 * Create a new icon from this buffered image.
	 *$
	 * @param bi
	 *            the image to wrap.
	 */
	public BufferedImageIcon(BufferedImage bi)
	{
		this.bi = bi;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		g.drawImage(bi, x, y, null);
	}

	@Override
	public int getIconWidth()
	{
		return bi.getWidth();
	}

	@Override
	public int getIconHeight()
	{
		return bi.getHeight();
	}

}
