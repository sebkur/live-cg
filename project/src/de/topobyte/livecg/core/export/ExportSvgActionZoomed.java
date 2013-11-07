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
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.scrolling.HasScene;
import de.topobyte.livecg.core.scrolling.Viewport;
import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.filefilters.FileFilterSvg;

public class ExportSvgActionZoomed<T extends Viewport & HasScene> extends
		BasicAction
{
	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory
			.getLogger(ExportSvgActionZoomed.class);

	private Component component;
	private AlgorithmPainter algorithmPainter;

	private T dimensionProvider;

	public ExportSvgActionZoomed(Component component,
			AlgorithmPainter algorithmPainter, T dimensionProvider)
	{
		super("Export SVG", "Export the current view to a SVG image", null);
		this.component = component;
		this.algorithmPainter = algorithmPainter;
		this.dimensionProvider = dimensionProvider;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		LastDirectoryService lastDirectoryService = LastDirectoryService
				.getInstance();
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(lastDirectoryService.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterSvg());
		int returnVal = fc.showSaveDialog(component);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		lastDirectoryService.setLastActiveDirectory(file.getParentFile());

		Rectangle scene = dimensionProvider.getScene();
		double width = scene.getWidth() * dimensionProvider.getZoom();
		double height = scene.getHeight() * dimensionProvider.getZoom();

		int iwidth = (int) Math.ceil(width);
		int iheight = (int) Math.ceil(height);
		algorithmPainter.setZoom(dimensionProvider.getZoom());

		try {
			SvgExporter.exportSVG(file, algorithmPainter, iwidth, iheight);
		} catch (IOException ex) {
			logger.error("unable to export image (IOException): "
					+ ex.getMessage());
		} catch (TransformerException ex) {
			logger.error("unable to export image (TransfomerException): "
					+ ex.getMessage());
		}
	}

}
