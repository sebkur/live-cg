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
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.livecg.geometry.io.ContentReader;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.util.SwingUtil;

public class OpenAction extends BasicAction
{

	private static final long serialVersionUID = 6902851975437908329L;

	static final Logger logger = LoggerFactory.getLogger(OpenAction.class);

	private final GeometryEditPane editPane;
	private final JComponent component;

	public OpenAction(JComponent component, GeometryEditPane editPane)
	{
		super("Open", "Open a document from a file",
				"org/freedesktop/tango/22x22/actions/document-open.png");
		this.component = component;
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		JFrame frame = SwingUtil.getContainingFrame(component);
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showOpenDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to open document from file: " + file);

			ContentReader reader = new ContentReader();
			try {
				Content content = reader.read(file);
				editPane.setContent(content);

				content.fireContentChanged();
			} catch (IOException e) {
				logger.debug("unable to open file.");
				logger.debug("Exception type: " + e.getClass().getSimpleName());
				logger.debug("Exception message: " + e.getMessage());
			} catch (ParserConfigurationException e) {
				logger.debug("unable to open file.");
				logger.debug("Exception type: " + e.getClass().getSimpleName());
				logger.debug("Exception message: " + e.getMessage());
			} catch (SAXException e) {
				logger.debug("unable to open file.");
				logger.debug("Exception type: " + e.getClass().getSimpleName());
				logger.debug("Exception message: " + e.getMessage());
			}
		}
	}

}
