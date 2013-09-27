package fortune.sweep.gui.swing.action;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import fortune.sweep.gui.swing.ImageLoader;

public abstract class BaseAction extends AbstractAction
{

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private Icon icon;

	public BaseAction(String name, String description, Icon icon)
	{
		this.name = name;
		this.description = description;
		this.icon = icon;
	}

	@Override
	public Object getValue(String key)
	{
		if (key.equals(Action.SMALL_ICON)) {
			return icon;
		} else if (key.equals(Action.NAME)) {
			return name;
		} else if (key.equals(Action.SHORT_DESCRIPTION)) {
			return description;
		}
		return null;
	}

	protected void setIconFromResource(String filename)
	{
		icon = ImageLoader.load(filename);
	}
}
