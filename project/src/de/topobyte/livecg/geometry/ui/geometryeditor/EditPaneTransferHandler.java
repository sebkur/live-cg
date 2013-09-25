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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import de.topobyte.livecg.geometry.ui.geom.Chain;

public class EditPaneTransferHandler extends TransferHandler
{

	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory
			.getLogger(EditPaneTransferHandler.class);

	private final Content content;

	public EditPaneTransferHandler(Content content)
	{
		this.content = content;
	}

	private void handleFiles(List<File> handleFiles, TransferSupport ts)
	{
		for (File file : handleFiles) {
			WKTReader reader = new WKTReader();
			try {
				Geometry geometry = reader.read(new FileReader(file));
				Chain editable = Chain.fromLineString(geometry);
				content.addChain(editable);
			} catch (FileNotFoundException e) {
				logger.error("unable to load geometry: " + e.getMessage());
			} catch (ParseException e) {
				logger.error("unable to load geometry: " + e.getMessage());
			}
		}
		content.fireContentChanged();
	}

	public boolean canImport(TransferSupport support)
	{
		return true;
	}

	public boolean importData(TransferSupport ts)
	{
		System.out.println("import");
		Transferable tr = ts.getTransferable();
		boolean handleable = false;
		List<File> handleFiles = new ArrayList<File>();

		if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			try {
				List<?> flist = (List<?>) tr
						.getTransferData(DataFlavor.javaFileListFlavor);
				File[] files = flist.toArray(new File[0]);
				for (File f : files) {
					handleFiles.add(f);
				}
				handleable = true;
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].isRepresentationClassReader()) {
					try {
						Reader reader = flavors[i].getReaderForText(tr);
						BufferedReader br = new BufferedReader(reader);
						handleFiles = createFileArray(br);
					} catch (UnsupportedFlavorException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					handleable = true;
					break;
				}
			}

		}

		if (handleable) {
			handleFiles(handleFiles, ts);
		}

		return handleable;
	}

	private static String ZERO_CHAR_STRING = "" + (char) 0;

	static List<File> createFileArray(BufferedReader bReader)
	{
		List<File> list = new java.util.ArrayList<File>();
		try {
			java.lang.String line = null;
			while ((line = bReader.readLine()) != null) {
				try {
					// kde seems to append a 0 char to the end of the reader
					if (ZERO_CHAR_STRING.equals(line))
						continue;

					File file = new File(new java.net.URI(line));
					list.add(file);
				} catch (Exception ex) {
					logger.debug("Error with " + line + ": " + ex.getMessage());
				}
			}
		} catch (IOException ex) {
			logger.debug("FileDrop: IOException");
		}
		return list;
	}

}
