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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.scrolling.HasScene;
import de.topobyte.livecg.core.scrolling.Viewport;

public class ExportUtil
{

	public static void addExportSvgItem(JMenu menu, Component component,
			AlgorithmPainter painter, SizeProvider sizeProvider)
	{
		JMenuItem exportSvg = new JMenuItem(new ExportSvgActionOriginalSize(
				component, painter, sizeProvider));
		menu.add(exportSvg);
	}

	public static void addExportPngItem(JMenu menu, Component component,
			AlgorithmPainter painter, SizeProvider sizeProvider)
	{
		JMenuItem exportBitmap = new JMenuItem(
				new ExportBitmapActionOriginalSize(component, painter,
						sizeProvider));
		menu.add(exportBitmap);
	}

	public static <T extends Viewport & HasScene> void addExportSvgZoomedItem(
			JMenu menu, Component component, AlgorithmPainter painter,
			T dimensionProvider)
	{
		JMenuItem exportSvg = new JMenuItem(new ExportSvgActionZoomed<T>(
				component, painter, dimensionProvider));
		menu.add(exportSvg);
	}

	public static <T extends Viewport & HasScene> void addExportPngZoomedItem(
			JMenu menu, Component component, AlgorithmPainter painter,
			T dimensionProvider)
	{
		JMenuItem exportBitmap = new JMenuItem(new ExportBitmapActionZoomed<T>(
				component, painter, dimensionProvider));
		menu.add(exportBitmap);
	}

}
