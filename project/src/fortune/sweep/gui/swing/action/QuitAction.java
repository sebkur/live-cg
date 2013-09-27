package fortune.sweep.gui.swing.action;

import java.awt.event.ActionEvent;

public class QuitAction extends BaseAction
{

	private static final long serialVersionUID = 1L;

	public QuitAction()
	{
		super("Quit", "Exit the application", null);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

}
