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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.FortunesSweep;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventQueueModification.Type;

public class HistoryEventQueue extends EventQueue
{
	static final Logger logger = LoggerFactory
			.getLogger(HistoryEventQueue.class);

	private Map<EventPoint, List<EventQueueModification>> modifications = new HashMap<>();

	private FortunesSweep algorithm;

	public HistoryEventQueue(FortunesSweep algorithm)
	{
		this.algorithm = algorithm;
	}

	public synchronized void insertEvent(EventPoint reason,
			CirclePoint circlePoint)
	{
		logger.debug("insertEvent() " + reason.getClass().getSimpleName());
		List<EventQueueModification> changes;
		changes = modifications.get(reason);
		if (changes == null) {
			changes = new ArrayList<>();
			modifications.put(reason, changes);
		}

		if (circlePoint instanceof CirclePoint) {
			EventQueueModification modification = new EventQueueModification(
					algorithm.getSweepX(), Type.ADD, circlePoint);
			// Circle events will just be appended
			changes.add(modification);
			insert(circlePoint);
		}
	}

	public synchronized boolean remove(EventPoint reason,
			CirclePoint circlePoint)
	{
		logger.debug("remove() " + reason.getClass().getSimpleName());
		boolean remove = super.remove(circlePoint);
		if (remove) {
			List<EventQueueModification> changes = modifications.get(reason);
			if (changes == null) {
				changes = new ArrayList<>();
				modifications.put(reason, changes);
			}
			EventQueueModification modification = new EventQueueModification(
					algorithm.getSweepX(), Type.REMOVE, circlePoint);
			changes.add(modification);
		}
		return remove;
	}

	@Override
	public void clear()
	{
		super.clear();
		modifications.clear();
	}

	public List<EventQueueModification> getModifications(EventPoint eventPoint)
	{
		return modifications.get(eventPoint);
	}

	public synchronized void revertModification(
			EventQueueModification modification)
	{
		logger.debug("revertModification() " + modification.getType());
		// Reverse EventQueue modification
		if (modification.getType() == Type.ADD) {
			// Remove if the event was added
			super.remove(modification.getEventPoint());
		} else if (modification.getType() == Type.REMOVE) {
			// Insert if the event was removed
			insert(modification.getEventPoint());
			// Revert pointers of arcs to their circle events.
			CirclePoint circlePoint = modification.getEventPoint();
			circlePoint.getArc().setCirclePoint(circlePoint);
		}
	}
}
