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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.LiveCG;
import de.topobyte.livecg.ui.geometryeditor.preferences.PreferencesDialog;
import de.topobyte.swing.util.action.SimpleAction;

public class PreferencesAction extends SimpleAction
{

	private static final long serialVersionUID = 6677047849839688978L;

	static final Logger logger = LoggerFactory
			.getLogger(PreferencesAction.class);

	private LiveCG liveCG;

	public PreferencesAction(LiveCG liveCG)
	{
		super("Preferences", "Edit application preferences",
				"res/images/24x24/preferences.png");

		this.liveCG = liveCG;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		PreferencesDialog dialog = new PreferencesDialog(liveCG.getFrame(),
				liveCG);
		dialog.pack();
		dialog.setLocationRelativeTo(liveCG.getFrame());
		dialog.setVisible(true);
	}
}
