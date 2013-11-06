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
package de.topobyte.livecg.core.painting;

public class Color
{
	private int argb;

	public Color(int rgb)
	{
		this.argb = setFullAlpha(rgb);
	}

	public Color(int rgb, boolean hasAlpha)
	{
		if (hasAlpha) {
			this.argb = rgb;
		} else {
			this.argb = setFullAlpha(rgb);
		}
	}

	public Color(int r, int g, int b)
	{
		this(r, g, b, 255);
	}

	public Color(int r, int g, int b, int a)
	{
		argb = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8)
				| ((b & 0xFF) << 0);
	}

	private int setFullAlpha(int rgb)
	{
		return 0xff000000 | rgb;
	}

	public int getARGB()
	{
		return argb;
	}

	public int getRGB()
	{
		return argb & 0xFFFFFF;
	}

	public double getAlpha()
	{
		int a = (argb & 0xff000000) >>> 24;
		return a / 255.0;
	}
}
