package de.topobyte.livecg.algorithms.voronoi.fortune.gui.swing.action;

import de.topobyte.livecg.algorithms.voronoi.fortune.gui.swing.SwingFortune;
import de.topobyte.livecg.core.ui.action.BasicAction;

public abstract class SwingFortuneAction extends BasicAction
{
	private static final long serialVersionUID = 1L;

	protected SwingFortune swingFortune;

	public SwingFortuneAction(String name, String description, String iconPath,
			SwingFortune swingFortune)
	{
		super(name, description, iconPath);
		this.swingFortune = swingFortune;
	}
}
