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
package de.topobyte.livecg.algorithms.voronoi.fortune.events;

public class EventQueueModification
{

	public enum Type {
		ADD, REMOVE
	}

	private double x;
	private Type type;
	private CirclePoint eventPoint;

	public EventQueueModification(double x, Type type, CirclePoint eventPoint)
	{
		this.x = x;
		this.type = type;
		this.eventPoint = eventPoint;
	}

	public double getX()
	{
		return x;
	}

	public Type getType()
	{
		return type;
	}

	public CirclePoint getEventPoint()
	{
		return eventPoint;
	}

	@Override
	public String toString()
	{
		return String.format("sweep: %f, type: %s, %s, point: %f,%f", x,
				type.toString(), eventPoint.getClass(), eventPoint.getX(),
				eventPoint.getY());
	}
}
