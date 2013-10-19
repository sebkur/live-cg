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
package de.topobyte.livecg.util.circular;

public class IntRingInterval
{

	private int size;
	private int a;
	private int b;
	private boolean wrap;

	public IntRingInterval(int size, int a, int b)
	{
		this.size = size;
		this.a = a;
		this.b = b;
		wrap = a > b;
	}

	public int getSize()
	{
		return size;
	}

	public int getA()
	{
		return a;
	}

	public int getB()
	{
		return b;
	}

	public boolean contains(int n, boolean open)
	{
		if (!wrap) {
			if (open) {
				return n > a && n < b;
			} else {
				return n >= a && n <= b;
			}
		} else {
			if (open) {
				return n > a || n < b;
			} else {
				return n >= a || n <= b;
			}
		}
	}

}
