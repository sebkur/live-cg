package de.topobyte.fortune.sweep.gui.swing.action;

import javax.swing.Icon;

import de.topobyte.fortune.sweep.gui.swing.SwingFortune;


public abstract class SwingFortuneAction extends BaseAction
{
	private static final long serialVersionUID = 1L;

	protected SwingFortune swingFortune;

	public SwingFortuneAction(String name, String description, Icon icon,
			SwingFortune swingFortune)
	{
		super(name, description, icon);
		this.swingFortune = swingFortune;
	}
}
