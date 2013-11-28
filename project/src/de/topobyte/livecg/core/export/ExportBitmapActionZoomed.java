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
import java.io.IOException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.scrolling.HasScene;
import de.topobyte.livecg.core.scrolling.Viewport;
import de.topobyte.livecg.ui.filefilters.FileFilterBitmap;

public class ExportBitmapActionZoomed<T extends Viewport & HasScene> extends
		ExportActionZoomed<T>
{

	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory
			.getLogger(ExportBitmapActionZoomed.class);

	public ExportBitmapActionZoomed(Component component,
			AlgorithmPainter algorithmPainter, T dimensionProvider)
	{
		super("Export Bitmap", "Export the current view to a bitmap", null,
				component, algorithmPainter, dimensionProvider);
	}

	@Override
	protected void setupFileChooser(JFileChooser fc)
	{
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterBitmap());
	}

	@Override
	protected void export(File file, int width, int height)
	{
		try {
			GraphicsExporter.exportPNG(file, algorithmPainter, width, height);
		} catch (IOException ex) {
			logger.error("unable to export image: " + ex.getMessage());
		}
	}

}
