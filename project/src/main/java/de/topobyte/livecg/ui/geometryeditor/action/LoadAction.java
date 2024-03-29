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
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.swing.util.Components;
import de.topobyte.swing.util.action.SimpleAction;

public class LoadAction extends SimpleAction
{

	private static final long serialVersionUID = -4452993048850158926L;

	static final Logger logger = LoggerFactory.getLogger(LoadAction.class);

	private final GeometryEditPane editPane;
	private final JComponent component;

	public LoadAction(JComponent component, GeometryEditPane editPane)
	{
		super("Load", "Load a line from a file into the document",
				"org/freedesktop/tango/22x22/actions/document-open.png");
		this.component = component;
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFrame frame = Components.getContainingFrame(component);
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showOpenDialog(frame);
		if (value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			logger.debug("attempting to open line from file: " + file);
			try {
				WKTReader wktReader = new WKTReader();
				Geometry geometry = wktReader.read(new FileReader(file));
				Chain chain = Chain.fromLineString(geometry);
				if (chain == null) {
					System.out.println(
							"loaded geometry is not a valid LineString");
				} else {
					editPane.getContent().addChain(chain);
					editPane.getContent().fireContentChanged();
				}
			} catch (IOException ex) {
				System.out.println("unable to load file: " + ex.getMessage());
			} catch (ParseException ex) {
				System.out.println("unable to load file: " + ex.getMessage());
			}
		}
	}

}
