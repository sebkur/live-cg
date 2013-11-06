/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.livecg.geometryeditor.geometryeditor.presets;

import java.awt.event.ActionEvent;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.Content;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.util.resources.ResourceFile;

public class PresetMenuAction extends BasicAction
{

	private static final long serialVersionUID = -5166457918173320012L;

	final static Logger logger = LoggerFactory
			.getLogger(PresetMenuAction.class);

	private GeometryEditPane editPane;
	private ResourceFile file;

	public PresetMenuAction(GeometryEditPane editPane, String fileName,
			ResourceFile file)
	{
		super(name(fileName), "Load these geometries into the scene",
				"res/images/24x24/way.png");
		this.editPane = editPane;
		this.file = file;
	}

	private static String name(String fileName)
	{
		String displayName = fileName;
		if (displayName.endsWith(".geom")) {
			displayName = fileName.substring(0, fileName.length() - 5);
		}
		return displayName;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		ContentReader reader = new ContentReader();
		try {
			InputStream input = file.open();
			Content content = reader.read(input);
			editPane.setContent(content);
			content.fireContentChanged();
		} catch (Exception e) {
			logger.debug("unable to open file.");
			logger.debug("Exception type: " + e.getClass().getSimpleName());
			logger.debug("Exception message: " + e.getMessage());
		}
	}

}
