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

package de.topobyte.frechet.lina;


public class Vector extends Matrix
{
	private final VectorType type;

	public Vector(int size, VectorType type)
	{
		super(type == VectorType.Row ? 1 : size,
				type == VectorType.Column ? 1 : size);
		this.type = type;
	}

	public void setValue(int p, double v)
	{
		if (type == VectorType.Column) {
			setValue(0, p, v);
		} else {
			setValue(p, 0, v);
		}
	}

	public double getValue(int p)
	{
		if (type == VectorType.Column) {
			return getValue(0, p);
		} else {
			return getValue(p, 0);
		}
	}

	public int getSize()
	{
		if (type == VectorType.Column) {
			return getHeight();
		} else {
			return getWidth();
		}
	}

	public String toString()
	{
		StringBuilder strb = new StringBuilder();
		for (int i = 0; i < getSize(); i++) {
			strb.append(getValue(i));
			if (i < getSize() - 1) {
				strb.append(",");
			}
		}
		return strb.toString();
	}

	public String toString(int k)
	{
		StringBuilder strb = new StringBuilder();
		for (int i = 0; i < getSize(); i++) {
			String format = String.format("%%.%df", k);
			strb.append(String.format(format, getValue(i)));
			if (i < getSize() - 1) {
				strb.append(",");
			}
		}
		return strb.toString();
	}

	public double distance(Vector prev)
	{
		double sum = 0;
		for (int i = 0; i < getSize(); i++) {
			sum += Math.pow(prev.getValue(i) - getValue(i), 2);
		}
		return Math.sqrt(sum);
	}
	
	public double norm()
	{
		return Math.sqrt(this.transponate().multiplyFromRight(this).toScalar());
	}
	
	public Vector normalized()
	{
		return this.multiply(1.0 / this.norm()).toVector();
	}
}
