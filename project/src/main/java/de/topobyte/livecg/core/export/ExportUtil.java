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

import de.topobyte.livecg.core.painting.VisualizationPainter;
import de.topobyte.viewports.scrolling.HasScene;
import de.topobyte.viewports.scrolling.Viewport;

public class ExportUtil
{

	public static void addExportItems(JMenu menu, Component component,
			VisualizationPainter painter, SizeProvider sizeProvider)
	{
		addExportPngItem(menu, component, painter, sizeProvider);
		addExportSvgItem(menu, component, painter, sizeProvider);
		addExportTikzItem(menu, component, painter, sizeProvider);
		addExportIpeItem(menu, component, painter, sizeProvider);
	}

	public static <T extends Viewport & HasScene> void addExportZoomedItems(
			JMenu menu, Component component, VisualizationPainter painter,
			T dimensionProvider)
	{
		addExportPngZoomedItem(menu, component, painter, dimensionProvider);
		addExportSvgZoomedItem(menu, component, painter, dimensionProvider);
		addExportTikzZoomedItem(menu, component, painter, dimensionProvider);
		addExportIpeZoomedItem(menu, component, painter, dimensionProvider);
	}

	public static void addExportSvgItem(JMenu menu, Component component,
			VisualizationPainter painter, SizeProvider sizeProvider)
	{
		JMenuItem exportSvg = new JMenuItem(new ExportSvgActionOriginalSize(
				component, painter, sizeProvider));
		menu.add(exportSvg);
	}

	public static void addExportPngItem(JMenu menu, Component component,
			VisualizationPainter painter, SizeProvider sizeProvider)
	{
		JMenuItem exportBitmap = new JMenuItem(
				new ExportBitmapActionOriginalSize(component, painter,
						sizeProvider));
		menu.add(exportBitmap);
	}

	public static void addExportTikzItem(JMenu menu, Component component,
			VisualizationPainter painter, SizeProvider sizeProvider)
	{
		JMenuItem exportTikz = new JMenuItem(new ExportTikzActionOriginalSize(
				component, painter, sizeProvider));
		menu.add(exportTikz);
	}

	public static void addExportIpeItem(JMenu menu, Component component,
			VisualizationPainter painter, SizeProvider sizeProvider)
	{
		JMenuItem exportIpe = new JMenuItem(new ExportIpeActionOriginalSize(
				component, painter, sizeProvider));
		menu.add(exportIpe);
	}

	public static <T extends Viewport & HasScene> void addExportSvgZoomedItem(
			JMenu menu, Component component, VisualizationPainter painter,
			T dimensionProvider)
	{
		JMenuItem exportSvg = new JMenuItem(new ExportSvgActionZoomed<>(
				component, painter, dimensionProvider));
		menu.add(exportSvg);
	}

	public static <T extends Viewport & HasScene> void addExportPngZoomedItem(
			JMenu menu, Component component, VisualizationPainter painter,
			T dimensionProvider)
	{
		JMenuItem exportBitmap = new JMenuItem(new ExportBitmapActionZoomed<>(
				component, painter, dimensionProvider));
		menu.add(exportBitmap);
	}

	public static <T extends Viewport & HasScene> void addExportTikzZoomedItem(
			JMenu menu, Component component, VisualizationPainter painter,
			T dimensionProvider)
	{
		JMenuItem exportTikz = new JMenuItem(new ExportTikzActionZoomed<>(
				component, painter, dimensionProvider));
		menu.add(exportTikz);
	}

	public static <T extends Viewport & HasScene> void addExportIpeZoomedItem(
			JMenu menu, Component component, VisualizationPainter painter,
			T dimensionProvider)
	{
		JMenuItem exportIpe = new JMenuItem(new ExportIpeActionZoomed<>(
				component, painter, dimensionProvider));
		menu.add(exportIpe);
	}
}
