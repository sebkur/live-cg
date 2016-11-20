/* This file is part of LiveCG.
 *
 * Copyright (C) 2013  Sebastian Kuerten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.topobyte.livecg.ui.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.util.ImageLoader;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public abstract class SimpleAction extends
		de.topobyte.swing.util.action.SimpleAction
{

	private static final long serialVersionUID = 1727617884915345905L;

	final static Logger logger2 = LoggerFactory.getLogger(SimpleAction.class);

	public SimpleAction(String name, String description)
	{
		super(name, description);
	}

	/**
	 * Set this action's icon from the denoted filename.
	 * 
	 * @param filename
	 *            the icon to use.
	 */
	protected void setIconFromResource(String filename)
	{
		logger2.debug("loading icon: " + filename);

		setIcon(ImageLoader.load(filename));
	}

}
