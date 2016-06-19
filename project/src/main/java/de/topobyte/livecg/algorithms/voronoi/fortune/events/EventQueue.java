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
package de.topobyte.livecg.algorithms.voronoi.fortune.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import de.topobyte.adt.trees.avltree.AvlTree;

public class EventQueue
{

	private AvlTree<EventPoint> points;

	public EventQueue()
	{
		points = new AvlTree<>(new Comparator<EventPoint>() {

			@Override
			public int compare(EventPoint e1, EventPoint e2)
			{
				if (e1.getX() != e2.getX()) {
					if (e1.getX() < e2.getX()) {
						return -1;
					} else if (e1.getX() > e2.getX()) {
						return 1;
					}
				}
				// e1.getX() == e2.getX()
				if (e1.getY() < e2.getY()) {
					return -1;
				} else if (e1.getY() > e2.getY()) {
					return 1;
				}
				// e1.getY() == e2.getY()
				boolean c1 = e1 instanceof CirclePoint;
				boolean c2 = e2 instanceof CirclePoint;
				if (c1 && !c2) {
					return -1;
				}
				if (!c1 && c2) {
					return 1;
				}
				// c1 == c2
				return 0;
			}
		});
	}

	public synchronized int size()
	{
		return points.size();
	}

	public synchronized void insert(EventPoint eventPoint)
	{
		points.add(eventPoint);
		fireEventQueueChanged();
	}

	public synchronized boolean remove(EventPoint eventPoint)
	{
		boolean removed = points.remove(eventPoint);
		if (removed) {
			fireEventQueueChanged();
		}
		return removed;
	}

	public synchronized EventPoint top()
	{
		return points.first();
	}

	public synchronized EventPoint pop()
	{
		EventPoint point = points.first();
		points.remove(point);
		fireEventQueueChanged();
		return point;
	}

	public Iterator<EventPoint> iterator()
	{
		return points.iterator();
	}

	public synchronized EventQueue getCopy()
	{
		EventQueue copy = new EventQueue();
		for (EventPoint point : points) {
			copy.insert(point);
		}
		return copy;
	}

	public synchronized EventPoint get(int index)
	{
		return points.get(index);
	}

	private List<EventQueueListener> listeners = new ArrayList<>();

	public void addEventQueueListener(EventQueueListener listener)
	{
		listeners.add(listener);
	}

	public void removeEventQueueListener(EventQueueListener listener)
	{
		listeners.remove(listener);
	}

	private void fireEventQueueChanged()
	{
		for (EventQueueListener listener : listeners) {
			listener.update();
		}
	}

	public void clear()
	{
		points.clear();
		fireEventQueueChanged();
	}

}
