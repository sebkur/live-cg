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

package de.topobyte.livecg.geometryeditor.geometryeditor.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.livecg.core.geometry.io.SetOfGeometryReader;
import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometryeditor.geometryeditor.SetOfGeometries;
import de.topobyte.livecg.geometryeditor.geometryeditor.clipboard.GeometryTransfer;
import de.topobyte.livecg.geometryeditor.geometryeditor.clipboard.GeometryTransferable;

public class PasteAction extends BasicAction
{

	private static final long serialVersionUID = -1907505828869493624L;

	static final Logger logger = LoggerFactory.getLogger(PasteAction.class);

	private final GeometryEditPane editPane;

	public PasteAction(GeometryEditPane editPane)
	{
		super("Paste", "Paste from clipboard",
				"org/freedesktop/tango/22x22/actions/edit-paste.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferData = clipboard.getContents(null);

		boolean success = false;

		if (transferData
				.isDataFlavorSupported(GeometryTransferable.flavorGeometries)) {
			try {
				Object data = transferData
						.getTransferData(GeometryTransferable.flavorGeometries);
				SetOfGeometries geometries = (SetOfGeometries) data;
				GeometryTransfer.transfer(geometries, editPane.getContent());
				editPane.repaint();
				success = true;
			} catch (UnsupportedFlavorException e) {
				logger.debug("Unsupported Data Flavor");
			} catch (IOException e) {
				logger.error("IOException");
			}
		}

		if (success) {
			return;
		}

		if (transferData
				.isDataFlavorSupported(GeometryTransferable.flavorPlainText)) {
			try {
				Object data = transferData
						.getTransferData(GeometryTransferable.flavorPlainText);
				InputStream input = (InputStream) data;
				SetOfGeometryReader reader = new SetOfGeometryReader();
				SetOfGeometries geometries = reader.read(input);
				GeometryTransfer.transfer(geometries, editPane.getContent());
				editPane.repaint();
				success = true;
			} catch (UnsupportedFlavorException e) {
				logger.debug("Unsupported Data Flavor");
			} catch (IOException e) {
				logger.error("IOException");
			} catch (ParserConfigurationException e) {
				logger.error("ParserConfigurationException");
			} catch (SAXException e) {
				logger.error("SAXException");
			}
		}

		if (success) {
			return;
		}

		logger.error("unable to paste data");
	}
}
