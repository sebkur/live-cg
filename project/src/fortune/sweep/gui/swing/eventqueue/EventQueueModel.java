package fortune.sweep.gui.swing.eventqueue;

import javax.swing.AbstractListModel;

import fortune.sweep.Algorithm;
import fortune.sweep.events.CirclePoint;
import fortune.sweep.events.EventPoint;
import fortune.sweep.events.EventQueue;
import fortune.sweep.events.EventQueueListener;
import fortune.sweep.events.SitePoint;

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
