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
package de.topobyte.livecg.core.geometry.dcel;

public class HalfEdge
{

	private Vertex origin;
	private HalfEdge prev;
	private HalfEdge next;
	private Face face;
	private HalfEdge twin;

	public HalfEdge(Vertex origin, HalfEdge prev, HalfEdge next, Face face,
			HalfEdge twin)
	{
		this.origin = origin;
		this.prev = prev;
		this.next = next;
		this.face = face;
		this.twin = twin;
	}

	public Vertex getOrigin()
	{
		return origin;
	}

	public void setOrigin(Vertex origin)
	{
		this.origin = origin;
	}

	public HalfEdge getPrev()
	{
		return prev;
	}

	public void setPrev(HalfEdge prev)
	{
		this.prev = prev;
	}

	public HalfEdge getNext()
	{
		return next;
	}

	public void setNext(HalfEdge next)
	{
		this.next = next;
	}

	public Face getFace()
	{
		return face;
	}

	public void setFace(Face face)
	{
		this.face = face;
	}

	public HalfEdge getTwin()
	{
		return twin;
	}

	public void setTwin(HalfEdge twin)
	{
		this.twin = twin;
	}

}
