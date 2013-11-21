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
package de.topobyte.livecg.core.painting;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.topobyte.livecg.algorithms.frechet.freespace.FreeSpacePainterChains;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.Config;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesPainter;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesTriangulationAlgorithm;
import de.topobyte.livecg.algorithms.polygon.monotonepieces.MonotonePiecesTriangulationPainter;
import de.topobyte.livecg.core.export.SvgExporter;
import de.topobyte.livecg.core.export.TikzExporter;
import de.topobyte.livecg.core.geometry.dcel.DCEL;
import de.topobyte.livecg.core.geometry.dcel.DcelConverter;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CopyUtil;
import de.topobyte.livecg.core.geometry.geom.CopyUtil.PolygonMode;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.datastructures.content.ContentConfig;
import de.topobyte.livecg.datastructures.content.ContentPainter;
import de.topobyte.livecg.datastructures.dcel.DcelConfig;
import de.topobyte.livecg.datastructures.dcel.DcelPainter;
import de.topobyte.livecg.datastructures.dcel.InstanceDcelPainter;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;

public class Test
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException, TransformerException
	{
		ContentReader contentReader = new ContentReader();

		// Geometery, DCEL

		String path1 = "res/presets/Startup.geom";
		Content content1 = contentReader.read(new File(path1));

		File svg1 = new File("/tmp/test1.svg");
		File tikz1 = new File("/tmp/test1.tikz");

		geometry(svg1, tikz1, content1);

		File svg2 = new File("/tmp/test2.svg");
		File tikz2 = new File("/tmp/test2.tikz");

		dcel(svg2, tikz2, content1);

		// Triangulations

		String path2 = "res/presets/polygons/Big.geom";
		Content content2 = contentReader.read(new File(path2));
		Polygon polygon = content2.getPolygons().get(0);

		File svg3 = new File("/tmp/test3.svg");
		File tikz3 = new File("/tmp/test3.tikz");

		monotone(svg3, tikz3, CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));

		File svg4 = new File("/tmp/test4.svg");
		File tikz4 = new File("/tmp/test4.tikz");

		triangulation(svg4, tikz4,
				CopyUtil.copy(polygon, PolygonMode.REUSE_NOTHING));

		// Frechet

		String path3 = "res/presets/frechet/Paper.geom";
		Content content3 = contentReader.read(new File(path3));

		List<Chain> chains = content3.getChains();
		Chain chain1 = chains.get(0);
		Chain chain2 = chains.get(1);

		File svg5 = new File("/tmp/test5.svg");
		File tikz5 = new File("/tmp/test5.tikz");

		freeSpace(svg5, tikz5, chain1, chain2);
	}

	private static void freeSpace(File svg, File tikz, Chain chain1,
			Chain chain2) throws TransformerException, IOException
	{
		de.topobyte.livecg.algorithms.frechet.freespace.Config config = new de.topobyte.livecg.algorithms.frechet.freespace.Config();
		FreeSpacePainterChains freeSpacePainter = new FreeSpacePainterChains(
				config, 100, chain1, chain2, null);

		int width = 400;
		int height = 400;
		SvgExporter.exportSVG(svg, freeSpacePainter, width, height);
		TikzExporter.exportTikz(tikz, freeSpacePainter, width, height);
	}

	private static void geometry(File svg, File tikz, Content content1)
			throws TransformerException, IOException
	{
		Rectangle scene = content1.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		ContentConfig contentConfig = new ContentConfig();
		ContentPainter contentPainter = new ContentPainter(scene, content1,
				contentConfig, null);

		SvgExporter.exportSVG(svg, contentPainter, width, height);
		TikzExporter.exportTikz(tikz, contentPainter, width, height);
	}

	private static void dcel(File svg, File tikz, Content content)
			throws TransformerException, IOException
	{
		Rectangle scene = content.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		DcelConfig dcelConfig = new DcelConfig();
		DCEL dcel = DcelConverter.convert(content);
		DcelPainter dcelPainter = new InstanceDcelPainter(scene, dcel,
				dcelConfig, null);

		SvgExporter.exportSVG(svg, dcelPainter, width, height);
		TikzExporter.exportTikz(tikz, dcelPainter, width, height);
	}

	private static void monotone(File svg, File tikz, Polygon polygon)
			throws TransformerException, IOException
	{
		MonotonePiecesAlgorithm algorithm = new MonotonePiecesAlgorithm(polygon);

		Config polygonConfig = new Config();
		Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(algorithm
				.getExtendedGraph());
		MonotonePiecesPainter triangulationPainter = new MonotonePiecesPainter(
				algorithm, polygonConfig, colorMap, null);

		Rectangle scene = algorithm.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, triangulationPainter, width, height);
		TikzExporter.exportTikz(tikz, triangulationPainter, width, height);
	}

	private static void triangulation(File svg, File tikz, Polygon polygon)
			throws TransformerException, IOException
	{
		MonotonePiecesTriangulationAlgorithm algorithm = new MonotonePiecesTriangulationAlgorithm(
				polygon);

		Config polygonConfig = new Config();
		Map<Polygon, Color> colorMap = ColorMapBuilder.buildColorMap(algorithm
				.getExtendedGraph());
		MonotonePiecesTriangulationPainter triangulationPainter = new MonotonePiecesTriangulationPainter(
				algorithm, polygonConfig, colorMap, null);

		Rectangle scene = algorithm.getScene();
		int width = (int) Math.ceil(scene.getWidth());
		int height = (int) Math.ceil(scene.getHeight());

		SvgExporter.exportSVG(svg, triangulationPainter, width, height);
		TikzExporter.exportTikz(tikz, triangulationPainter, width, height);
	}
}
