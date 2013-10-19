package de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.action;

import de.topobyte.livecg.algorithms.voronoi.fortune.ui.swing.FortuneDialog;
import de.topobyte.livecg.geometryeditor.action.BasicAction;

public abstract class FortuneAction extends BasicAction
{
	private static final long serialVersionUID = 1L;

	protected FortuneDialog fortune;

	public FortuneAction(String name, String description, String iconPath,
			FortuneDialog fortune)
	{
		super(name, description, iconPath);
		this.fortune = fortune;
	}
}
