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
package de.topobyte.livecg.core.export;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.livecg.ui.filefilters.FileFilterIpe;

public class ExportIpeActionOriginalSize extends ExportActionOriginalSize
{
	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory
			.getLogger(ExportIpeActionOriginalSize.class);

	public ExportIpeActionOriginalSize(Component component,
			VisualizationPainter visualizationPainter, SizeProvider sizeProvider)
	{
		super("Export Ipe", "Export the current view to a Ipe file", null,
				component, visualizationPainter, sizeProvider);
	}

	@Override
	protected void setupFileChooser(JFileChooser fc)
	{
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterIpe());
	}

	@Override
	protected void export(File file)
	{
		try {
			IpeExporter.exportIpe(file, visualizationPainter,
					sizeProvider.getWidth(), sizeProvider.getHeight());
		} catch (Exception ex) {
			logger.error("unable to export image (Exception): "
					+ ex.getMessage());
		}
	}

}
