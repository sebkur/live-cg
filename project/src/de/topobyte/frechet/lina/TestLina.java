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

import java.util.Random;

public class TestLina
{
	public static void main(String[] args)
	{
		System.out.println("testing lina package");
		
		Random random = new Random();
		
		Matrix a = new Matrix(3, 4);
		a.initRandom(random, 3);
		a.setValue(2, 0, 5);
		
		Matrix b = new Matrix(4,3);
		b.initRandom(random, 3);
		
		System.out.println("a:");
		System.out.println(a);
		System.out.println("b:");
		System.out.println(b);
		
		Matrix c = a.multiplyFromRight(b);
		System.out.println("c:");
		System.out.println(c);
		
		Matrix add = new Matrix(3,3);
		add.initRandom(random, 8);
		
		System.out.println("add:");
		System.out.println(add);
		
		Matrix added = c.add(add);
		System.out.println("added:");
		System.out.println(added);
		
		Matrix multiplied = added.multiply(0.5);
		System.out.println("multiplied:");
		System.out.println(multiplied);
		
		System.out.println("isVector: " + multiplied.isVector());
		System.out.println("isScalar: " + multiplied.isScalar());
		
		Vector v1 = new Vector(3, VectorType.Column);
		v1.initRandom(random, 3);
		
		System.out.println("v1:");
		System.out.println(v1);
		
		Matrix v2 = multiplied.multiplyFromRight(v1);
		System.out.println("v2:");
		System.out.println(v2);
		
		System.out.println("isVector: " + v1.isVector());
		System.out.println("isVector: " + v2.isVector());
		
		Vector v3 = new Vector(3, VectorType.Row);
		v3.initRandom(random, 4);
		
		Matrix v4 = v3.multiplyFromRight(v2);
		System.out.println("v4:");
		System.out.println(v4);
		System.out.println("isScalar: " + v4.isScalar());
		System.out.println(v4.toScalar());
		
		Matrix m = new Matrix(4, 4);
		m.initRandom(random, 5);
		System.out.println("m:");
		System.out.println(m);
		
		Matrix inverse = m.invert();
		System.out.println("inverse:");
		System.out.println(inverse);
		
		Matrix backmult = m.multiplyFromRight(inverse);
		System.out.println("backmult:");
		System.out.println(backmult);
	}
}
