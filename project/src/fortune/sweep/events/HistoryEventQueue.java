package fortune.sweep.events;

import java.util.ArrayList;
import java.util.List;

import fortune.sweep.Algorithm;
import fortune.sweep.events.EventQueueModification.Type;

public class HistoryEventQueue extends EventQueue
{

	private List<EventQueueModification> modifications = new ArrayList<EventQueueModification>();

	private Algorithm algorithm;

	public HistoryEventQueue(Algorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	public synchronized boolean insertEvent(EventPoint eventPoint)
	{
		if (eventPoint instanceof CirclePoint) {
			EventQueueModification modification = new EventQueueModification(
					algorithm.getSweepX(), Type.ADD, eventPoint);
			// Circle events will just be appended
			modifications.add(modification);
			insert(eventPoint);
			return true;
		} else if (eventPoint instanceof SitePoint) {
			EventQueueModification modification = new EventQueueModification(
					0.0, Type.ADD, eventPoint);
			// Site events need to be inserted at the correct position
			int pos = findLastSiteInsertion();
			modifications.add(pos + 1, modification);
			insert(eventPoint);
			return true;
		}
		return false;
	}

	/**
	 * Find the position of the last insertion of a SitePoint. Since all
	 * SitePoint insertion are stored as a sequence at the beginning of the
	 * modifications list, the returned value is the index of the last SitePoint
	 * insertion of that sequence.
	 * 
	 * @return the index of the last SitePoint insertion in the sequence of
	 *         SitePoint insertions or -1 if there has not been any SitePoint
	 *         insertion yet.
	 */
	private int findLastSiteInsertion()
	{
		int pos = -1;
		for (int i = 0; i < modifications.size(); i++) {
			EventQueueModification mod = modifications.get(i);
			if (mod.getType() == Type.ADD
					&& mod.getEventPoint() instanceof SitePoint) {
				pos = i;
			} else {
				break;
			}
		}
		return pos;
	}

	@Override
	public synchronized boolean remove(EventPoint eventPoint)
	{
		boolean remove = super.remove(eventPoint);
		if (remove) {
			modifications.add(new EventQueueModification(algorithm.getSweepX(),
					Type.REMOVE, eventPoint));
		}
		return remove;
	}

	@Override
	public synchronized EventPoint pop()
	{
		EventPoint eventPoint = top();
		modifications.add(new EventQueueModification(eventPoint.getX(),
				Type.REMOVE, eventPoint));
		return super.pop();
	}

	public synchronized boolean hasModification()
	{
		return modifications.size() > 0;
	}

	public synchronized EventQueueModification getLatestModification()
	{
		if (modifications.size() == 0) {
			return null;
		}
		return modifications.get(modifications.size() - 1);
	}

	public synchronized EventQueueModification revertModification()
	{
		if (modifications.size() == 0) {
			return null;
		}
		EventQueueModification modification = modifications
				.remove(modifications.size() - 1);
		// Reverse EventQueue modification
		if (modification.getType() == Type.ADD) {
			// Remove if the event was added
			if (modification.getEventPoint() instanceof CirclePoint) {
				super.remove(modification.getEventPoint());
			}
		} else if (modification.getType() == Type.REMOVE) {
			// Insert if the event was removed
			insert(modification.getEventPoint());
			// Revert pointers of arcs to their circle events.
			if (modification.getEventPoint() instanceof CirclePoint) {
				CirclePoint circlePoint = (CirclePoint) modification
						.getEventPoint();
				circlePoint.getArc().setCirclePoint(circlePoint);
			}
		}

		return modification;
	}
	
	public void clear()
	{
		super.clear();
		modifications.clear();
	}

}
