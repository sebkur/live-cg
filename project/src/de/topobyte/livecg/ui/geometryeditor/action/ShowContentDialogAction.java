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
package de.topobyte.livecg.ui.geometryeditor.action;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.ui.geometryeditor.LiveCG;

public class ShowContentDialogAction extends BasicAction
{

	private static final long serialVersionUID = 1200273618665720148L;

	private LiveCG liveCG;

	public ShowContentDialogAction(LiveCG liveCG)
	{
		super("Content dialog", "Show the content dialog",
				"res/images/24x24/empty.png");
		this.liveCG = liveCG;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		liveCG.showContentDialog();
	}

}