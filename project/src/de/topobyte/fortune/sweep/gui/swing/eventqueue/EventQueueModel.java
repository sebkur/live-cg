package de.topobyte.fortune.sweep.gui.swing.eventqueue;

import javax.swing.AbstractListModel;

import de.topobyte.fortune.sweep.Algorithm;
import de.topobyte.fortune.sweep.events.CirclePoint;
import de.topobyte.fortune.sweep.events.EventPoint;
import de.topobyte.fortune.sweep.events.EventQueue;
import de.topobyte.fortune.sweep.events.EventQueueListener;
import de.topobyte.fortune.sweep.events.SitePoint;

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
