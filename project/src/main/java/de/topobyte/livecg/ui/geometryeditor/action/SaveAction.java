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
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.io.ContentWriter;
import de.topobyte.livecg.ui.action.BasicAction;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.util.Components;

public class SaveAction extends BasicAction
{

	private static final long serialVersionUID = 5846324015237476127L;

	static final Logger logger = LoggerFactory.getLogger(SaveAction.class);

	private final GeometryEditPane editPane;
	private final JComponent component;

	public SaveAction(JComponent component, GeometryEditPane editPane)
	{
		super("Save", "Save the current document to a file",
				"org/freedesktop/tango/22x22/actions/document-save.png");
		this.component = component;
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		JFrame frame = Components.getContainingFrame(component);
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showSaveDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to write document to file: " + file);
			try {
				write(editPane.getContent(), file);
			} catch (IOException e) {
				logger.debug("unable to write file.");
				logger.debug("Exception type: " + e.getClass().getSimpleName());
				logger.debug("Exception message: " + e.getMessage());
			}
		}
	}

	private void write(Content content, File file) throws IOException
	{
		ContentWriter writer = new ContentWriter();
		writer.write(content, file);
	}

}
