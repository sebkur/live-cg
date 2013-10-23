package de.topobyte.livecg.algorithms.voronoi.fortune.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.voronoi.fortune.Algorithm;
import de.topobyte.livecg.algorithms.voronoi.fortune.events.EventQueueModification.Type;

public class HistoryEventQueue extends EventQueue
{
	static final Logger logger = LoggerFactory
			.getLogger(HistoryEventQueue.class);

	private Map<EventPoint, List<EventQueueModification>> modifications = new HashMap<EventPoint, List<EventQueueModification>>();

	private Algorithm algorithm;

	public HistoryEventQueue(Algorithm algorithm)
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
			changes = new ArrayList<EventQueueModification>();
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
				changes = new ArrayList<EventQueueModification>();
				modifications.put(reason, changes);
			}
			EventQueueModification modification = new EventQueueModification(
					algorithm.getSweepX(), Type.REMOVE, circlePoint);
			changes.add(modification);
		}
		return remove;
	}

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
