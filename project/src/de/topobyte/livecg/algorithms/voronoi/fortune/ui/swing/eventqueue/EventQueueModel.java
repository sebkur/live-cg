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
package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.eventqueue;

import javax.swing.AbstractListModel;

import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.CirclePoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventPoint;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventQueue;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventQueueListener;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.SitePoint;

public class EventQueueModel extends AbstractListModel implements
		EventQueueListener
{

	private static final long serialVersionUID = 1L;

	public class Element
	{

		private EventPoint event;

		public Element(EventPoint eventPoint)
		{
			this.event = eventPoint;
		}

		@Override
		public String toString()
		{
			if (event instanceof SitePoint) {
				return String.format("Site: %.1f, %.1f", event.getX(),
						event.getY());
			} else if (event instanceof CirclePoint) {
				return String.format("Circle: %.1f, %.1f", event.getX(),
						event.getY());
			}
			return null;
		}

	}

	private Algorithm algorithm;
	private EventQueue copy = null;

	public EventQueueModel(Algorithm algorithm)
	{
		this.algorithm = algorithm;
		algorithm.getEventQueue().addEventQueueListener(this);
	}

	@Override
	public int getSize()
	{
		if (copy == null) {
			return 0;
		}
		return copy.size();
	}

	@Override
	public Object getElementAt(int index)
	{
		EventPoint eventPoint = copy.get(index);
		return new Element(eventPoint);
	}

	@Override
	public void update()
	{
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				copy = algorithm.getEventQueue().getCopy();
				fireContentsChanged(this, 0, copy.size());
			}
		});
	}

}
