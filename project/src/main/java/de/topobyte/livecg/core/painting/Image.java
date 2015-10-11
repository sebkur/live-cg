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

public class Image
{

	private int width;
	private int height;
	private int[] values;

	public Image(int width, int height)
	{
		this.width = width;
		this.height = height;
		values = new int[width * height];
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	/**
	 * Get the RGB value at (i,j)
	 * 
	 * @param i
	 *            the x coordinate
	 * @param j
	 *            the y coordinate
	 */
	public int getRGB(int i, int j)
	{
		return values[j * width + i];
	}

	/**
	 * Set the RGB value at (i,j)
	 * 
	 * @param i
	 *            the x coordinate
	 * @param j
	 *            the y coordinate
	 */
	public void setRGB(int i, int j, int rgb)
	{
		values[j * width + i] = rgb;
	}
}
