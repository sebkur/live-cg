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

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.FilePropertiesDialog;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.util.SwingUtil;

public class FilePropertiesAction extends BasicAction
{

	private static final long serialVersionUID = 8520412983084660304L;

	static final Logger logger = LoggerFactory
			.getLogger(FilePropertiesAction.class);

	private final GeometryEditPane editPane;
	private final JComponent component;

	public FilePropertiesAction(JComponent component, GeometryEditPane editPane)
	{
		super("Properties", "Edit file properties",
				"res/images/24x24/preferences.png");
		this.component = component;
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		JFrame frame = SwingUtil.getContainingFrame(component);
		Content content = editPane.getContent();
		FilePropertiesDialog dialog = new FilePropertiesDialog(frame, content);

		dialog.setModal(true);
		dialog.setLocationRelativeTo(frame);
		dialog.pack();
		dialog.setVisible(true);
	}

}
