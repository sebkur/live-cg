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

package de.topobyte.livecg.geometry.ui.geometryeditor.action.geometry;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

import de.topobyte.carbon.geometry.serialization.util.FileFormat;
import de.topobyte.carbon.geometry.serialization.util.GeometrySerializer;
import de.topobyte.carbon.geometry.serialization.util.GeometrySerializerFactory;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.BasicAction;
import de.topobyte.util.SwingUtil;

public class SaveAction extends BasicAction
{
	private static final long serialVersionUID = -4452993048850158926L;

	static final Logger logger = LoggerFactory.getLogger(SaveAction.class);

	private final GeometryEditPane editPane;
	private final JComponent component;

	public SaveAction(JComponent component, GeometryEditPane editPane)
	{
		super("Save", "Save the current line to a file",
				"org/freedesktop/tango/22x22/actions/document-save.png");
		this.component = component;
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		List<Chain> chains = editPane.getCurrentChains();
		if (chains.size() != 1) {
			return;
		}
		Chain line = chains.iterator().next();

		JFrame frame = SwingUtil.getContainingFrame(component);
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showSaveDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to write line to file: " + file);
			try {
				write(line, file);
			} catch (IOException e) {
				logger.debug("unable to write file.");
				logger.debug("Exception type: " + e.getClass().getSimpleName());
				logger.debug("Exception message: " + e.getMessage());
			}
		}
	}

	private void write(Chain line, File file) throws IOException
	{
		Geometry geometry = line.createGeometry();
		GeometrySerializer serializer = GeometrySerializerFactory
				.getInstance(FileFormat.WKT);
		serializer.serialize(geometry, file);
	}

}
