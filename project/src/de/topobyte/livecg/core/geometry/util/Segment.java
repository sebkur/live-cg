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
package de.topobyte.livecg.core.geometry.util;

import de.topobyte.livecg.core.geometry.geom.Node;

public class Segment
{
	private Node node1;
	private Node node2;

	public Segment(Node node1, Node node2)
	{
		this.node1 = node1;
		this.node2 = node2;
	}

	public Node getNode1()
	{
		return node1;
	}

	public Node getNode2()
	{
		return node2;
	}
}
