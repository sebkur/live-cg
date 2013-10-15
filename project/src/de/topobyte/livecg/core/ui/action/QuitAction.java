package de.topobyte.livecg.core.ui.action;

import java.awt.event.ActionEvent;

public class QuitAction extends BasicAction
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
