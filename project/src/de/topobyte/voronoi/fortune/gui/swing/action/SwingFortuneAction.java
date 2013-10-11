package de.topobyte.voronoi.fortune.gui.swing.action;

import javax.swing.Icon;

import de.topobyte.voronoi.fortune.gui.swing.SwingFortune;

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
