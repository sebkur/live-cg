package fortune.sweep.gui.swing.eventqueue;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fortune.sweep.Algorithm;

public class EventQueueDialog extends JDialog
{

	private static final long serialVersionUID = 1L;

	public EventQueueDialog(Window parent, Algorithm algorithm)
	{
		super(parent, "Event Queue");

		JPanel panel = new JPanel(new BorderLayout());
		setContentPane(panel);

		setSize(250, 500);

		final EventQueueModel eventQueueModel = new EventQueueModel(algorithm);

		JScrollPane jsp = new JScrollPane();
		final JList list = new JList(eventQueueModel);
		jsp.setViewportView(list);

		panel.add(jsp, BorderLayout.CENTER);
	}
}
